package com.shaw.sso.common.model;

import com.shaw.sso.common.GridSQLBuilder;
import com.shaw.sso.utils.JackJson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 分页参数封装类.
 *
 * @author shaw
 */
public class PageRequest {

    protected boolean countTotal = true;

    protected boolean queryResult = true;

    protected boolean summary = false;

    private boolean hasWhere = false;

    private boolean hasGroupBy = false;

    private boolean hasOrderBy = false;

    private boolean wrapSortSql = true;

    protected int page = 1;

    protected int limit = 30;

    protected String sort;

    private String searchOper;

    private String searchString;

    private String searchField;

    private String filters;

    private List<Sort> sorts;

    public PageRequest() {
    }

    public PageRequest(int page, int limit, String filters) {
        this.page = page;
        this.limit = limit;
        this.filters = filters;
    }

    public boolean isHasOrderBy() {
        return hasOrderBy;
    }

    public void setHasOrderBy(boolean hasOrderBy) {
        this.hasOrderBy = hasOrderBy;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public boolean isHasWhere() {
        return hasWhere;
    }

    public void setHasWhere(boolean hasWhere) {
        this.hasWhere = hasWhere;
    }

    public boolean isWrapSortSql() {
        return wrapSortSql;
    }

    public void setWrapSortSql(boolean wrapSortSql) {
        this.wrapSortSql = wrapSortSql;
    }

    public String getSearchOper() {
        return searchOper;
    }

    public void setSearchOper(String searchOper) {
        this.searchOper = searchOper;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getFilterJson() {
        return filters;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    GridSQLBuilder.Filters filters2 = null;

    public GridSQLBuilder.Filters toFilters() {
        if (filters2 == null) {
            if (StringUtils.isNotBlank(filters)) {
                filters2 = GridSQLBuilder.buildFilters(this);
            } else {
                filters2 = new GridSQLBuilder.Filters();
            }
        }
        return filters2;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;

        if (page < 1) {
            this.page = 1;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;

        if (limit < 1) {
            this.limit = 1;
        }
    }

    @SuppressWarnings("unchecked")
    public void setSort(String sort) {
        this.sort = sort;
        if (StringUtils.isNotEmpty(sort)) {
            setSorts(JackJson.fromJsonToObject(sort, List.class, Sort.class));
        }
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    /**
     * 获得排序参数.
     */
    public List<Sort> getSorts() {
        return CollectionUtils.isNotEmpty(sorts) ? sorts : Collections.<Sort>emptyList();
    }

    /**
     * 是否默认计算总记录数.
     */
    public boolean isCountTotal() {
        return countTotal;
    }

    /**
     * 设置是否默认计算总记录数.
     */
    public void setCountTotal(boolean countTotal) {
        this.countTotal = countTotal;
    }

    public boolean isQueryResult() {
        return queryResult;
    }

    public void setQueryResult(boolean queryResult) {
        this.queryResult = queryResult;
    }

    public boolean isHasGroupBy() {
        return hasGroupBy;
    }

    public void setHasGroupBy(boolean hasGroupBy) {
        this.hasGroupBy = hasGroupBy;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置, 序号从0开始.
     */
    public int getOffset() {
        return ((page - 1) * limit);
    }

    public void addSort(int index, String property, String direction) {
        getSorts().add(index, new Sort(property, direction));
    }

    public void addSort(String property, String direction) {
        getSorts().add(new Sort(property, direction));
    }

    public void addSort(String property) {
        getSorts().add(new Sort(property));
    }

    public void replaceSort(String oldProperty, String newProperty) {
        List<Sort> sorts = getSorts();
        if (CollectionUtils.isNotEmpty(sorts)) {
            for (Sort sort : sorts) {
                if (StringUtils.equalsIgnoreCase(sort.getProperty(), oldProperty)) {
                    sort.setProperty(newProperty);
                }
            }
        }
    }

    public static class Sort {
        private String property;
        private String direction = "asc";

        public Sort() {
        }

        public Sort(String property) {
            this.property = property;
        }

        public Sort(String property, String direction) {
            this.property = property;
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public String getDirection() {
            return direction;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public static Sort asc(String property) {
            return new Sort(property);
        }

        public static Sort desc(String property) {
            return new Sort(property, "desc");
        }
    }
}
