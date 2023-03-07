package com.shaw.sso.common;

import java.io.Serializable;

/**
 * @author shaw
 * @date 2022/12/13
 */
public class RefreshTokenContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private AccessTokenContent accessTokenContent;
    private String accessToken;

    public RefreshTokenContent(AccessTokenContent accessTokenContent, String accessToken) {
        this.accessTokenContent = accessTokenContent;
        this.accessToken = accessToken;
    }

    public AccessTokenContent getAccessTokenContent() {
        return accessTokenContent;
    }

    public void setAccessTokenContent(AccessTokenContent accessTokenContent) {
        this.accessTokenContent = accessTokenContent;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
