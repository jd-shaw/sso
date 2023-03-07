package com.shaw.sso.domain;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author shaw
 * @date 2021/7/30
 */
@MappedSuperclass
public class BaseTreeNode extends BaseDomain {
    private static final long serialVersionUID = 1L;

    @Column(name = "leaf")
    @Type(type = "byte")
    private boolean leaf; // 是否是叶子节点

    @Column(name = "expanded")
    @Type(type = "byte")
    private boolean expanded; // 是否展开

    private int orderFlag;// 顺序
    private String parentId;
    private String iconCls;
    private List<BaseTreeNode> children;

    public BaseTreeNode() {
    }

    public BaseTreeNode(String id) {
        super(id, null);
    }

    @Transient
    public List<BaseTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<BaseTreeNode> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Column(name = "order_flag")
    public int getOrderFlag() {
        return orderFlag;
    }

    public void setOrderFlag(int orderFlag) {
        this.orderFlag = orderFlag;
    }

    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Column(name = "icon_cls")
    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

}
