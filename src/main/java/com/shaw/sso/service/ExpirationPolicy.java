package com.shaw.sso.service;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface ExpirationPolicy {

    /**
     * 每5分钟执行一次
     */
    public static final String SCHEDULED_CRON = "0 */5 * * * ?";

    /**
     * 定时清理
     */
    void verifyExpired();

}
