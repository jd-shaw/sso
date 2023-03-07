package com.shaw.sso.common;

import com.shaw.sso.rpc.SsoUser;

import java.io.Serializable;

/**
 * @author shaw
 * @date 2022/12/12
 */
public class AccessTokenContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private CodeContent codeContent;
    private SsoUser user;
    private String appId;

    public AccessTokenContent(CodeContent codeContent, SsoUser user, String appId) {
        this.codeContent = codeContent;
        this.user = user;
        this.appId = appId;
    }

    public CodeContent getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(CodeContent codeContent) {
        this.codeContent = codeContent;
    }

    public SsoUser getUser() {
        return user;
    }

    public void setUser(SsoUser user) {
        this.user = user;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
