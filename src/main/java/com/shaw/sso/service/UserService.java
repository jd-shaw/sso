package com.shaw.sso.service;

import com.shaw.sso.domain.AbstractUser;
import com.shaw.sso.domain.Account;

import java.util.List;

public interface UserService<T extends AbstractUser>   {

    Account getAccountById(String id);

    Account getAccountByLoginNameOrPhoneNumber(String loginNameOrPhoneNumber);

    AbstractUser getUserByLoginNameOrPhoneNumber(String loginNameOrPhoneNumber);

    boolean existsByLoginName(String loginName);

    void updatePassword(String accountId, String password);

    void setLoginTime(String userId);

    void setLogoutTime(String userId);


    String getUserIdByName(String name);

    List<String> getUserRoleByUserId(String userId);

}
