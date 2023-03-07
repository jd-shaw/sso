package com.shaw.sso.session;

import com.shaw.sso.common.AppConstant;
import com.shaw.sso.rpc.SsoUser;
import com.shaw.sso.service.AccessTokenManagerService;
import com.shaw.sso.service.TicketManagerService;
import com.shaw.sso.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shaw
 * @date 2022/12/13
 */
@Component
public class SessionManager {

	@Resource(name="sso.LocalAccessTokenManagerService")
	private AccessTokenManagerService localAccessTokenManagerService;

	@Resource(name="sso.LocalTicketManagerService")
	private TicketManagerService localTicketManagerService;

	public String setUser(SsoUser user, HttpServletRequest request, HttpServletResponse response) {
		String tgt = getCookieTgt(request);
		if (org.apache.commons.lang3.StringUtils.isEmpty(tgt)) {// cookie中没有
			tgt = localTicketManagerService.generate(user);

			// TGT存cookie，和Cas登录保存cookie中名称一致为：TGC
			CookieUtils.addCookie(AppConstant.TGC, tgt, "/", request, response);
		}
		else if(localTicketManagerService.getAndRefresh(tgt) == null){
			localTicketManagerService.create(tgt, user);
		}
		else {
			localTicketManagerService.set(tgt, user);
		}
		return tgt;
	}

	public SsoUser getUser(HttpServletRequest request) {
		String tgt = getCookieTgt(request);
		if (StringUtils.isEmpty(tgt)) {
			return null;
		}
		return localTicketManagerService.getAndRefresh(tgt);
	}

	public void invalidate(HttpServletRequest request, HttpServletResponse response) {
		String tgt = getCookieTgt(request);
		if (StringUtils.isEmpty(tgt)) {
			return;
		}
		// 删除登录凭证
		localTicketManagerService.remove(tgt);
		// 删除凭证Cookie
		CookieUtils.removeCookie(AppConstant.TGC, "/", response);
		// 删除所有tgt对应的调用凭证，并通知客户端登出注销本地session
		localAccessTokenManagerService.remove(tgt);
	}

	public String getTgt(HttpServletRequest request) {
		String tgt = getCookieTgt(request);
		if (StringUtils.isEmpty(tgt) || localTicketManagerService.getAndRefresh(tgt) == null) {
			return null;
		}
		else {
			return tgt;
		}
	}

	private String getCookieTgt(HttpServletRequest request) {
		return CookieUtils.getCookie(request, AppConstant.TGC);
	}
}
