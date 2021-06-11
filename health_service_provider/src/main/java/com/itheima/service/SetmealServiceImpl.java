package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Setmeal;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService{

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;

    @Value("${out_put_path}")//从属性文件读取输出目录的路径
    private String outPutPath;
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        if (checkgroupIds!=null&&checkgroupIds.length>0){
            /*要返回自增的id*/
            setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        //将图片名称保存到Redis
        savePic2Redis(setmeal.getImg());
        //新增套餐后需要重新生成静态页面 (1.套餐列表页面、2.套餐详情页面)
        generateMobileStaticHtml();


    }
    //生成移动端静态页面（要生成两种）
    private void generateMobileStaticHtml() {
        //准备模板文件中所需的实际数据（查询了所有的套餐）
        List<Setmeal> setmealList = setmealDao.findAll();
        //生成套餐列表的静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情的静态页面
        generateMobileSetmealDetaiListHtml(setmealList);

    }

    //套餐详情的方法（setmealList是套餐，有多个，所以生成多个详情页面）
    private void generateMobileSetmealDetaiListHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal:setmealList){
            Map<String ,Object> map=new HashMap<>();
            map.put("setmeal",setmealDao.findById(setmeal.getId()));
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal.getId()+".html",map);

        }
    }

    // 套餐列表的方法 （多个套餐生成一个页面）
    private void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> map = new HashMap<>();
        //key与页面的插值一致 ，为模板提供数据
        map.put("setmealList",setmealList);

        this.generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);

    }
    //通用的方法，传进套餐列表或者套餐详情页面   （参数：使用的模板，生成文件名，数据）
    private void generateHtml(String templateName, String htmlPageName, Map<String, Object> map) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();//获取配置对象
        Writer out=null;
        try {
            //加载模版文件（传入模板）
            Template template = configuration.getTemplate(templateName);
            //生成的html文件
            out = new FileWriter(new File(outPutPath+"/"+htmlPageName));
            //写入数据
            template.process(map,out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        //分页插件，获取 页码和每页最大数
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page= setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }
    //查找所有的套餐
    @Override
    public List<Setmeal> findAll() {
        List<Setmeal> list=setmealDao.findAll();
        return list;
    }

    @Override
    public Setmeal findById(int id) {

        return setmealDao.findById(id);
    }

    //绑定套餐和检查组的多对多关系
    private void setSetmealAndCheckGroup(Integer id,Integer[] checkgroupIds){
        for (Integer checkgroupId:checkgroupIds){
            Map<String ,Integer> map=new HashMap<>();
            map.put("setmeal_id",id);
            map.put("checkgroup_id",checkgroupId);
            setmealDao.setSetmealAndCheckGroup(map);
        }
    }
    //将图片名称保存到Redis
    private void savePic2Redis(String pis){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pis);
    }
}
