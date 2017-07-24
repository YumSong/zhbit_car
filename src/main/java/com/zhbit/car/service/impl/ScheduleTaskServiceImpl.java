package com.zhbit.car.service.impl;

import com.zhbit.car.service.ScheduleTaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/7/24.
 */
//@Service("ScheduleTaskService")
public class ScheduleTaskServiceImpl implements ScheduleTaskService {

    private static final SimpleDateFormat dataFormat =  new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime(){

        System.out.println("每隔5秒执行一次 "+ dataFormat.format(new Date()));

    }

    @Scheduled(cron = "0 50 11 ? * *")
    public void fixTimeExecution(){

        System.out.println("在规定时间 "+ dataFormat.format(new Date()) +" 执行");

    }

}
