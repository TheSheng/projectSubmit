package com.bishe.java.util;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.pagehelper.Page;
import org.apache.commons.collections4.MapUtils;



/**
 * 说明: 分页助手
 * @ClassName: PageInfo
 * @Description: 对Page<E>结果进行包装
 * @author JasonYan
 * @2018年4月13日
 * @version v1.0
 * 项目地址 : http://git.oschina.net/free/Mybatis_PageHelper
 */
@SuppressWarnings("all")
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    //当前页
    private int pageNum;

    //每页的数量
    private int pageSize;

    //总页数
    private long totalPage;

    //总记录数
    private long total;

    //结果集
    private List<T> list;

    /**
     * 无参构造
     */
    public PageInfo() {

    }

    /**
     * 包装Page对象
     *
     * @param list
     */
    public PageInfo(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();

            //this.pages = page.getPages();
            this.list = page;
            this.total = page.getTotal();
        } else if (list instanceof Collection) {
            this.pageNum = 1;
            this.pageSize = list.size() == 0 ? 10 : list.size();

            //this.pages = 1;
            this.list = list;
            this.total = list.size();
        }
        this.pages();
    }

    public <E> PageInfo(List<E> list, Function<E, T> function) {
        if(list == null){
            this.pageNum = 1;
            this.pageSize = 1;
            this.total = 0;
        }else if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.total = page.getTotal();
        } else if (list instanceof Collection) {
            this.pageNum = 1;
            this.pageSize = list.size();
            this.total = list.size();
        }
        this.pages();
        this.list = Optional.ofNullable(list).map(ls -> ls.stream().map(l -> function.apply(l)).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private void pages(){
        if(this.total % this.pageSize == 0){
            this.totalPage = this.total / this.pageSize;
        }else{
            this.totalPage = this.total / this.pageSize + 1;
        }
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @return the pageNum
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @param pageNum the pageNum to set
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @param total the total to set
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @return the list
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @param list the list to set
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 2018年5月21日
     * @author 557092
     * @version 1.0
     * @return the toString字符串
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageInfo{");
        sb.append("pageNum=").append(pageNum);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", total=").append(total);
        sb.append(", list=").append(list);
        sb.append(", navigatepageNums=");
        sb.append('}');
        return sb.toString();
    }


    /**
     * @Title: getPageNum
     * @Description: 从参数集合中获取页码
     * @Author: Gavin
     * @Create Date: 2017年11月23日下午2:32:34
     * @param params
     * @return int
     */
    public static int getPageNum(Map<String, Object> params){
        int pageNum = MapUtils.getIntValue(params, PageHelperConstants.PAGENUM,1);
        if(pageNum == 0) {
            pageNum = 1;
        }
        return pageNum;
    }

    /**
     * @Title: getPageSize
     * @Description: 从参数集合中获取每页条数
     * @Author: Gavin
     * @Create Date: 2017年11月23日下午2:32:51
     * @param params
     * @return int
     */
    public static int getPageSize(Map<String, Object> params){
        int pageSize = MapUtils.getIntValue(params, PageHelperConstants.PAGESIZE,10);
        if(pageSize == 0) {
            pageSize = 10;
        }
        //防止数据太多
        if(pageSize > 200){
            pageSize = 200;
        }
        return pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public PageInfo<T> setTotalPage(long totalPage) {
        this.totalPage = totalPage;
        return this;
    }
}
