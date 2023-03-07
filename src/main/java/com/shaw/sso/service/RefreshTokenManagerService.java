package com.shaw.sso.service;

import com.shaw.sso.common.AccessTokenContent;
import com.shaw.sso.common.Expiration;
import com.shaw.sso.common.RefreshTokenContent;

import java.util.UUID;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface RefreshTokenManagerService extends Expiration {

    /**
     * 生成refreshToken
     *
     * @param accessTokenContent
     * @param accessToken
     * @return
     */
    default String generate(AccessTokenContent accessTokenContent, String accessToken) {
        String resfreshToken = "RT-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(resfreshToken, new RefreshTokenContent(accessTokenContent, accessToken));
        return resfreshToken;
    }

    /**
     * 生成refreshToken
     *
     * @param refreshToken
     * @param refreshTokenContent
     */
    void create(String refreshToken, RefreshTokenContent refreshTokenContent);

    /**
     * 验证refreshToken有效性，无论有效性与否，都remove掉
     *
     * @param refreshToken
     * @return
     */
    RefreshTokenContent validate(String refreshToken);
}
