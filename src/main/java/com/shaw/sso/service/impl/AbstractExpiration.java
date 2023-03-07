package com.shaw.sso.service.impl;

import com.shaw.sso.common.Expiration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author shaw
 * @date 2022/12/15
 */
public abstract class AbstractExpiration implements Expiration {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${sso.timeout}")
    protected int timeout;

}
