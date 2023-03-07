package com.shaw.sso.service;

import com.shaw.sso.common.CodeContent;
import com.shaw.sso.common.Expiration;

import java.util.UUID;

/**
 * @author shaw
 * @date 2022/12/13
 */
public interface CodeManagerService extends Expiration {

	/**
	 * 生成授权码
	 *
	 * @param tgt
	 * @param clientType
	 * @param redirectUri
	 * @return
	 */
	default String generate(String tgt, boolean sendLogoutRequest, String redirectUri) {
		String code = "code-" + UUID.randomUUID().toString().replaceAll("-", "");
		create(code, new CodeContent(tgt, sendLogoutRequest, redirectUri));
		return code;
	}

    /**
     * 生成授权码
     *
	 * @param code
	 * @param codeContent
	 */
	public void create(String code, CodeContent codeContent) ;

    /**
     * 查找并删除
     *
     * @param code
     * @return
     */
	CodeContent getAndRemove(String code);

	/*
	 * code失效时间默认为10分钟
	 */
	@Override
	default int getExpiresIn() {
		return 600;
	}
}
