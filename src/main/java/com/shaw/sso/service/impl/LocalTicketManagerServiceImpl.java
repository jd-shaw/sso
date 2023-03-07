package com.shaw.sso.service.impl;

import com.shaw.sso.rpc.SsoUser;
import com.shaw.sso.service.ExpirationPolicy;
import com.shaw.sso.service.TicketManagerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shaw
 * @date 2022/12/13
 */
@Service("sso.LocalTicketManagerService")
public class LocalTicketManagerServiceImpl extends AbstractExpiration implements TicketManagerService, ExpirationPolicy {

    private static Map<String, TgtVo> TGT_MAP = new ConcurrentHashMap<>();

    @Override
    public void create(String tgt, SsoUser user) {
        TGT_MAP.put(tgt, new TgtVo(user, System.currentTimeMillis() + getExpiresIn() * 1000L));
        logger.info("登录凭证生成成功, tgt:{}", tgt);
    }

    @Override
    public SsoUser getAndRefresh(String tgt) {
        TgtVo dummyTgt = TGT_MAP.get(tgt);
        long currentTime = System.currentTimeMillis();
        if (dummyTgt == null || currentTime > dummyTgt.getExpired()) {
            return null;
        }
        dummyTgt.setExpired(currentTime + getExpiresIn() * 1000L);
        return dummyTgt.getUser();
    }

    @Override
    public void set(String tgt, SsoUser user) {
        TgtVo dummyTgt = TGT_MAP.get(tgt);
        if (dummyTgt != null) {
            dummyTgt.setUser(user);
        }
    }

    @Override
    public void remove(String tgt) {
        TGT_MAP.remove(tgt);
        logger.debug("登录凭证删除成功, tgt:{}", tgt);
    }

    @Override
    @Scheduled(cron = SCHEDULED_CRON)
    public void verifyExpired() {
        TGT_MAP.forEach((tgt, dummyTgt) -> {
            if (System.currentTimeMillis() > dummyTgt.getExpired()) {
                TGT_MAP.remove(tgt);
                logger.debug("登录凭证已失效, tgt:{}", tgt);
            }
        });
    }

    @Override
    public int getExpiresIn() {
        return timeout;
    }

    private static class TgtVo {
        private SsoUser user;
        private long expired;

        public TgtVo(SsoUser user, long expired) {
            super();
            this.user = user;
            this.expired = expired;
        }

        public SsoUser getUser() {
            return user;
        }

        public void setUser(SsoUser user) {
            this.user = user;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }
    }
}
