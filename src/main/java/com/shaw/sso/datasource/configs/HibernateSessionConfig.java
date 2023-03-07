package com.shaw.sso.datasource.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author shaw
 * @date 2021/7/13
 */
@Configuration
public class HibernateSessionConfig {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hbm2ddlAuto;

    @Value("${spring.jpa.show-sql}")
    private String showSql;

    //注入数据源，生成sessionFactory
    @Bean
    public LocalSessionFactoryBean sessionFactory(@Qualifier("dataSource") DataSource dataSource) {
        return buildLocalSessionFactory(dataSource);
    }

    /**
     * 设置Hibernate的配置属性
     *
     * @return
     */
    private Properties getHibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        hibernateProperties.put("current_session_context_class",
                "org.springframework.orm.hibernate5.SpringSessionContext");
        hibernateProperties.put("hibernate.show_sql", showSql);
        hibernateProperties.put("hibernate.format_sql", "false");
        hibernateProperties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        hibernateProperties.put("hibernate.jdbc.fetch_size", "50");
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        return hibernateProperties;
    }

    /**
     * 构建LocalSessionFactoryBean实例
     *
     * @param dataSource 构建实例所使用的的数据源
     * @return
     */
    private LocalSessionFactoryBean buildLocalSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource); // 配置数据源,指定成第一个数据源
        // 如果使用 xml 配置则使用该方法进行包扫描
        //PathMatchingResourcePatternResolver pmprpr = new PathMatchingResourcePatternResolver();
        //Resource[] resource = pmprpr.getResources("classpath*:com/ml/hibernatepro/ml/domain/*.hbm.xml");
        //localSessionFactoryBean.setMappingLocations(resource);

        // 现在配置基本都切换到 java config
        //localSessionFactoryBean.setAnnotatedPackages("classpath*:com/ml/hibernatepro/ml/domain");
        // 添加 Hibernate 配置规则
        localSessionFactoryBean.setHibernateProperties(getHibernateProperties());
        //指定需要扫描的hibernate的Entity实体类包名，可以指定多个包名
        localSessionFactoryBean.setPackagesToScan("com.shaw.sso.domain");
        return localSessionFactoryBean;
    }

}
