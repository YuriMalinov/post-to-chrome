package ru.smarty.post_to_chrome

import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.spring.DBIFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import javax.sql.DataSource

Configuration
EnableWebMvcSecurity
open public class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        http!!
        http.authorizeRequests()
                .antMatchers("/shutdown").hasIpAddress("127.0.0.1")
                .anyRequest().permitAll()
    }
}

Configuration
open public class DbConfig {
    Bean
    open fun jdbi(dataSource: DataSource): DBI {
        val factory = DBIFactoryBean()
        factory.setDataSource(dataSource)

        val dbi = factory.getObject() as DBI
        dbi.registerMapper(KotlinDataMapperFactory())

        return dbi
    }
}