package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass =CheckGroupService.class )
/*事务*/
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService{
   @Autowired
    private CheckGroupDao checkGroupDao;
    @Override
    public void add(CheckGroup checkGroup,Integer[] checkitemIds) {
        checkGroupDao.add(checkGroup);
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);

    }

    public  void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
        //判断检查项是否为空,检查组必须有检查项
        if (checkitemIds!=null&&checkitemIds.length>0){
            Map<String, Integer> map=null;
            //遍历所有的检查项id
            for (Integer checkitemId:checkitemIds
                 ) {
                //创建map集合，将检查组对应的每一个检查项id存储到map集合里，进行sql操作时，将拿出集合元素一一对应
                 map= new HashMap<>();
                map.put("checkgroup_id",checkGroupId);
                map.put("checkitem_id",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);

                //或者我直接将两个id传过去setCheckGroupAndCheckItem（Integer checkgroup_id,Integer checkitem_id）;

            }
        }
    }
    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        /*mybatis分页插件实现了 通过修改页码和每页展示数 查询所有记录,会返回一个Page对象*/
        PageHelper.startPage(currentPage,pageSize);
        /*我需要做的是按条件查询*/
        Page<CheckGroup> page =checkGroupDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        CheckGroup checkGroup= checkGroupDao.findById(id);
        return checkGroup;
    }

    @Override
    public List<Integer> findcheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findcheckItemIdsByCheckGroupId(id);

    }

    /*编辑检查组，两个步骤，一，修改检查组表，二，修改联合表==>使用集合*/
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.deleteAssociation(checkGroup.getId());

        checkGroupDao.edit(checkGroup);
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);

    }

    @Override
    public List<CheckGroup> findAll() {
        //查询检查组

        return  checkGroupDao.findAll();
    }
}
