package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass =CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService{
    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
       //使用到了mybatis的分页插件,传 页码，每页显示数
        PageHelper.startPage(currentPage,pageSize);

        //条件查询
        Page<CheckItem> page = checkItemDao.selectByCondittion(queryString);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Integer id) throws RuntimeException{
        /*不能直接删除，需要判断是否于检查组关联，如果关联，不可删除*/
        long count = checkItemDao.findCountByCheckItemId(id);
        if (count > 0) {
            throw new RuntimeException("当前检查项被应用，不能删除");
        } else {
            checkItemDao.delete(id);
        }
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        CheckItem byId = checkItemDao.findById(id);
        return byId;
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

}
