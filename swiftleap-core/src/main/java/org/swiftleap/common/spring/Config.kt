package org.swiftleap.common.spring

import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.*
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@PropertySource(value = ["classpath:core.properties"])
@EnableTransactionManagement
@Configuration("org.swiftleap.common.spring")
@ComponentScan("org.swiftleap")
open class Config {
    private val log = LoggerFactory.getLogger(Config::class.java)
    @Primary
    @Bean
    open fun coreFlyway(
            @Qualifier("coreDataSource") coreDataSource: DataSource,
            @Qualifier("coreDataSourceProperties") props: CustomDataSourceProperties
    ): Flyway? {
        log.info("Config: Flyway Enabled: ${props.flywayEnabled}")
        val flyway = Flyway()
        if (props.flywayEnabled) {
            flyway.isBaselineOnMigrate = true
            flyway.dataSource = coreDataSource
            flyway.repair()
            flyway.migrate()
        }
        return flyway
    }

    @Primary
    @Bean
    @ConfigurationProperties("core.datasource")
    open fun coreDataSourceProperties() = CustomDataSourceProperties()

    @Primary
    @Bean
    open fun coreDataSource(
            @Qualifier("coreDataSourceProperties") props: CustomDataSourceProperties
    ): DataSource = props.initializeDataSourceBuilder().build()

    @Primary
    @Bean
    @DependsOn("coreFlyway")
    open fun coreEntityManagerFactory(
            @Qualifier("coreDataSource") dataSource: DataSource,
            @Qualifier("coreDataSourceProperties") props: CustomDataSourceProperties,
            builder: EntityManagerFactoryBuilder
    ): FactoryBean<EntityManagerFactory> =
            builder
                    .dataSource(dataSource)
                    .packages("com.swiftleap", "org.swiftleap")
                    .properties(props.jpaProperties)
                    .persistenceUnit("corePersistenceUnit")
                    .build()

    @Primary
    @Bean
    open fun coreEntityManager(
            @Qualifier("coreEntityManagerFactory") factory: EntityManagerFactory): EntityManager =
        factory.createEntityManager()
}