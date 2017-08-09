package com.zhbit.car.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhbit.car.service.ScheduleTaskService;
import com.zhbit.car.util.BaiduapiOffline;
import com.zhbit.car.util.HttpUtils;
import com.zhbit.car.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2017/7/24.
 */
@Service("ScheduleTaskService")
public class ScheduleTaskServiceImpl implements ScheduleTaskService {

    private static final SimpleDateFormat dataFormat =  new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private JedisPool jedisPools;

    public static void changeGps2Redis (JedisPool jedisPools){

        try {

            List<Map> getGps= new ArrayList<Map>();

            Jedis jedis = jedisPools.getResource();

            String url = jedis.get("url");

            Map map1 = new HashMap();

            String time = jedis.get("last_get_time"); //最后一次访问的时间

            String url_value = jedis.get("url_value");

            map1.put("value",url_value+"\""+time+"\"");

            String result = null;

            result = HttpUtils.sendPostRequest(url,map1);

            String newStr = new String(result.getBytes(), "UTF-8");

            Map track = JSON.parseObject(newStr,Map.class);

            List<Map> list = JSON.parseObject(track.get("value").toString(), ArrayList.class);

            time = (String)track.get("server_time");

            for (Map map : list){

                if((Integer)map.get("zhuang_tai")==2){
                }

                double driverlat = Double.valueOf(map.get("latitude")+"");

                double driverlng = Double.valueOf(map.get("longitude")+"");

                Map m = new HashMap<>();

                m.put("latitude",BaiduapiOffline.transform(driverlat,driverlng).get("y"));

                m.put("longitude",BaiduapiOffline.transform(driverlat,driverlng).get("x"));

                m.put("address",map.get("address"));

                m.put("device_id",map.get("device_id"));

                m.put("direction",map.get("direction"));

                m.put("gps_time",map.get("gps_time"));

                m.put("speed",map.get("speed"));

                m.put("zhuang_tai",map.get("zhuang_tai"));

                m.put("time",map.get("time"));

                getGps.add(m);

            }

            jedis.set("getGps", JSON.toJSON(getGps).toString());

            jedis.set("last_get_time",time);

            jedis.set("gps_num", String.valueOf(getGps.size()));

            jedis.close();

        } catch (Exception e) {

            System.out.println("出现gps异常 "+e);

        }
    }

    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime(){

        System.out.println("每隔10秒执行一次 "+ dataFormat.format(new Date()));
        changeGps2Redis (jedisPools);

    }

    @Scheduled(cron = "0 50 11 ? * *")
    public void fixTimeExecution(){

        System.out.println("在规定时间 "+ dataFormat.format(new Date()) +" 执行");

    }

}
