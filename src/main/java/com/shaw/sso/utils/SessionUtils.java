package com.shaw.sso.utils;

import com.shaw.sso.common.Constants;
import com.shaw.sso.rpc.RpcAccessToken;
import com.shaw.sso.rpc.SsoUser;
import com.shaw.sso.session.SessionAccessToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author shaw
 * @date 2022/12/13
 */
public class SessionUtils {

    public static SessionAccessToken getAccessToken(HttpServletRequest request) {
        return (SessionAccessToken) request.getSession().getAttribute(Constants.SESSION_ACCESS_TOKEN);
    }

    public static SsoUser getUser(HttpServletRequest request) {
        return Optional.ofNullable(getAccessToken(request)).map(RpcAccessToken::getUser).orElse(null);
    }

    public static String getUserId(HttpServletRequest request) {
        return Optional.ofNullable(getUser(request)).map(SsoUser::getId).orElse(null);
    }

    public static void setAccessToken(HttpServletRequest request, RpcAccessToken rpcAccessToken) {
        SessionAccessToken sessionAccessToken = null;
        if (rpcAccessToken != null) {
            sessionAccessToken = createSessionAccessToken(rpcAccessToken);
        }
        request.getSession().setAttribute(Constants.SESSION_ACCESS_TOKEN, sessionAccessToken);
    }

    private static SessionAccessToken createSessionAccessToken(RpcAccessToken accessToken) {
        long expirationTime = System.currentTimeMillis() + accessToken.getExpiresIn() * 1000L;
        return new SessionAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn(),
                accessToken.getRefreshToken(), accessToken.getUser(), expirationTime);
    }

    public static void invalidate(HttpServletRequest request) {
        setAccessToken(request, null);
        request.getSession().invalidate();
    }
}
