package com.shaw.sso.service;

import com.shaw.sso.domain.Account;

public interface AccountService {

    Account getByLoginNameOrPhoneNumber(String value);

    boolean login(String userId, String password, Account account);
}
