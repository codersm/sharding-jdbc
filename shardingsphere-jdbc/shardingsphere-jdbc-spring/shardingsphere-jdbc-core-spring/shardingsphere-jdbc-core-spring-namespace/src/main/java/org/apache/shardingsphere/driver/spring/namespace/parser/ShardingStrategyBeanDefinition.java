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

package org.apache.shardingsphere.driver.spring.namespace.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.sharding.api.config.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.driver.spring.namespace.constants.ShardingStrategyBeanDefinitionParserTag;
import org.apache.shardingsphere.infra.exception.ShardingSphereException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

/**
 * Sharding strategy parser for spring namespace.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShardingStrategyBeanDefinition {
    
    static AbstractBeanDefinition getBeanDefinitionByElement(final Element element) {
        String type = element.getLocalName();
        switch (type) {
            case ShardingStrategyBeanDefinitionParserTag.STANDARD_STRATEGY_ROOT_TAG:
                return getStandardShardingStrategyConfigBeanDefinition(element);
            case ShardingStrategyBeanDefinitionParserTag.COMPLEX_STRATEGY_ROOT_TAG:
                return getComplexShardingStrategyConfigBeanDefinition(element);
            case ShardingStrategyBeanDefinitionParserTag.HINT_STRATEGY_ROOT_TAG:
                return getHintShardingStrategyConfigBeanDefinition(element);
            case ShardingStrategyBeanDefinitionParserTag.NONE_STRATEGY_ROOT_TAG:
                return getNoneShardingStrategyConfigBeanDefinition();
            default:
                throw new ShardingSphereException("Cannot support type: %s", type);
        }
    }
    
    private static AbstractBeanDefinition getStandardShardingStrategyConfigBeanDefinition(final Element element) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(StandardShardingStrategyConfiguration.class);
        factory.addConstructorArgValue(element.getAttribute(ShardingStrategyBeanDefinitionParserTag.SHARDING_COLUMN_ATTRIBUTE));
        factory.addConstructorArgReference(element.getAttribute(ShardingStrategyBeanDefinitionParserTag.ALGORITHM_REF_ATTRIBUTE));
        return factory.getBeanDefinition();
    }
    
    private static AbstractBeanDefinition getComplexShardingStrategyConfigBeanDefinition(final Element element) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ComplexShardingStrategyConfiguration.class);
        factory.addConstructorArgValue(element.getAttribute(ShardingStrategyBeanDefinitionParserTag.SHARDING_COLUMNS_ATTRIBUTE));
        factory.addConstructorArgReference(element.getAttribute(ShardingStrategyBeanDefinitionParserTag.ALGORITHM_REF_ATTRIBUTE));
        return factory.getBeanDefinition();
    }
    
    private static AbstractBeanDefinition getHintShardingStrategyConfigBeanDefinition(final Element element) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(HintShardingStrategyConfiguration.class);
        factory.addConstructorArgReference(element.getAttribute(ShardingStrategyBeanDefinitionParserTag.ALGORITHM_REF_ATTRIBUTE));
        return factory.getBeanDefinition();
    }
    
    private static AbstractBeanDefinition getNoneShardingStrategyConfigBeanDefinition() {
        return BeanDefinitionBuilder.rootBeanDefinition(NoneShardingStrategyConfiguration.class).getBeanDefinition();
    }
}
