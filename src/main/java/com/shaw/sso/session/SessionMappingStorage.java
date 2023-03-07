package com.shaw.sso.session;

import javax.servlet.http.HttpSession;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface SessionMappingStorage {

    HttpSession removeSessionByMappingId(String accessToken);

    void removeBySessionById(String sessionId);

    void addSessionById(String accessToken, HttpSession session);
}
