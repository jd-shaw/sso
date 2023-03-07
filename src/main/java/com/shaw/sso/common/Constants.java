package com.shaw.sso.common;

/**
 * @author shaw
 * @date 2022/12/12
 */
public class Constants {

    public static final String ENABLE = "1";
    public static final String DISABLE = "0";
    public static final String DOT = ".";

    /**
     * 服务端登录地址
     */
    public static final String LOGIN_URL = "/login";

    /**
     * 服务端登出地址
     */
    public static final String LOGOUT_URL = "/logout";

    /**
     * 服务端回调客户端地址参数名称
     */
    public static final String REDIRECT_URI = "redirectUri";

    /**
     * 服务端单点登出回调客户端登出参数名称
     */
    public static final String LOGOUT_PARAMETER_NAME = "logoutRequest";

    /**
     * 本地session中的AccessToken信息
     */
    public static final String SESSION_ACCESS_TOKEN = "_sessionAccessToken";

    /**
     * 模糊匹配后缀
     */
    public static final String URL_FUZZY_MATCH = "/*";

    /**
     * 未登录或已过期
     */
    public static final int NO_LOGIN = 2100;

    /**
     * 应用识别唯一
     */
    public static final String APP_ID = "appId";

    /**
     * 授权码（授权码模式）
     */
    public static final String AUTH_CODE = "code";

    /**
     * 用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止 csrf 攻击
     */
    public static final String STATE = "state";

    /**
     * 授权方式
     */
    public static final String GRANT_TYPE = "grantType";


    /**
     * 应用密钥
     */
    public static final String APP_SECRET = "appSecret";

    /**
     * 刷新token
     */
    public static final String REFRESH_TOKEN = "refreshToken";


    /**
     * 用户名（密码模式）
     */
    public static final String USERNAME = "username";

    /**
     * 密码（密码模式）
     */
    public static final String PASSWORD = "password";

    /**
     * 获取accessToken地址
     */
    public static final String ACCESS_TOKEN_URL = "/oauth2/access_token";

    /**
     * 刷新accessToken地址
     */
    public static final String REFRESH_TOKEN_URL = "/oauth2/refresh_token";
}
