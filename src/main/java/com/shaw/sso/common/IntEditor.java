package com.shaw.sso.common;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * @author shaw
 * @date 2013-4-26
 */
public final class IntEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text))
            text = Constants.DISABLE;
        setValue(Integer.parseInt(text));
    }

    @Override
    public String getAsText() {
        if (getValue() == null)
            return null;

        return getValue().toString();
    }
}
