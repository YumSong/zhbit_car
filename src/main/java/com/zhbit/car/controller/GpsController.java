package com.zhbit.car.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhbit.car.util.OperateTemplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/26.
 */
@Controller
public class GpsController {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 显示车辆gps
     *
     * @param lng
     * @param lat
     * @param request
     * @return
     */
    @RequestMapping("/showCarByGPS")
    @ResponseBody
    public Map showDriverByGPS(@RequestParam("lng") String lng,
                               @RequestParam("lat") String lat,
                               final HttpServletRequest request) {

        final Double callat = Double.valueOf(lat);

        final Double callng = Double.valueOf(lng);

        OperateTemplete templete = new OperateTemplete(request) {

            protected void doSomething() throws Exception {

                if (doValidate()) {

                    map.put("driverList", getLastListTest(callat,callng));

                }

            }

        };

        return templete.operate();

    }

    /**
     * 根据经纬度计算
     * @param userlat
     * @param userlng
     * @return
     */

    public List<Map> getLastListTest (double userlat, double userlng){

        Jedis jedis = jedisPool.getResource();

        List<Map> getGps= new ArrayList<Map>();

        String str = jedis.get("getGps");

        List<Map> list = JSONObject.parseObject(str,ArrayList.class);

        String s = jedis.get("gpsArea");

        double apartLength = (s!=null)?Double.valueOf(s):800;

        for (Map map : list){

            if(getShortDistance(userlng,userlat,Double.valueOf(map.get("longitude").toString()),Double.valueOf(map.get("latitude").toString())) < apartLength){

                getGps.add(map);

            }
        }

        jedis.close();

        return getGps;

    }


    /**
     * 计算距离
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     */
    public static double getShortDistance(double lon1, double lat1, double lon2, double lat2) {

        double DEF_PI = 3.14159265359; // PI

        double DEF_2PI= 6.28318530712; // 2*PI

        double DEF_PI180= 0.01745329252; // PI/180.0

        double DEF_R =6370693.5; // radius of earth

        double ew1, ns1, ew2, ns2;

        double dx, dy, dew;

        double distance;

        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;

        ns1 = lat1 * DEF_PI180;

        ew2 = lon2 * DEF_PI180;

        ns2 = lat2 * DEF_PI180;

        // 经度差
        dew = ew1 - ew2;

        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI)

            dew = DEF_2PI - dew;

        else if (dew < -DEF_PI)

            dew = DEF_2PI + dew;

        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)

        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)

        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);

        return distance;

    }
}
