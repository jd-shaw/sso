package com.shaw.sso.common;

import java.beans.PropertyEditorSupport;

/**
 * @author shaw
 * @date 2022/12/12
 */
public final class BooleanEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text != null && (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("t") || text.equalsIgnoreCase("1")
                || text.equalsIgnoreCase("enabled") || text.equalsIgnoreCase("y") || text.equalsIgnoreCase("yes")
                || text.equalsIgnoreCase("on"))) {
            setValue(true);
        } else {
            setValue(false);
        }
    }

    @Override
    public String getAsText() {
        if (getValue() == null)
            return null;

        return getValue().toString();
    }
}
