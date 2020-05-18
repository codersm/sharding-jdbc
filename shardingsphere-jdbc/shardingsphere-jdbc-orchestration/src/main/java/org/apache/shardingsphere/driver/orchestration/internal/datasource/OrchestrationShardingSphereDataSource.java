/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.driver.orchestration.internal.datasource;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.driver.orchestration.internal.util.DataSourceConverter;
import org.apache.shardingsphere.infra.config.DataSourceConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.database.DefaultSchema;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.infra.rule.StatusContainedRule;
import org.apache.shardingsphere.infra.rule.event.impl.DataSourceNameDisabledEvent;
import org.apache.shardingsphere.orchestration.center.config.OrchestrationConfiguration;
import org.apache.shardingsphere.orchestration.core.common.event.DataSourceChangedEvent;
import org.apache.shardingsphere.orchestration.core.common.event.PropertiesChangedEvent;
import org.apache.shardingsphere.orchestration.core.common.event.RuleConfigurationsChangedEvent;
import org.apache.shardingsphere.orchestration.core.configcenter.ConfigCenter;
import org.apache.shardingsphere.orchestration.core.facade.ShardingOrchestrationFacade;
import org.apache.shardingsphere.orchestration.core.metadatacenter.event.MetaDataChangedEvent;
import org.apache.shardingsphere.orchestration.core.registrycenter.event.DisabledStateChangedEvent;
import org.apache.shardingsphere.orchestration.core.registrycenter.schema.OrchestrationSchema;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Orchestration ShardingSphere data source.
 */
@Getter(AccessLevel.PROTECTED)
public class OrchestrationShardingSphereDataSource extends AbstractOrchestrationDataSource {
    
    private ShardingSphereDataSource dataSource;
    
    public OrchestrationShardingSphereDataSource(final OrchestrationConfiguration orchestrationConfig) throws SQLException {
        super(new ShardingOrchestrationFacade(orchestrationConfig, Collections.singletonList(DefaultSchema.LOGIC_NAME)));
        ConfigCenter configService = getShardingOrchestrationFacade().getConfigCenter();
        Collection<RuleConfiguration> configurations = configService.loadRuleConfigurations(DefaultSchema.LOGIC_NAME);
        Preconditions.checkState(null != configurations && !configurations.isEmpty(), "Missing the sharding rule configuration on registry center");
        Map<String, DataSourceConfiguration> dataSourceConfigurations = configService.loadDataSourceConfigurations(DefaultSchema.LOGIC_NAME);
        dataSource = new ShardingSphereDataSource(DataSourceConverter.getDataSourceMap(dataSourceConfigurations), configurations, configService.loadProperties());
        initShardingOrchestrationFacade();
        persistMetaData(dataSource.getRuntimeContext().getMetaData().getSchema());
    }
    
    public OrchestrationShardingSphereDataSource(final ShardingSphereDataSource shardingSphereDataSource, final OrchestrationConfiguration orchestrationConfig) {
        super(new ShardingOrchestrationFacade(orchestrationConfig, Collections.singletonList(DefaultSchema.LOGIC_NAME)));
        dataSource = shardingSphereDataSource;
        initShardingOrchestrationFacade(Collections.singletonMap(DefaultSchema.LOGIC_NAME, DataSourceConverter.getDataSourceConfigurationMap(dataSource.getDataSourceMap())),
                getRuleConfigurationMap(), dataSource.getRuntimeContext().getProperties().getProps());
        persistMetaData(dataSource.getRuntimeContext().getMetaData().getSchema());
    }
    
    private Map<String, Collection<RuleConfiguration>> getRuleConfigurationMap() {
        Map<String, Collection<RuleConfiguration>> result = new LinkedHashMap<>(1, 1);
        result.put(DefaultSchema.LOGIC_NAME, dataSource.getRuntimeContext().getConfigurations());
        return result;
    }
    
    /**
     * Renew meta data of the schema.
     *
     * @param event meta data changed event.
     */
    @Subscribe
    public final synchronized void renew(final MetaDataChangedEvent event) {
        for (String each : event.getSchemaNames()) {
            if (DefaultSchema.LOGIC_NAME.equals(each)) {
                dataSource.getRuntimeContext().setMetaData(new ShardingSphereMetaData(dataSource.getRuntimeContext().getMetaData().getDataSources(), event.getRuleSchemaMetaData()));
            }
        }
    }
    
    /**
     * Renew sharding rule.
     *
     * @param ruleConfigurationsChangedEvent rule configurations changed event
     */
    @Subscribe
    @SneakyThrows
    public final synchronized void renew(final RuleConfigurationsChangedEvent ruleConfigurationsChangedEvent) {
        dataSource = new ShardingSphereDataSource(dataSource.getDataSourceMap(), ruleConfigurationsChangedEvent.getRuleConfigurations(), dataSource.getRuntimeContext().getProperties().getProps());
    }
    
    /**
     * Renew sharding data source.
     *
     * @param dataSourceChangedEvent data source changed event
     */
    @Subscribe
    @SneakyThrows
    public final synchronized void renew(final DataSourceChangedEvent dataSourceChangedEvent) {
        Map<String, DataSourceConfiguration> dataSourceConfigurations = dataSourceChangedEvent.getDataSourceConfigurations();
        dataSource.close(getDeletedDataSources(dataSourceConfigurations));
        dataSource.close(getModifiedDataSources(dataSourceConfigurations).keySet());
        dataSource = new ShardingSphereDataSource(getChangedDataSources(dataSource.getDataSourceMap(), dataSourceConfigurations), 
                dataSource.getRuntimeContext().getConfigurations(), dataSource.getRuntimeContext().getProperties().getProps());
        getDataSourceConfigurations().clear();
        getDataSourceConfigurations().putAll(dataSourceConfigurations);
    }
    
    /**
     * Renew properties.
     *
     * @param propertiesChangedEvent properties changed event
     */
    @SneakyThrows
    @Subscribe
    public final synchronized void renew(final PropertiesChangedEvent propertiesChangedEvent) {
        dataSource = new ShardingSphereDataSource(dataSource.getDataSourceMap(), dataSource.getRuntimeContext().getConfigurations(), propertiesChangedEvent.getProps());
    }
    
    /**
     * Renew disabled data source names.
     *
     * @param disabledStateChangedEvent disabled state changed event
     */
    @Subscribe
    public synchronized void renew(final DisabledStateChangedEvent disabledStateChangedEvent) {
        OrchestrationSchema orchestrationSchema = disabledStateChangedEvent.getOrchestrationSchema();
        if (DefaultSchema.LOGIC_NAME.equals(orchestrationSchema.getSchemaName())) {
            for (ShardingSphereRule each : dataSource.getRuntimeContext().getRules()) {
                if (each instanceof StatusContainedRule) {
                    ((StatusContainedRule) each).updateRuleStatus(new DataSourceNameDisabledEvent(orchestrationSchema.getDataSourceName(), disabledStateChangedEvent.isDisabled()));
                }
            }
        }
    }
}
