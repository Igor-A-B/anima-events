package com.anima.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(basePackages = ["com.anima.features"])
class PersistenceConfig {

    @Bean
    fun dataSource(
        @Value($$"${spring.datasource.url}") dbUrl: String,
        @Value($$"${spring.datasource.username}") dbUser: String,
        @Value($$"${spring.datasource.password}") dbPassword: String,
    ): DataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = dbUrl
        username = dbUser
        password = dbPassword
        driverClassName = "org.postgresql.Driver"
    })

    @Bean
    fun entityManagerFactory(dataSource: DataSource) =
        LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            persistenceProvider = HibernatePersistenceProvider()
            setPackagesToScan("com.anima.features")
            setJpaPropertyMap(
                mapOf(
                    "hibernate.hbm2ddl.auto" to "update",
                    "hibernate.show_sql" to "true",
                )
            )
        }

    @Bean
    fun transactionManager(emf: LocalContainerEntityManagerFactoryBean): PlatformTransactionManager =
        JpaTransactionManager().apply { entityManagerFactory = emf.getObject() }
}