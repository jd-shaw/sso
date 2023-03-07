package com.shaw.sso.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.shaw.sso.common.Constants;
import com.shaw.sso.common.JResult;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@DynamicUpdate
@Table(name = "sso_account")
public class Account extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private String loginName;//登录名
    private String realName;//真实姓名
    private String password;//密码
    private String phoneNumber;//手机号
    private String enabled = Constants.ENABLE;//是否启用
    private String locked = Constants.DISABLE;// 是否被锁
    private String expiredDate;//失效日期
    private Date passwordExpiredDate;// 密码过期时间
    private Date passwordUpdateDate;// 密码更新时间，如果没有则需要用户初次登录更改密码
    private int passwordErrorTimes;// 密码错误次数，超过一定次数会被锁定，直到密码正确才会被清零
    private Date lockDate;// 密码锁定到期时间，会根据密码错误次数增加

    public Account() {
    }

    public Account(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }

    public Account(String id, String loginName, String password) {
        super.setId(id);
        this.loginName = loginName;
        this.password = password;
    }

    public Account(String id, String createBy, String loginName, String password, String realName, Date passwordExpiredDate, String locked) {
        super(id, createBy);
        this.loginName = loginName;
        this.password = password;
        this.realName = realName;
        this.passwordExpiredDate = passwordExpiredDate;
        this.locked = locked;
    }

    public Account(String id, String createBy, String loginName, String realName, String locked, String phoneNumber) {
        super(id, createBy);
        this.loginName = loginName;
        this.realName = realName;
        this.locked = locked;
        this.phoneNumber = phoneNumber;
    }


    //	此构造用于，通过用户名和密码生成token
    public Account(String loginName) {
        this.loginName = loginName;
    }

    public static interface SimpleAccountView extends BaseDomain.BaseDomainView, JResult.JResultView {
    }

    @JsonView({SimpleAccountView.class})
    @Column(name = "login_name", length = 20, unique = true)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Column(name = "phone_number", length = 20)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonView({SimpleAccountView.class})
    @Column(name = "real_name", length = 20)
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(name = "PASSWORD", length = 50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "enabled", length = 2)
    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Column(name = "locked", length = 1)
    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    @Column(name = "password_update_date")
    public Date getPasswordUpdateDate() {
        return passwordUpdateDate;
    }

    public void setPasswordUpdateDate(Date passwordUpdateDate) {
        this.passwordUpdateDate = passwordUpdateDate;
    }

    @Column(name = "password_error_times", columnDefinition = "int default 0")
    public int getPasswordErrorTimes() {
        return passwordErrorTimes;
    }

    public void setPasswordErrorTimes(int passwordErrorTimes) {
        this.passwordErrorTimes = passwordErrorTimes;
    }

    @Column(name = "lock_date")
    public Date getLockDate() {
        return lockDate;
    }

    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

    @Column(name = "expired_date", length = 20)
    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    @Column(name = "password_expired_date")
    public Date getPasswordExpiredDate() {
        return passwordExpiredDate;
    }

    public void setPasswordExpiredDate(Date passwordExpiredDate) {
        this.passwordExpiredDate = passwordExpiredDate;
    }
}
