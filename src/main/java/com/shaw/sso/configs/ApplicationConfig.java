package com.shaw.sso.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author shaw
 * @date 2021/7/12
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class ApplicationConfig {


}
