package com.shaw.sso.security;

import com.shaw.sso.utils.AES256Util;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

/**
 * @author shaw
 * @date 2022/3/11
 * <p>
 * JDBC 配置文件部分信息加密
 */
//@Component
public class JdbcPropertiesEncryptConfigurer extends PropertySourcesPlaceholderConfigurer {

    private String[] encryptedProperties; // 需要解密的属key
    private Properties properties; // 配置文件

    public void setEncryptedProperties(String[] encryptedProperties) {
        this.encryptedProperties = encryptedProperties;
    }

    @Override
    protected void convertProperties(Properties properties) {
        if (encryptedProperties != null) {
            for (int i = 0; i < encryptedProperties.length; i++) {
                String key = encryptedProperties[i];
                if (properties.containsKey(key)) {
                    String value = properties.getProperty(key);
                    // 解密
                    value = AES256Util.decode(AES256Util.DEFAULT_SECRET_KEY, value);
                    // 重新赋值
                    properties.setProperty(key, value);
                }
            }
        }
        this.properties = properties;
        super.convertProperties(properties);
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

}
