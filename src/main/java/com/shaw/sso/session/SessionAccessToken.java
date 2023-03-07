package com.shaw.sso.session;

import com.shaw.sso.rpc.RpcAccessToken;
import com.shaw.sso.rpc.SsoUser;

/**
 * @author shaw
 * @date 2022/12/13
 */
public class SessionAccessToken extends RpcAccessToken {

    private static final long serialVersionUID = 1L;

    /**
     * AccessToken过期时间
     */
    private long expirationTime;

    public SessionAccessToken(String accessToken, int expiresIn, String refreshToken, SsoUser user,
                              long expirationTime) {
        super(accessToken, expiresIn, refreshToken, user);
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}
