package com.shaw.sso.service;

import com.shaw.sso.common.Expiration;
import com.shaw.sso.rpc.SsoUser;

import java.util.UUID;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface TicketManagerService extends Expiration {

    /**
     * 登录成功后，根据用户信息生成令牌
     *
     * @param user
     * @return
     */
    default String generate(SsoUser user) {
        String tgt = "TGT-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(tgt, user);
        return tgt;
    }

    /**
     * 登录成功后，根据用户信息生成令牌
     *
     * @param user
     * @return
     */
    void create(String tgt, SsoUser user);

    /**
     * 验证st是否存在且在有效期内，并更新过期时间戳
     *
     * @param tgt
     * @return
     */
    SsoUser getAndRefresh(String tgt);

    /**
     * 设置新的用户信息
     *
     * @param user
     * @return
     */
    void set(String tgt, SsoUser user);

    /**
     * 移除
     *
     * @param tgt
     */
    void remove(String tgt);

}
