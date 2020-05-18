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

package org.apache.shardingsphere.proxy.backend.communication.jdbc.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Mock data source.
 */
public final class MockDataSource implements DataSource, AutoCloseable {
    
    private final AtomicInteger count = new AtomicInteger(0);
    
    @Override
    public Connection getConnection() throws SQLException {
        if (5 <= count.get()) {
            throw new SQLException("datasource is not enough");
        }
        count.getAndIncrement();
        return new MockConnection();
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection();
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) {
        return false;
    }
    
    @Override
    public PrintWriter getLogWriter() {
        return null;
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) {
    
    }
    
    @Override
    public void setLoginTimeout(final int seconds) {
    
    }
    
    @Override
    public int getLoginTimeout() {
        return 0;
    }
    
    @Override
    public Logger getParentLogger() {
        return null;
    }
    
    @Override
    public void close() {
    }
    
    private final class MockConnection implements Connection {
        
        @Override
        public Statement createStatement() {
            return null;
        }
    
        @Override
        public Statement createStatement(final int resultSetType, final int resultSetConcurrency) {
            return null;
        }
    
        @Override
        public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
            return null;
        }
    
        @Override
        public PreparedStatement prepareStatement(final String sql) {
            return null;
        }
    
        @Override
        public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) {
            return null;
        }
    
        @Override
        public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) {
            return null;
        }
    
        @Override
        public PreparedStatement prepareStatement(final String sql, final String[] columnNames) {
            return null;
        }
    
        @Override
        public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
            return null;
        }
    
        @Override
        public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) {
            return null;
        }
    
        @Override
        public CallableStatement prepareCall(final String sql) {
            return null;
        }
    
        @Override
        public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) {
            return null;
        }
    
        @Override
        public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
            return null;
        }
    
        @Override
        public String nativeSQL(final String sql) {
            return null;
        }
    
        @Override
        public void setAutoCommit(final boolean autoCommit) {
        
        }
    
        @Override
        public boolean getAutoCommit() {
            return false;
        }
    
        @Override
        public void commit() {
        
        }
    
        @Override
        public void rollback() {
        
        }
    
        @Override
        public void rollback(final Savepoint savepoint) {
        
        }
    
        @Override
        public void close() {
            count.getAndDecrement();
        }
    
        @Override
        public boolean isClosed() {
            return false;
        }
    
        @Override
        public DatabaseMetaData getMetaData() {
            return null;
        }
    
        @Override
        public void setReadOnly(final boolean readOnly) {
        
        }
    
        @Override
        public boolean isReadOnly() {
            return false;
        }
    
        @Override
        public void setCatalog(final String catalog) {
        
        }
    
        @Override
        public String getCatalog() {
            return null;
        }
    
        @Override
        public void setTransactionIsolation(final int level) {
        
        }
    
        @Override
        public int getTransactionIsolation() {
            return Connection.TRANSACTION_NONE;
        }
    
        @Override
        public SQLWarning getWarnings() {
            return null;
        }
    
        @Override
        public void clearWarnings() {
        
        }
        
        @Override
        public Map<String, Class<?>> getTypeMap() {
            return null;
        }
    
        @Override
        public void setTypeMap(final Map<String, Class<?>> map) {
        
        }
    
        @Override
        public void setHoldability(final int holdability) {
        
        }
    
        @Override
        public int getHoldability() {
            return 0;
        }
    
        @Override
        public Savepoint setSavepoint() {
            return null;
        }
    
        @Override
        public Savepoint setSavepoint(final String name) {
            return null;
        }
    
        @Override
        public void releaseSavepoint(final Savepoint savepoint) {
        }
    
        @Override
        public Clob createClob() {
            return null;
        }
    
        @Override
        public Blob createBlob() {
            return null;
        }
    
        @Override
        public NClob createNClob() {
            return null;
        }
    
        @Override
        public SQLXML createSQLXML() {
            return null;
        }
    
        @Override
        public boolean isValid(final int timeout) {
            return false;
        }
    
        @Override
        public void setClientInfo(final String name, final String value) {
        
        }
    
        @Override
        public void setClientInfo(final Properties properties) {
        }
    
        @Override
        public String getClientInfo(final String name) {
            return null;
        }
    
        @Override
        public Properties getClientInfo() {
            return null;
        }
    
        @Override
        public Array createArrayOf(final String typeName, final Object[] elements) {
            return null;
        }
    
        @Override
        public Struct createStruct(final String typeName, final Object[] attributes) {
            return null;
        }
    
        @Override
        public void setSchema(final String schema) {
        }
    
        @Override
        public String getSchema() {
            return null;
        }
    
        @Override
        public void abort(final Executor executor) {
        }
    
        @Override
        public void setNetworkTimeout(final Executor executor, final int milliseconds) {
        }
    
        @Override
        public int getNetworkTimeout() {
            return 0;
        }
    
        @Override
        public <T> T unwrap(final Class<T> iface) {
            return null;
        }
    
        @Override
        public boolean isWrapperFor(final Class<?> iface) {
            return false;
        }
    }
}
