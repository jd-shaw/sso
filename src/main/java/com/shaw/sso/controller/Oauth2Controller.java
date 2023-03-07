package com.shaw.sso.controller;

import com.shaw.sso.common.AccessTokenContent;
import com.shaw.sso.common.CodeContent;
import com.shaw.sso.common.Constants;
import com.shaw.sso.common.JResult;
import com.shaw.sso.common.RefreshTokenContent;
import com.shaw.sso.domain.Account;
import com.shaw.sso.enums.GrantTypeEnum;
import com.shaw.sso.rpc.RpcAccessToken;
import com.shaw.sso.rpc.SsoUser;
import com.shaw.sso.service.AccessTokenManagerService;
import com.shaw.sso.service.AccountService;
import com.shaw.sso.service.AppService;
import com.shaw.sso.service.CodeManagerService;
import com.shaw.sso.service.RefreshTokenManagerService;
import com.shaw.sso.service.TicketManagerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author shaw
 * @date 2022/12/14
 */
@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller extends BaseController {

    @Resource(name = "sso.AccountService")
    private AccountService accountService;

    @Resource(name = "sso.AppService")
    private AppService appService;

    @Resource(name = "sso.LocalCodeManagerService")
    private CodeManagerService localCodeManagerService;

    @Resource(name = "sso.LocalTicketManagerService")
    private TicketManagerService ticketManagerService;

    @Resource(name = "sso.LocalAccessTokenManagerService")
    private AccessTokenManagerService localAccessTokenManagerService;

    @Resource(name = "sso.LocalRefreshTokenManagerService")
    private RefreshTokenManagerService localRefreshTokenManagerService;

    /**
     * 获取accessToken
     *
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    @RequestMapping(value = "/access-token", method = RequestMethod.GET)
    public JResult getAccessToken(
            @RequestParam(value = Constants.GRANT_TYPE) String grantType,
            @RequestParam(value = Constants.APP_ID) String appId,
            @RequestParam(value = Constants.APP_SECRET) String appSecret,
            @RequestParam(value = Constants.AUTH_CODE, required = false) String code,
            @RequestParam(value = Constants.USERNAME, required = false) String username,
            @RequestParam(value = Constants.PASSWORD, required = false) String password) {

        // 校验基本参数
        JResult result = validateParam(grantType, code, username, password);
        if (result.isSuccess()) {
            // 校验应用
            boolean appResult = appService.validate(appId, appSecret);
            if (!appResult) {
                return JResult.createWarnMessage("无效appId!");
            }

            // 校验授权
            JResult accessTokenResult = validateAuth(grantType, code, username, password, appId);
            if (!accessTokenResult.isSuccess()) {
                return accessTokenResult;
            }

            // 生成RpcAccessToken返回
            return JResult.createSuccess(generateRpcAccessToken((AccessTokenContent) accessTokenResult.getResult(), null));
        } else {
            return result;
        }
    }

    private JResult validateParam(String grantType, String code, String username, String password) {
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(code)) {
                return JResult.createWarnMessage("code不能为空");
            }
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return JResult.createWarnMessage("username和password不能为空");
            }
        } else {
            return JResult.createWarnMessage("授权方式不支持");
        }
        return JResult.SUCCESS;
    }

    private JResult validateAuth(String grantType, String code, String username, String password,
                                 String appId) {
        AccessTokenContent authDto = null;
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            CodeContent codeContent = localCodeManagerService.getAndRemove(code);
            if (codeContent == null) {
                return JResult.createWarnMessage("code有误或已过期");
            }

            SsoUser user = ticketManagerService.getAndRefresh(codeContent.getTgt());
            if (user == null) {
                return JResult.createWarnMessage("服务端session已过期");
            }
            authDto = new AccessTokenContent(codeContent, user, appId);
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            // app通过此方式由客户端代理转发http请求到服务端获取accessToken
            Account account = accountService.getByLoginNameOrPhoneNumber(username);
            if (account != null) {
                boolean loginResult = accountService.login(username, password, account);
                if (!loginResult) {
                    return JResult.createWarnMessage("登录失败！请检查账户密码！");
                }
                SsoUser user = new SsoUser(account.getPhoneNumber(), account.getLoginName());
                String tgt = ticketManagerService.generate(user);
                CodeContent codeContent = new CodeContent(tgt, false, null);
                authDto = new AccessTokenContent(codeContent, user, appId);
            } else {
                return JResult.createWarnMessage("不存在对应账户信息！");
            }
        }
        return JResult.createSuccess(authDto);
    }

    /**
     * 刷新accessToken，并延长TGT超时时间
     * <p>
     * accessToken刷新结果有两种：
     * 1. 若accessToken已超时，那么进行refreshToken会生成一个新的accessToken，新的超时时间；
     * 2. 若accessToken未超时，那么进行refreshToken不会改变accessToken，但超时时间会刷新，相当于续期accessToken。
     *
     * @param appId
     * @param refreshToken
     * @return
     */
    @RequestMapping(value = "/refresh_token", method = RequestMethod.GET)
    public JResult refreshToken(
            @RequestParam(value = Constants.APP_ID) String appId,
            @RequestParam(value = Constants.REFRESH_TOKEN) String refreshToken) {
        if (!appService.exists(appId)) {
            return JResult.createWarnMessage("非法应用");
        }

        RefreshTokenContent refreshTokenContent = localRefreshTokenManagerService.validate(refreshToken);
        if (refreshTokenContent == null) {
            return JResult.createWarnMessage("refreshToken有误或已过期");
        }
        AccessTokenContent accessTokenContent = refreshTokenContent.getAccessTokenContent();
        if (!appId.equals(accessTokenContent.getAppId())) {
            return JResult.createWarnMessage("非法应用");
        }
        SsoUser user = ticketManagerService.getAndRefresh(accessTokenContent.getCodeContent().getTgt());
        if (user == null) {
            return JResult.createWarnMessage("服务端session已过期");
        }

        return JResult.createSuccess(generateRpcAccessToken(accessTokenContent, refreshTokenContent.getAccessToken()));
    }

    private RpcAccessToken generateRpcAccessToken(AccessTokenContent accessTokenContent, String accessToken) {
        String newAccessToken = accessToken;
        if (newAccessToken == null || !localAccessTokenManagerService.refresh(newAccessToken)) {
            newAccessToken = localAccessTokenManagerService.generate(accessTokenContent);
        }
        String refreshToken = localRefreshTokenManagerService.generate(accessTokenContent, newAccessToken);

        return new RpcAccessToken(newAccessToken, localAccessTokenManagerService.getExpiresIn(), refreshToken,
                accessTokenContent.getUser());
    }

}
