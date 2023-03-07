package com.shaw.sso.service;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface AppService {

    boolean exists(String appId);

    boolean validate(String appId, String appSecret);
}
