package com.shaw.sso.controller;

import com.shaw.sso.common.BooleanEditor;
import com.shaw.sso.common.DoubleEditor;
import com.shaw.sso.common.IntEditor;
import com.shaw.sso.common.JResult;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author shaw
 * @date Jan 16, 2014
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取HttpServletRequest
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取HttpServletResponse
     *
     * @return
     */
    public HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    protected Map<String, Object> getRequestParameters() {
        return getRequestParameters(getRequest());
    }

    protected Map<String, Object> getRequestParameters(HttpServletRequest request) {
        Iterator<String> keys = request.getParameterMap().keySet().iterator();
        Map<String, Object> result = new HashMap<String, Object>();
        while (keys.hasNext()) {
            String key = keys.next();
            result.put(key, this.getParamByName(request, key));
        }
        return result;
    }

    protected Object getParamByName(String name) {
        return getParamByName(getRequest(), name);
    }

    protected Object getParamByName(HttpServletRequest request, String name) {
        Object[] obs = (Object[]) request.getParameterMap().get(name);
        if (obs != null && obs.length == 1) {
            return obs[0];
        } else {
            return obs;
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        IntEditor intEditor = new IntEditor();
        DoubleEditor doubleEditor = new DoubleEditor();
        BooleanEditor booleanEditor = new BooleanEditor();
        binder.registerCustomEditor(int.class, intEditor);
        binder.registerCustomEditor(Integer.class, intEditor);
        binder.registerCustomEditor(Double.class, doubleEditor);
        binder.registerCustomEditor(double.class, doubleEditor);
        binder.registerCustomEditor(boolean.class, booleanEditor);
        binder.registerCustomEditor(Boolean.class, booleanEditor);
    }


    @ExceptionHandler(StaleObjectStateException.class)
    public JResult handleException(StaleObjectStateException e, HttpServletRequest request) {
        return JResult.createWarnMessage("当前数据正在被其他人操作，请重试！");
    }

    @ExceptionHandler(BindException.class)
    public JResult handleException(BindException e, HttpServletRequest request) {
        BindException bindException = (BindException) e;
        if (bindException.getAllErrors().size() > 0) {
            return JResult.createWarnMessage(bindException.getAllErrors().get(0).getDefaultMessage());
        } else {
            return JResult.createWarnMessage(e.getMessage());
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public JResult handleException(ConstraintViolationException e, HttpServletRequest request) {
        ConstraintViolationException exception = (ConstraintViolationException) e;
        if (CollectionUtils.isNotEmpty(exception.getConstraintViolations())) {
            return JResult.createWarnMessage(exception.getConstraintViolations().iterator().next().getMessage());
        } else {
            return JResult.createWarnMessage(e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JResult handleException(MethodArgumentNotValidException e, HttpServletRequest request) {
        MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
        if (methodArgumentNotValidException.getBindingResult().getAllErrors().size() > 0) {
            return JResult.createWarnMessage(
                    methodArgumentNotValidException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        } else {
            return JResult.createWarnMessage(e.getMessage());
        }
    }

}
