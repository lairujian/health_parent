package com.itheima.test;


import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

public class QiNiuTest {
    //使用七牛云提供的SDK实现本地图片的上传
    @Test
    public void test(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "4KgBlMIttq3M_Epk_3sCttvvJT3VpfKR0DR7TBpQ";
        String secretKey = "8VyKpCj9hPvetgYV_wJDPwweHwGXY8ZOnreRxDQp";
        String bucket = "health-lairujian";
//如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "F:\\java学习\\就业班2.1笔记源码-压缩版\\阶段4：会员版(2.1)-医疗实战-传智健康\\day04-第4章：预约管理-套餐管理\\素材\\图片资源\\3bd90d2c-4e82-42a1-a401-882c88b06a1a2.jpg";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "abc.jsp";

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }

    }

    //删除图片
    @Test
    public void test2(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
//...其他参数参考类注释

        String accessKey = "4KgBlMIttq3M_Epk_3sCttvvJT3VpfKR0DR7TBpQ";
        String secretKey = "8VyKpCj9hPvetgYV_wJDPwweHwGXY8ZOnreRxDQp";

        String bucket = "health-lairujian";
        String key = "FuM1Sa5TtL_ekLsdkYWcf5pyjKGu";

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }

    }
}
