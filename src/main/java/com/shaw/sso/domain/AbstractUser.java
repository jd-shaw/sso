package com.shaw.sso.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.shaw.sso.common.JResult;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * @author shaw
 * @date 2021/7/30
 */
@MappedSuperclass
public abstract class AbstractUser extends BaseDomain {
    private static final long serialVersionUID = 1L;

    // 登录时间
    private Date loginTime;

    // 注销时间
    private Date logoutTime;

    private Account account;

    public static interface SimpleUserView extends Account.SimpleAccountView, BaseDomainView, JResult.JResultView {
    }

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "account_id", unique = true)
    @JsonView(SimpleUserView.class)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Column(name = "login_time")
    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    @Column(name = "logout_time")
    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    @Override
    public String toString() {
        return getId();
    }
}
