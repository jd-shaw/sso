package com.shaw.sso.security.vo;

import java.io.Serializable;

/**
 * @author shaw
 * @date 2022/12/13
 */
public class AuthenticationToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private char[] password;
    private String key;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
