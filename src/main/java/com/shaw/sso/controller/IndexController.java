package com.shaw.sso.controller;

import com.shaw.sso.common.Constants;
import com.shaw.sso.rpc.SsoUser;
import com.shaw.sso.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author shaw
 * @date 2022/12/12
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${sso.server.url}")
    private String serverUrl;

    /**
     * 初始页
     *
     * @param request
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping
    public String execute(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        SsoUser user = SessionUtils.getUser(request);
        model.addAttribute("userName", user != null ? user.getUsername() : null);// 设置登录用户名
        model.addAttribute("serverPort", serverPort); // 当前服务端口号
        model.addAttribute("sessionId", request.getSession().getId());  // 当前sessionId
        model.addAttribute("logoutUrl", serverUrl + Constants.LOGOUT_URL + "?" + Constants.REDIRECT_URI + "="
                + URLEncoder.encode(getLocalUrl(request), "utf-8"));  // 单点退出地址
        return "redirect:login";
    }

    /**
     * 获取当前应用访问路径
     *
     * @param request
     * @return
     */
    private String getLocalUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url.append(":").append(request.getServerPort());
        }
        url.append(request.getContextPath());
        return url.toString();
    }
}
