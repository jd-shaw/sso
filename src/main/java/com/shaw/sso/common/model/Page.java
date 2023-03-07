package com.shaw.sso.common.model;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 分页查询结果封装.
 *
 * @param <T> Page中记录的类型.
 * @author shaw
 */
public class Page<T> extends PageRequest implements Iterable<T> {

    protected List<T> result = null;

    protected long totalCount = -1;

    protected Map<String, Object> summaryData;

    public Page() {
    }

    public Page(PageRequest request) {
        this.page = request.page;
        this.limit = request.limit;
        this.countTotal = request.countTotal;
    }

    public Map<String, Object> getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(Map<String, Object> summaryData) {
        this.summaryData = summaryData;
    }

    /**
     * 获得页内的记录列表.
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setResult(final List<T> result) {
        this.result = result;
    }

    /**
     * 获得总记录数, 默认值为-1.
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数.
     */
    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 实现Iterable接口, 可以for(Object item : page)遍历使用
     */
    @Override
    public Iterator<T> iterator() {
        return result.iterator();
    }

    /**
     * 根据pageSize与totalItems计算总页数.
     */
    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / (double) getLimit());

    }

    /**
     * 是否还有下一页.
     */
    public boolean hasNextPage() {
        return (getPage() + 1 <= getTotalPages());
    }

    /**
     * 是否最后一页.
     */
    public boolean isLastPage() {
        return !hasNextPage();
    }

    /**
     * 取得下页的页号, 序号从1开始. 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        if (hasNextPage()) {
            return getPage() + 1;
        } else {
            return getPage();
        }
    }

    /**
     * 是否还有上一页.
     */
    public boolean hasPrePage() {
        return (getPage() > 1);
    }

    /**
     * 是否第一页.
     */
    public boolean isFirstPage() {
        return !hasPrePage();
    }

    /**
     * 取得上页的页号, 序号从1开始. 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        if (hasPrePage()) {
            return getPage() - 1;
        } else {
            return getPage();
        }
    }

    /**
     * 计算以当前页为中心的页面列表,如"首页,23,24,25,26,27,末页"
     *
     * @param count 需要计算的列表大小
     * @return pageNo列表
     */
    public List<Integer> getSlider(int count) {
        int halfSize = count / 2;
        int totalPage = getTotalPages();

        int startPageNo = Math.max(getPage() - halfSize, 1);
        int endPageNo = Math.min(startPageNo + count - 1, totalPage);

        if (endPageNo - startPageNo < count) {
            startPageNo = Math.max(endPageNo - count, 1);
        }

        List<Integer> result = Lists.newArrayList();
        for (int i = startPageNo; i <= endPageNo; i++) {
            result.add(i);
        }
        return result;
    }

    /**
     * 返回map对象，在ajax访问数据时使用
     */
    public Map<String, Object> returnMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("totalSize", this.getTotalCount());
        map.put("totalPages", this.getTotalPages());
        map.put("data", this.getResult());
        return map;
    }
}
