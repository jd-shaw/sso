package com.shaw.sso.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.shaw.sso.common.JResult;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * @author shaw
 * @date 2021/7/30
 */
@MappedSuperclass
public class BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Date createDate;
    protected Date updateDate;
    protected String createBy;
    protected String updateBy;
    private String id;


    public BaseDomain() {
        Date nowTime = new Date();
        this.createDate = nowTime;
        this.updateDate = nowTime;
    }

    public BaseDomain(String id, String createBy) {
        this.id = id;
        this.createBy = createBy;
        this.updateBy = createBy;
        Date nowTime = new Date();
        this.createDate = nowTime;
        this.updateDate = nowTime;
    }

    @Transient
    public void set(String id, String createBy) {
        this.id = id;
        this.createBy = createBy;
        this.updateBy = createBy;
        Date nowTime = new Date();
        this.createDate = nowTime;
        this.updateDate = nowTime;
    }

    @Transient
    public void setUpdateByandDate(String id, String createBy) {
        this.id = id;
        this.updateBy = createBy;
        Date nowTime = new Date();
        this.updateDate = nowTime;
    }

    @Id
    @JsonView(BaseView.class)
    @Column(name = "id", columnDefinition = "varchar(36) COMMENT '主键ID'", unique = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonView(BaseDomainView.class)
    @Column(name = "create_date", columnDefinition = "DATETIME COMMENT '创建日期'", insertable = true)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonView(BaseDomainView.class)
    @Column(name = "update_date", columnDefinition = "DATETIME COMMENT '更新日期'", insertable = true, updatable = true)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @JsonView(BaseDomainView.class)
    @Column(name = "create_by", columnDefinition = "varchar(36) COMMENT '创建人'", insertable = true)
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @JsonView(BaseDomainView.class)
    @Column(name = "update_by", columnDefinition = "varchar(36) COMMENT '更新人'", insertable = true, updatable = true)
    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }


    public static interface BaseView extends JResult.JResultView {
    }

    public static interface BaseDomainView extends BaseView {
    }


}
