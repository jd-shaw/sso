package com.shaw.sso.service;

import com.shaw.sso.domain.AbstractUser;

import java.util.Date;
import java.util.Set;

/**
 * @author shaw
 * @date 2016年4月26日
 */
public interface LoginUserService<T extends AbstractUser> extends UserService<T> {

    public Set<String> getActiveUserIdAfterTime(Date time);

}
