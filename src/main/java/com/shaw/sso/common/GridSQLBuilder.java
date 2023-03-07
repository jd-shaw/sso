package com.shaw.sso.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shaw.sso.common.model.PageRequest;
import com.shaw.sso.utils.JackJson;
import com.shaw.sso.utils.TypeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridSQLBuilder {
    /**
     * eq : 等于， ne ：不等于 ，lt ：小于 ，le ：小于等于 ，gt ：大于 ，ge ：大于等于， nu ：is null， nn :
     * is not null， in : 属于， ni ：不属于 ，bw : 开始于， bn : 不开始于 ，ew : 结束于 ，en ：不结束于
     * ，cn : 包含， nc : 不包含
     */
    public static Map<String, String> OPERATORS = new HashMap<String, String>();

    /**
     * 查询操作符
     */
    public static String SEARCHOPER = "searchOper";

    /**
     * 查询关键字
     */
    public static String SEARCHSTRING = "searchString";

    /**
     * 查询字段
     */
    public static String SEARCHFIELD = "searchField";

    /**
     * 组合查询参数名
     */
    public static String FILTERS = "filters";

    /**
     * SQL片段
     */
    public static String SQLFRAGMENT = "SqlFragment";

    public static String AND = "and";
    public static String WHERE = "where";
    public static String HAVING = "having";
    public static String WHITESPACE = " ";

    static {
        OPERATORS.put("eq", "=");
        OPERATORS.put("ne", "<>");
        OPERATORS.put("lt", "<");
        OPERATORS.put("le", "<=");
        OPERATORS.put("gt", ">");
        OPERATORS.put("ge", ">=");
        OPERATORS.put("nu", "is null");
        OPERATORS.put("nn", "is not null");
        OPERATORS.put("in", "in");
        OPERATORS.put("ni", "not in");
        OPERATORS.put("bw", "");
        OPERATORS.put("bn", "like");
        OPERATORS.put("ew", "");
        OPERATORS.put("en", "like");
        OPERATORS.put("cn", "like");
        OPERATORS.put("nc", "not like");
    }

    public static Filters buildFilters(PageRequest request) {
        if (StringUtils.isEmpty(request.getFilterJson())) {
            if (StringUtils.isNotEmpty(request.getSearchField()) && StringUtils.isNotEmpty(request.getSearchOper())
                    && StringUtils.isNotEmpty(request.getSearchString())) {
                Filters filters = new Filters();
                return filters.addRule(
                        new Rule(request.getSearchField(), request.getSearchOper(), request.getSearchString()));
            } else {
                return new Filters();
            }
        } else {
            return JackJson.fromJsonToObject(request.getFilterJson(), Filters.class);
        }
    }

    public static String buildCountSql(String sql, boolean hasWhere, boolean hasGroupBy, boolean hasOrderBy,
                                       Filters filters) {
        sql = insertSqlFragment(sql, filters.toWhereSqlFragment(), filters.toHavingSqlFragment(), hasWhere, hasGroupBy);
        if (hasGroupBy) {
            return "select count(1) from (" + (hasOrderBy ? sql : removeOrders(sql)) + ") tb_ ";
        }
        sql = "select count(1) " + removeSelect((hasOrderBy ? sql : removeOrders(sql)));
        return sql;
    }

    public static String buildSearchSql(String sql, boolean hasWhere, boolean hasGroupBy, Filters filters,
                                        List<PageRequest.Sort> sorts, boolean isWrapSortSql) {
        sql = insertSqlFragment(sql, filters.toWhereSqlFragment(), filters.toHavingSqlFragment(), hasWhere, hasGroupBy);
        if (CollectionUtils.isNotEmpty(sorts)) {
            if (isWrapSortSql) {
                sql = "select * from (" + sql + ") tb_ ";
            }
            sql += getOrderBySqlFragment(filters, sorts, isWrapSortSql);
        }
        return sql;
    }

    private static String getOrderBySqlFragment(Filters filters, List<PageRequest.Sort> sorts, boolean isWrapSortSql) {
        if (CollectionUtils.isNotEmpty(sorts)) {
            StringBuffer sb = new StringBuffer(" order by ");
            int i = 0;
            for (PageRequest.Sort sort : sorts) {
                if (i == 0) {
                    if (StringUtils.equalsIgnoreCase("order", sort.getProperty())
                            || StringUtils.equalsIgnoreCase("key", sort.getProperty())) {
                        sb.append("`").append(getSortProperty(sort.getProperty(), isWrapSortSql)).append("` ")
                                .append(sort.getDirection()).append(WHITESPACE);
                    } else {
                        sb.append(getSortProperty(sort.getProperty(), isWrapSortSql)).append(WHITESPACE)
                                .append(sort.getDirection()).append(WHITESPACE);
                    }
                } else {
                    if (StringUtils.equalsIgnoreCase("order", sort.getProperty())
                            || StringUtils.equalsIgnoreCase("key", sort.getProperty())) {
                        sb.append(",").append("`").append(getSortProperty(sort.getProperty(), isWrapSortSql))
                                .append("` ").append(sort.getDirection()).append(WHITESPACE);
                    } else {
                        sb.append(",").append(getSortProperty(sort.getProperty(), isWrapSortSql)).append(WHITESPACE)
                                .append(sort.getDirection()).append(WHITESPACE);
                    }
                }
                i++;
            }
            return sb.toString();
        }
        return StringUtils.EMPTY;
    }

    private static String getSortProperty(String property, boolean isWrapSortSql) {
        if (isWrapSortSql) {
            if (StringUtils.contains(property, Constants.DOT)) {
                String[] propertyParts = StringUtils.split(property, Constants.DOT);
                if (propertyParts.length == 2) {
                    return propertyParts[1];
                }
            }
        }
        return property;
    }

    public static String removeSelect(String sql) {
        int beginPos = sql.toLowerCase().indexOf("from ");
        return sql.substring(beginPos);
    }

    private static String removeOrders(String sql) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String insertSqlFragment(String sql, String whereSqlFragment, String havingSqlFragment,
                                            boolean hasWhere, boolean hasGroupBy) {
        StringBuffer sb = new StringBuffer(sql);
        if (StringUtils.isNotBlank(whereSqlFragment)) {
            String connection = hasWhere ? AND : WHERE;
            if (hasGroupBy) {
                Pattern p = Pattern.compile("\\s+group\\s+by\\s+", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(sql);

                int offset = sql.length();
                while (m.find()) {
                    offset = m.start();
                }

                String whereSb = new StringBuffer().append(WHITESPACE).append(connection).append(WHITESPACE)
                        .append(whereSqlFragment).toString();
                sb.insert(offset, whereSb.toString());
            } else {
                sb.append(WHITESPACE).append(connection).append(WHITESPACE).append(whereSqlFragment);
            }
        }

        if (StringUtils.isNotBlank(havingSqlFragment)) {
            sb.append(WHITESPACE).append(HAVING).append(WHITESPACE).append(havingSqlFragment);
        }
        return sb.toString();
    }

    public static class Filters {
        private String groupOp = AND;
        private List<Rule> rules = new LinkedList<Rule>();

        public String getGroupOp() {
            return groupOp;
        }

        public void setGroupOp(String groupOp) {
            this.groupOp = groupOp;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }

        public String toWhereSqlFragment() {
            if (CollectionUtils.isNotEmpty(this.rules)) {
                StringBuffer sb = new StringBuffer();
                sb.append("(");
                for (int i = 0; i < rules.size(); i++) {
                    Rule rule = rules.get(i);
                    if (!rule.isHaving()) {
                        toSqlFragment(rule, i, (i + 1 < rules.size()), sb);
                    }
                }
                sb.append(")");
                return sb.toString();
            }
            return null;
        }

        public String toHavingSqlFragment() {
            if (CollectionUtils.isNotEmpty(this.rules)) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < rules.size(); i++) {
                    Rule rule = rules.get(i);
                    if (rule.isHaving()) {
                        toSqlFragment(rule, i, (i + 1 < rules.size()), sb);
                    }
                }
                return sb.toString();
            }
            return null;
        }

        private void toSqlFragment(Rule rule, int i, boolean hasGroupOp, StringBuffer sb) {
            if (StringUtils.isNotBlank(rule.getSqlFragment())) {
                String fragment = rule.getSqlFragment();
                fragment = StringUtils.replace(fragment, "{op}", OPERATORS.get(rule.getOp()));
                fragment = StringUtils.replace(fragment, "{data}", getFieldPlaceHolder(rule, i));
                sb.append("(").append(fragment).append(")").append(WHITESPACE);
            } else {
                if (StringUtils.equalsIgnoreCase("order", rule.getField())
                        || StringUtils.equalsIgnoreCase("key", rule.getField())) {
                    // 拼接SQL
                    sb.append("`").append(rule.getField()).append("` ").append(OPERATORS.get(rule.getOp()))
                            .append(WHITESPACE).append(getFieldPlaceHolder(rule, i)).append(WHITESPACE);
                } else {
                    // 拼接SQL
                    sb.append(rule.getField()).append(WHITESPACE).append(OPERATORS.get(rule.getOp())).append(WHITESPACE)
                            .append(getFieldPlaceHolder(rule, i)).append(WHITESPACE);
                }
            }

            if (hasGroupOp)
                sb.append(groupOp).append(WHITESPACE);
        }

        public Map<String, Object> toMap() {
            if (CollectionUtils.isEmpty(this.rules))
                return Collections.emptyMap();

            Map<String, Object> map = Maps.newHashMapWithExpectedSize(this.rules.size());
            for (int i = 0; i < this.rules.size(); i++) {
                Rule rule = this.rules.get(i);
                String fieldName = getFieldName(rule, i);
                if (StringUtils.isNotEmpty(fieldName))
                    map.put(fieldName, wrapParam(rule.getOp(), rule.getData()));
            }
            return map;
        }

        public void setRuleSqlFragment(String field, String sqlFragment) {
            ListIterator<Rule> iterator = rules.listIterator();
            while (iterator.hasNext()) {
                Rule rule = iterator.next();
                if (StringUtils.equalsIgnoreCase(rule.getField(), field)) {
                    rule.setSqlFragment(sqlFragment);
                }
            }
        }

        public void removeRule(String field) {
            boolean flag = true;
            while (flag) {
                flag = false;

                ListIterator<Rule> iterator = rules.listIterator();
                while (iterator.hasNext()) {
                    Rule rule = iterator.next();
                    if (StringUtils.equalsIgnoreCase(rule.getField(), field)) {
                        rules.remove(rule);
                        flag = true;
                        break;
                    }
                }
            }
        }

        public boolean hasRule(String field) {
            Rule rule = getRule(field);
            return rule != null;
        }

        public Rule getRule(String field) {
            ListIterator<Rule> iterator = rules.listIterator();
            while (iterator.hasNext()) {
                Rule rule = iterator.next();
                if (StringUtils.equalsIgnoreCase(rule.getField(), field)) {
                    return rule;
                }
            }
            return null;
        }

        public List<Rule> getRules(String field) {
            List<Rule> rs = Lists.newArrayList();
            ListIterator<Rule> iterator = rules.listIterator();
            while (iterator.hasNext()) {
                Rule rule = iterator.next();
                if (StringUtils.equalsIgnoreCase(rule.getField(), field)) {
                    rs.add(rule);
                }
            }
            return rs;
        }

        public Filters addRule(Rule rule) {
            rules.add(rule);
            return this;
        }
    }

    private static String getFieldName(Rule rule, int index) {
        return "field" + index;
    }

    private static String getFieldPlaceHolder(Rule rule, int index) {
        if (isArrayOp(rule.getOp())) {
            return "(:" + getFieldName(rule, index) + ")";
        } else if ("nu".equals(rule.getOp()) || "nn".equals(rule.getOp())) {
            return StringUtils.EMPTY;
        } else {
            return ":" + getFieldName(rule, index);
        }
    }

    private static boolean isArrayOp(String op) {
        return "in".equals(op) || "ni".equals(op);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rule {
        public static final String DATA_TYPE_STRING = "string";
        public static final String DATA_TYPE_NUMBER = "number";

        private String field;
        private String op;
        private Object data;
        private String dataType = DATA_TYPE_STRING;
        private boolean having;
        private String sqlFragment;

        public Rule() {
        }

        public Rule(String field, String op, Object data) {
            this.field = field;
            this.op = op;
            this.data = data;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public Object getData() {
            if (data != null) {
                if (dataType.equals(Rule.DATA_TYPE_NUMBER)) {
                    return new BigDecimal(data.toString());
                }
            }
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public boolean isHaving() {
            return having;
        }

        public void setHaving(boolean having) {
            this.having = having;
        }

        public String getSqlFragment() {
            return sqlFragment;
        }

        public void setSqlFragment(String sqlFragment) {
            this.sqlFragment = sqlFragment;
        }

    }

    /**
     * 包装值对象
     *
     * @param op
     * @param value
     * @return
     */
    protected static Object wrapParam(String op, Object value) {
        if ("cn".equals(op) || "nc".equals(op)) {
            if (TypeUtils.toString(value).contains("[") && TypeUtils.toString(value).contains("]")) {
                String replace = StringUtils.replace(TypeUtils.toString(value), "[", "");
                String replace2 = StringUtils.replace(replace, "]", "");
                String replace3 = StringUtils.replace(replace2, " ", "");
                String[] split = replace3.split(",");
                String key = StringUtils.join(split, "%,%");
                value = "%" + StringUtils.replace(key, "'", "\\'") + "%";
            } else {
                value = "%" + StringUtils.replace(TypeUtils.toString(value), "'", "\\'") + "%";
            }
        } else if ("bn".equals(op)) {
            value = StringUtils.replace(TypeUtils.toString(value), "'", "\\'") + "%";
        } else if ("en".equals(op)) {
            value = "%" + StringUtils.replace(TypeUtils.toString(value), "'", "\\'");
        } else if (isArrayOp(op)) {
            if (value instanceof String) {
                String str = (String) value;
                if (StringUtils.isNotBlank(str)) {
                    if (str.contains(",")) {
                        return str.split(",");
                    } else if (str.contains(" ")) {
                        return str.split(" ");
                    }
                }
            }
        }
        return value;
    }

    public static String getSqlInValues(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append("'").append(StringUtils.replace(values[i], "'", "\\'")).append("'");
            if (i != values.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.replace("aaa'aaaa", "'", "\\'"));
    }

    // public static void main(String[] args)
    // {
    // String json =
    // "{\"groupOp\":\"OR\",
    // \"rules\":[{\"field\":\"b.pro_code\",\"op\":\"eq\",\"data\":\"ddd\"},{\"field\":\"b.pro_name\",\"op\":\"cn\",\"data\":\"uuuu\"},{\"field\":\"a.display_name\",\"op\":\"eq\",\"data\":\"sss\"}
    // ]}";
    // }
}
