package com.shaw.sso.controller;

import com.shaw.sso.common.AppConstant;
import com.shaw.sso.common.Constants;
import com.shaw.sso.common.JResult;
import com.shaw.sso.domain.Account;
import com.shaw.sso.rpc.SsoUser;
import com.shaw.sso.service.AccountService;
import com.shaw.sso.service.AppService;
import com.shaw.sso.service.CodeManagerService;
import com.shaw.sso.session.SessionManager;
import com.shaw.sso.utils.RandomUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

/**
 * @author shaw
 * @date 2022/12/12
 */
@Controller
public class LoginController extends BaseController {

    private final static String IDENTIFYING_CODE_SESSION_KEY = "identifying-code";

    @Resource(name = "sso.AccountService")
    private AccountService accountService;

    @Resource(name = "sso.AppService")
    private AppService appService;

    @Resource(name = "sessionManager")
    private SessionManager sessionManager;

    @Resource(name = "sso.LocalCodeManagerService")
    private CodeManagerService LocalCodeManagerService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(
            @RequestParam(value = Constants.REDIRECT_URI) String redirectUri,
            @RequestParam(value = Constants.APP_ID) String appId,
            HttpServletRequest request) throws UnsupportedEncodingException {
        String tgt = sessionManager.getTgt(request);
        if (StringUtils.isEmpty(tgt)) {
            return goLoginPath(redirectUri, appId, request);
        }
        return generateCodeAndRedirect(redirectUri, tgt);
    }

    @ResponseBody
    @RequestMapping("/get-identifying-code")
    public JResult login(@RequestParam("loginNameOrPhoneNumber") String loginNameOrPhoneNumber,
                         HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            IdentifyingCode identifyingCode = (IdentifyingCode) session.getAttribute(IDENTIFYING_CODE_SESSION_KEY);
            if (identifyingCode != null) {
                if (!new Date().after(DateUtils.addMinutes(identifyingCode.getCreateDate(), 1))) {
                    return JResult.createWarnMessage("请勿频繁获取验证码！");
                }
            }

            Account account = accountService.getByLoginNameOrPhoneNumber(loginNameOrPhoneNumber);
            if (account != null) {
                if (StringUtils.isNotEmpty(account.getPhoneNumber())) {
                    String code = RandomUIDUtils.getNumberUID(6);
                    boolean isSend = sendSMS(account.getPhoneNumber(), code);
                    logger.info("手机号：{},code:{}", account.getPhoneNumber(), code);
                    if (isSend) {
                        session.setAttribute(IDENTIFYING_CODE_SESSION_KEY,
                                new IdentifyingCode(account.getPhoneNumber(), code));
                    }
                } else {
                    return JResult.createWarnMessage("无此用户[" + loginNameOrPhoneNumber + "]手机号码！");
                }
            } else {
                return JResult.createWarnMessage("无此用户[" + loginNameOrPhoneNumber + "]信息！");
            }
        }
        return JResult.SUCCESS;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(
            @RequestParam(value = Constants.REDIRECT_URI) String redirectUri,
            @RequestParam(value = Constants.APP_ID) String appId,
            @RequestParam("userName") String userName, @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        if (!appService.exists(appId)) {
            request.setAttribute("errorMessage", "非法应用");
            return goLoginPath(redirectUri, appId, request);
        }

        Account account = accountService.getByLoginNameOrPhoneNumber(userName);
        if (account != null) {
            HttpSession session = request.getSession();
            IdentifyingCode identifyingCode = (IdentifyingCode) session
                    .getAttribute(IDENTIFYING_CODE_SESSION_KEY);
            boolean login = identifyingCode != null
                    && !new Date().after(DateUtils.addMinutes(identifyingCode.getCreateDate(), 2))
                    && StringUtils.equals(identifyingCode.getPhoneNumber(), account.getPhoneNumber());
            if (!login)
                login = accountService.login(account.getId(), password, account);

            if (!login) {
                request.setAttribute("errorMessage", "登录失败！");
                return goLoginPath(redirectUri, appId, request);
            }
            String tgt = sessionManager.setUser(new SsoUser(account.getPhoneNumber(), account.getLoginName()), request, response);
            return generateCodeAndRedirect(redirectUri, tgt);
        }
        request.setAttribute("errorMessage", "用户获取失败!");
        return goLoginPath(redirectUri, appId, request);
    }

    private String generateCodeAndRedirect(String redirectUri, String tgt) throws UnsupportedEncodingException {
        // 生成授权码
        String code = LocalCodeManagerService.generate(tgt, true, redirectUri);
        return "redirect:" + authRedirectUri(redirectUri, code);
    }

    private String authRedirectUri(String redirectUri, String code) throws UnsupportedEncodingException {
        StringBuilder sbf = new StringBuilder(redirectUri);
        if (redirectUri.contains("?")) {
            sbf.append("&");
        } else {
            sbf.append("?");
        }
        sbf.append(Constants.AUTH_CODE).append("=").append(code);
        return URLDecoder.decode(sbf.toString(), "utf-8");
    }

    private String goLoginPath(String redirectUri, String appId, HttpServletRequest request) {
        request.setAttribute(Constants.REDIRECT_URI, redirectUri);
        request.setAttribute(Constants.APP_ID, appId);
        return AppConstant.LOGIN_PATH;
    }

    private boolean sendSMS(String phoneNumber, String code) {
        // TODO 补全短信发送功能
        return true;
    }

    public static class IdentifyingCode implements Serializable {
        private static final long serialVersionUID = 1L;
        private String phoneNumber;
        private String identifyingCode;
        private Date createDate;

        public IdentifyingCode() {
        }

        public IdentifyingCode(String phoneNumber, String identifyingCode) {
            this.phoneNumber = phoneNumber;
            this.identifyingCode = identifyingCode;
            this.createDate = new Date();
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getIdentifyingCode() {
            return identifyingCode;
        }

        public void setIdentifyingCode(String identifyingCode) {
            this.identifyingCode = identifyingCode;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

    }
}
