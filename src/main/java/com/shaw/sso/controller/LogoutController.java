package com.shaw.sso.controller;

import com.shaw.sso.common.Constants;
import com.shaw.sso.session.SessionManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shaw
 * @date 2022/12/14
 */
@Controller
public class LogoutController extends BaseController {

    @Resource(name = "sessionManager")
    private SessionManager sessionManager;

    /**
     * 登出
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@RequestParam(value = Constants.REDIRECT_URI, required = false) String redirectUri,
                         HttpServletRequest request, HttpServletResponse response) {
        sessionManager.invalidate(request, response);
        return "redirect:" + redirectUri;
    }

}
