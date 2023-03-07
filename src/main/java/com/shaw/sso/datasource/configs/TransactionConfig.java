package com.shaw.sso.datasource.configs;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;

/**
 * @author shaw
 * @date 2021/7/13
 */
@Configuration
public class TransactionConfig implements TransactionManagementConfigurer {

    //注入基于数据源生成的会话工厂
    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    // 事务管理交给 HibernateTransactionManager
    //基于数据源的事务管理
    @Bean("transactionManager")
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory);
        return hibernateTransactionManager;
    }

    //实现接口 TransactionManagementConfigurer 方法，其返回值代表默认使用的事务管理器
    //注意，此处返回的事务管理器就是@Transactional的默认值，如果不返回则需要指明@Transactional使用的事务管理器名称
    //多事务管理器时指明@Transactional(value="transactionManager"),则代表使用的那个事务
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }

}
