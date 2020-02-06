/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.common.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.hsqldb.jdbc.pool.JDBCXADataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.swiftleap.common.persistance.atomikos.AtomikosJtaPlatform;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.HashMap;


//https://developer.jboss.org/wiki/JBossTransactionsWithSpring
//https://developer.jboss.org/thread/213081
@EnableTransactionManagement
public class AbstractConfig {
    private static final Logger log = LoggerFactory.getLogger(AbstractConfig.class);

    @Value("${core.datasource.driver-class-name}")
    String driverClassName;
    @Value("${core.datasource.class-name}")
    String dataSourceClassName;
    @Value("${core.datasource.URL}")
    String url;
    @Value("${core.datasource.user}")
    String user;
    @Value("${core.datasource.password}")
    String password;
    @Value("${core.datasource.flyway.enabled}")
    boolean flywayEnabled;
    @Autowired
    UserTransaction userTransaction;
    @Autowired
    TransactionManager transactionManager;
    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        return mapper;
    }

    @Bean
    public Flyway coreFlyway(DataSource coreDataSource) {
        log.info("Config: Flyway Enabled: " + flywayEnabled);

        Flyway flyway = new Flyway();
        if (flywayEnabled) {
            flyway.setBaselineOnMigrate(true);
            flyway.setDataSource(coreDataSource);
            flyway.repair();
            flyway.migrate();
        }
        return flyway;
    }

    @Primary
    @Bean(name = "coreDataSource", initMethod = "init", destroyMethod = "close")
    public DataSource coreDataSource() throws Exception {
        log.info("Config: Connecting to DB: " + url);
        log.info("Config: Driver class: " + driverClassName);
        log.info("Config: Datasource class: " + dataSourceClassName);


        XADataSource dataSource = (XADataSource) getClass()
                .getClassLoader()
                .loadClass(dataSourceClassName)
                .newInstance();

        String testQuery = "SELECT 1";

        if (dataSource instanceof JdbcDataSource) {
            JdbcDataSource ds = (JdbcDataSource) dataSource;
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(password);
        }

        if (dataSource instanceof MysqlXADataSource) {
            MysqlXADataSource ds = (MysqlXADataSource) dataSource;
            ds.setPinGlobalTxToPhysicalConnection(true);
            ds.setURL(url);
            ds.setUser(user);
            ds.setUseSSL(false);
            ds.setPassword(password);
            ds.setAutoReconnectForConnectionPools(true);
            ds.setAllowPublicKeyRetrieval(true);
        }
        if (dataSource instanceof JDBCXADataSource) {
            JDBCXADataSource ds = (JDBCXADataSource) dataSource;
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(password);
            testQuery = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
        }


        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName("xaCoreDataSource");
        xaDataSource.setMaxPoolSize(50);
        xaDataSource.setTestQuery(testQuery);
        return xaDataSource;
    }

    @Primary
    @Bean
    public EntityManager coreEntityManager(
            @Qualifier("coreEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    @Primary
    @Bean
    @DependsOn({"coreFlyway"})
    public FactoryBean<EntityManagerFactory> coreEntityManagerFactory(
            @Qualifier("coreDataSource") DataSource coreDataSource) {

        AtomikosJtaPlatform.userTransaction = userTransaction;
        AtomikosJtaPlatform.transactionManager = transactionManager;


        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJtaDataSource(coreDataSource);
        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
        entityManager.setPackagesToScan("org.swiftleap", "org.swiftleap");
        entityManager.setPersistenceUnitName("corePersistenceUnit");
        entityManager.setJpaPropertyMap(properties);
        return entityManager;

    }
}
