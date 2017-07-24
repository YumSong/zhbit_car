package com.zhbit.car.service.impl;

import com.zhbit.car.service.AsyncTaskService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/7/24.
 */
@Service("AsyncTaskService")
public class AsyncTaskServiceImpl implements AsyncTaskService {

    @Async
    public void executeAsyncTask(Integer i){

        System.out.println("方法1 +" + i);

    }


    @Async
    public void executeAsyncTaskPlus(Integer i){

        System.out.println("way 2 +" + i);

    }

}
