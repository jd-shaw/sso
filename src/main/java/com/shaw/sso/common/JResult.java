package com.shaw.sso.common;

import com.fasterxml.jackson.annotation.JsonView;
import com.shaw.sso.enums.ResultCode;

import java.io.Serializable;

/**
 * 后台给前台的返回结果
 *
 * @author shaw
 * @date 2015-3-3
 */
public class JResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public final static JResult SUCCESS = new JResult(true, ResultCode.SUCCESS.getCode(), null);

    public static interface JResultView {
    }

    ;

    private boolean success;
    private int code; // 200：成功 300：未授权 400：校验失败 500：系统错误
    private Object result;

    public JResult() {
    }

    public JResult(boolean success, int code, Object result) {
        this.success = success;
        this.result = result;
        this.code = code;
    }

    @JsonView(JResultView.class)
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static JResult createSuccess(Object result) {
        return new JResult(true, 200, result);
    }

    public static JResult createFail(int code, Object result) {
        return new JResult(false, code, result);
    }

    public static JResult createFail(ResultCode code, Object result) {
        return createFail(code.getCode(), result);
    }

    public static JResult createWarnMessage(Object result) {
        return createFail(ResultCode.WARN_MESSAGE, result);
    }

    public static JResult create500Fail() {
        return createFail(500, "Internal server error");
    }

    @JsonView(JResultView.class)
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @JsonView(JResultView.class)
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
