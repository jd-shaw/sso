package com.shaw.sso.security;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author shaw
 * @date 2022/12/13
 */
public class PasswordHelper {

    public static String encryptPassword(String algorithmName, String password, String salt) {
        return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(salt)).toHex();
    }

    public static String encryptPassword2(String algorithmName, String password, String salt) {
        return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(password)).toHex();
    }

}
