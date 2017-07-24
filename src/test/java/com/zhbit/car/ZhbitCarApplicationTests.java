package com.zhbit.car;

import com.zhbit.car.service.AsyncTaskService;
import com.zhbit.car.service.ScheduleTaskService;
import com.zhbit.car.service.impl.AsyncTaskServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZhbitCarApplicationTests {

	@Resource
	public AsyncTaskService asyncTaskService;

	@Resource
	public ScheduleTaskService scheduleTaskService;

	@Test
	public void contextLoads() {
	}

	/**
	 * asyncTaskService的测试类
	 *
	 * 多线程异步
	 *
	 */
	@Test
	public void test1(){

		for(int i = 0;i<10;i++){

			asyncTaskService.executeAsyncTask(i);

			asyncTaskService.executeAsyncTaskPlus(i);

		}

	}

	/**
	 * scheduleTaskService的测试类
	 *
	 * 定时任务
	 *
	 */
	@Test
	public void test2(){
		scheduleTaskService.fixTimeExecution();
		scheduleTaskService.reportCurrentTime();
		while (true){

			try {

				Thread.sleep(1000);

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}


	}
}
