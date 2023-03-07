package com.shaw.sso.exception;

/**
 * @author shaw
 * @date 2022/12/12
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Object... objects) {
        super(String.format(msg, objects));
    }

    public ServiceException(String msg, Throwable e) {
        super(msg, e);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
