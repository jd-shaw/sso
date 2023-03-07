package com.shaw.sso.service.impl;

import com.shaw.sso.common.AccessTokenContent;
import com.shaw.sso.common.CodeContent;
import com.shaw.sso.service.AccessTokenManagerService;
import com.shaw.sso.service.ExpirationPolicy;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shaw
 * @date 2022/12/13
 */
@Service("sso.LocalAccessTokenManagerService")
public class LocalAccessTokenManagerServiceImpl extends AbstractExpiration implements AccessTokenManagerService, ExpirationPolicy {

    private static Map<String, AccessTokenVo> ACCESS_TOKEN_MAP = new ConcurrentHashMap<>();
    private static Map<String, Set<String>> TGT_MAP = new ConcurrentHashMap<>();

    @Override
    public void create(String accessToken, AccessTokenContent accessTokenContent) {
        AccessTokenVo dat = new AccessTokenVo(accessTokenContent, System.currentTimeMillis() + getExpiresIn() * 1000L);
        ACCESS_TOKEN_MAP.put(accessToken, dat);

        TGT_MAP.computeIfAbsent(accessTokenContent.getCodeContent().getTgt(), a -> new HashSet<>()).add(accessToken);
        logger.info("调用凭证生成成功, accessToken:{}", accessToken);
    }

    @Override
    public AccessTokenContent get(String accessToken) {
        AccessTokenVo dummyAt = ACCESS_TOKEN_MAP.get(accessToken);
        if (dummyAt == null || System.currentTimeMillis() > dummyAt.getExpired()) {
            return null;
        } else {
            return dummyAt.getAccessTokenContent();
        }
    }

    @Override
    public boolean refresh(String accessToken) {
        AccessTokenVo dummyAt = ACCESS_TOKEN_MAP.get(accessToken);
        if (dummyAt == null || System.currentTimeMillis() > dummyAt.getExpired()) {
            return false;
        }
        dummyAt.setExpired(System.currentTimeMillis() + getExpiresIn() * 1000L);
        return true;
    }

    @Override
    public void remove(String tgt) {
        Set<String> accessTokenSet = TGT_MAP.remove(tgt);
        if (CollectionUtils.isEmpty(accessTokenSet)) {
            return;
        }
        accessTokenSet.forEach(accessToken -> {
            AccessTokenVo dummyAt = ACCESS_TOKEN_MAP.get(accessToken);
            if (dummyAt == null || System.currentTimeMillis() > dummyAt.getExpired()) {
                return;
            }
            CodeContent codeContent = dummyAt.accessTokenContent.getCodeContent();
            if (codeContent == null || !codeContent.isSendLogoutRequest()) {
                return;
            }
            logger.debug("发起客户端登出请求, accessToken:{}, url:{}", accessToken, codeContent.getRedirectUri());
            sendLogoutRequest(codeContent.getRedirectUri(), accessToken);
        });
    }

    @Override
    @Scheduled(cron = SCHEDULED_CRON)
    public void verifyExpired() {
        ACCESS_TOKEN_MAP.forEach((accessToken, dummyAt) -> {
            if (System.currentTimeMillis() > dummyAt.getExpired()) {
                ACCESS_TOKEN_MAP.remove(accessToken);
                logger.debug("调用凭证已失效, accessToken:{}", accessToken);
            }
        });
    }

    /**
     * accessToken时效为登录session时效的1/2
     */
    @Override
    public int getExpiresIn() {
        return timeout / 2;
    }

    private static class AccessTokenVo {
        private AccessTokenContent accessTokenContent;
        private long expired; // 过期时间

        public AccessTokenVo(AccessTokenContent accessTokenContent, long expired) {
            super();
            this.accessTokenContent = accessTokenContent;
            this.expired = expired;
        }

        public AccessTokenContent getAccessTokenContent() {
            return accessTokenContent;
        }

        public void setAccessTokenContent(AccessTokenContent accessTokenContent) {
            this.accessTokenContent = accessTokenContent;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }
    }
}
