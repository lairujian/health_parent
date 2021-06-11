package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*预约设置*/
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;
    @PostMapping("/upload")
    public Result upload(@RequestParam("excelFile")MultipartFile excelFile){

        //读取Excel文件数据
        try {
            //使用POI解析表格数据  行【列】
            List<String[]> list = POIUtils.readExcel(excelFile);

            if (list!=null && list.size()>0){
                //将数据转换为 预约的对象
              List<OrderSetting> data = new ArrayList<>();
                //读取每一行数据
              for (String[] strings:list){
                  //读取第一列数据
                  String orderDate = strings[0];
                  String number = strings[1];//读取第二列数据
                  //将数据转换为一个一个对象
                  OrderSetting orderSetting = new OrderSetting(new Date(orderDate), Integer.parseInt(number));
                  //将对象存储到list集合里
                  data.add(orderSetting);
              }
              //通过dubbo远程调用服务 实现批量导入到数据库
              orderSettingService.add(data);

            }
        } catch (IOException e) {
            e.printStackTrace();
            //文件解析失败
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }

        return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
    }

    @PostMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){ // 2021-06
        //通过日期去查询数据库,返回可预约数，和已预约数
      try {
          List<Map> list= orderSettingService.getOrderSettingByMonth(date);
          return  new Result(true ,MessageConstant.GET_ORDERSETTING_SUCCESS,list);
      }catch (Exception e){
          e.printStackTrace();
          return  new Result(false,MessageConstant.GET_ORDERSETTING_FAIL);
      }

    }

    //手动设置可预约人数
    /*根据指定日期修改可预约人数*/
    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }

    }
}
