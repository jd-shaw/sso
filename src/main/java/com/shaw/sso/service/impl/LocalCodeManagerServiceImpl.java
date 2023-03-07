package com.shaw.sso.service.impl;

import com.shaw.sso.common.CodeContent;
import com.shaw.sso.service.CodeManagerService;
import com.shaw.sso.service.ExpirationPolicy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shaw
 * @date 2022/12/13
 */
@Service("sso.LocalCodeManagerService")
public class LocalCodeManagerServiceImpl extends AbstractExpiration implements CodeManagerService, ExpirationPolicy {

    private static Map<String, CodeVo> CODE_MAP = new ConcurrentHashMap<>();

    @Override
    public void create(String code, CodeContent codeContent) {
        CODE_MAP.put(code, new CodeVo(codeContent, System.currentTimeMillis() + getExpiresIn() * 1000L));
        logger.info("授权码生成成功, code:{}", code);
    }

    @Override
    public CodeContent getAndRemove(String code) {
        CodeVo dc = CODE_MAP.remove(code);
        if (dc == null || System.currentTimeMillis() > dc.getExpired()) {
            return null;
        }
        return dc.getCodeContent();
    }

    @Override
    @Scheduled(cron = SCHEDULED_CRON)
    public void verifyExpired() {
        CODE_MAP.forEach((code, dummyCode) -> {
            if (System.currentTimeMillis() > dummyCode.getExpired()) {
                CODE_MAP.remove(code);
                logger.info("授权码已失效, code:{}", code);
            }
        });
    }

    private static class CodeVo {
        private CodeContent codeContent;
        private long expired; // 过期时间

        public CodeVo(CodeContent codeContent, long expired) {
            this.codeContent = codeContent;
            this.expired = expired;
        }

        public CodeContent getCodeContent() {
            return codeContent;
        }

        public void setCodeContent(CodeContent codeContent) {
            this.codeContent = codeContent;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }
    }
}
