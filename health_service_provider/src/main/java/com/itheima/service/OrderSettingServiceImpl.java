package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService{
    @Autowired
   private OrderSettingDao orderSettingDao;
    //批量导入预约设置数据
    @Override
    public void add(List<OrderSetting> list) {
     if (list!=null&& list.size()>0) {
         //将日期拿出来
         for (OrderSetting orderSetting : list) {
             //判断当前日期是否已经进行了预约设置
             long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
             if (countByOrderDate>0){
                 //已经进行了预约设置，执行更新操作
                 orderSettingDao.editNumberByOrderDate(orderSetting);
             }else {
                 orderSettingDao.add(orderSetting);
             }
             /*//查询数据库的日期
             if (orderSettingDao.findCountByOrderDate(orderDate) != null) {
                 orderSettingDao.editNumberByOrderDate(list);
             } else {
                 orderSettingDao.add(list);
             }*/

         }
     }
    }

    /*根据日期查询预约设置数据*/
    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        //查询数据的参数，从第一天到最后一天，不用map，直接传过两个数据？也可以吧
        String dateBegin=date+"-1";
        String dateEnd=date+"-31";
        //将最后一天和第一天存储到map集合里
        Map<String,String> map=new HashMap();
        map.put("dateBegin",dateBegin);
        map.put("dateEnd",dateEnd);
        //查询到可预约人数和已预约人数，及对应的日期
        List<OrderSetting> list =orderSettingDao.getOrderSettingByMonth(map);

        List<Map> data=new ArrayList<>();
        if (list!=null && list.size()>0) {
            for (OrderSetting orderSetting : list) {
                Map<String,Object> orderSettingMap = new HashMap();

                orderSettingMap.put("date", orderSetting.getOrderDate().getDate());//获取数据库的日期转换数字
                orderSettingMap.put("number", orderSetting.getNumber());
                orderSettingMap.put("reservations", orderSetting.getReservations());

                data.add(orderSettingMap);
            }
        }
        return data;
    }

    /*根据日期修改可预约人数*/
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        /*查询该日期*/
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count>0){
            //已经有的日期，修改
        orderSettingDao.editNumberByDate(orderSetting);
        }else {
            orderSettingDao.add(orderSetting);
        }
    }
}
