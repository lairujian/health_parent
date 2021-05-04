package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    public void add(CheckItem checkItem);
    public Page<CheckItem> selectByCondittion(String queryString);
    public void delete(Integer id);
    /*查询检查组关于检查项的数量*/
    public long findCountByCheckItemId(Integer checkItemId);

    public void edit(CheckItem checkItem);
    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
