package com.shaw.sso.service.impl;

import com.shaw.sso.common.RefreshTokenContent;
import com.shaw.sso.service.ExpirationPolicy;
import com.shaw.sso.service.RefreshTokenManagerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shaw
 * @date 2022/12/14
 */
@Service("sso.LocalRefreshTokenManagerService")
public class LocalRefreshTokenManagerServiceImpl extends AbstractExpiration implements RefreshTokenManagerService, ExpirationPolicy {

    private static Map<String, RefreshTokenVo> REFRESH_TOKEN_MAP = new ConcurrentHashMap<>();

    @Override
    public void create(String refreshToken, RefreshTokenContent refreshTokenContent) {
        RefreshTokenVo tokenVo = new RefreshTokenVo(refreshTokenContent,
                System.currentTimeMillis() + getExpiresIn() * 1000L);
        REFRESH_TOKEN_MAP.put(refreshToken, tokenVo);
    }

    @Override
    public RefreshTokenContent validate(String rt) {
        RefreshTokenVo tokenVo = REFRESH_TOKEN_MAP.remove(rt);
        if (tokenVo == null || System.currentTimeMillis() > tokenVo.getExpired()) {
            return null;
        }
        return tokenVo.getRefreshTokenContent();
    }

    @Override
    @Scheduled(cron = SCHEDULED_CRON)
    public void verifyExpired() {
        REFRESH_TOKEN_MAP.forEach((refreshToken, dummyRt) -> {
            if (System.currentTimeMillis() > dummyRt.getExpired()) {
                REFRESH_TOKEN_MAP.remove(refreshToken);
                logger.debug("resfreshToken : " + refreshToken + "已失效");
            }
        });
    }

    /*
     * refreshToken时效和登录session时效一致
     */
    @Override
    public int getExpiresIn() {
        return timeout;
    }

    private static class RefreshTokenVo {
        private RefreshTokenContent refreshTokenContent;
        private long expired; // 过期时间

        public RefreshTokenVo(RefreshTokenContent refreshTokenContent, long expired) {
            super();
            this.refreshTokenContent = refreshTokenContent;
            this.expired = expired;
        }

        public RefreshTokenContent getRefreshTokenContent() {
            return refreshTokenContent;
        }

        public void setRefreshTokenContent(RefreshTokenContent refreshTokenContent) {
            this.refreshTokenContent = refreshTokenContent;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }
    }
}
