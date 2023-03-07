package com.shaw.sso.service.impl;

import com.shaw.sso.domain.App;
import com.shaw.sso.service.AppService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author shaw
 * @date 2022/12/13
 */
@Service("sso.AppService")
public class AppServiceImpl implements AppService {

    private static List<App> appList = Lists.newArrayList();

    static {
        appList.add(new App("服务端1", "server1", "123456"));
        appList.add(new App("erp", "erp", "123456"));
    }

    @Override
    public boolean exists(String appId) {
        return appList.stream().anyMatch(app -> app.getAppId().equals(appId));
    }

    @Override
    public boolean validate(String appId, String appSecret) {
        for (App app : appList) {
            if (app.getAppId().equals(appId)) {
                if (app.getAppSecret().equals(appSecret)) {
                    return true;
                }
            }
        }
        return false;
    }
}
