package com.shaw.sso.service;

import com.shaw.sso.common.AccessTokenContent;
import com.shaw.sso.common.Constants;
import com.shaw.sso.common.Expiration;
import com.shaw.sso.utils.HttpUtils;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface AccessTokenManagerService extends Expiration {

    /**
     * 生成AccessToken
     *
     * @param accessTokenContent
     * @return
     */
    default String generate(AccessTokenContent accessTokenContent) {
        String accessToken = "AT-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(accessToken, accessTokenContent);
        return accessToken;
    }

    /**
     * 生成AccessToken
     *
     * @param accessToken
     * @param accessTokenContent
     */
    void create(String accessToken, AccessTokenContent accessTokenContent);

    /**
     * 延长AccessToken生命周期
     *
     * @param accessToken
     * @return
     */
    boolean refresh(String accessToken);

    /**
     * 查询
     *
     * @param accessToken
     * @return
     */
    AccessTokenContent get(String accessToken);

    /**
     * 根据TGT删除AccessToken
     *
     * @param tgt
     */
    void remove(String tgt);

    /**
     * 发起客户端登出请求
     *
     * @param redirectUri
     * @param accessToken
     */
    default void sendLogoutRequest(String redirectUri, String accessToken) {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put(Constants.LOGOUT_PARAMETER_NAME, accessToken);
        HttpUtils.postHeader(redirectUri, headerMap);
    }
}
