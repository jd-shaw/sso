package com.shaw.sso.service.impl;

import com.shaw.sso.domain.Account;
import com.shaw.sso.security.PasswordHelper;
import com.shaw.sso.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.stereotype.Service;

/**
 * @author shaw
 * @date 2022/12/13
 */
@Service("sso.AccountService")
public class AccountServiceImpl implements AccountService {

    @Override
    public Account getByLoginNameOrPhoneNumber(String value) {
        return null;
    }

    @Override
    public boolean login(String userId, String password, Account account) {
        return doCredentialsMatch(userId, password, account.getPassword());
    }


    public boolean doCredentialsMatch(String userId, String password, String key) {
        String encryptedPassword = PasswordHelper.encryptPassword(Sha256Hash.ALGORITHM_NAME,
                new String(password), userId);
        if (StringUtils.equals(encryptedPassword, key)) {
            return true;
        }
        return false;

    }
}
