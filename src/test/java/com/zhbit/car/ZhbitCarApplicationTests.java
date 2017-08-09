package com.zhbit.car;

import com.zhbit.car.service.AsyncTaskService;
import com.zhbit.car.service.ScheduleTaskService;
import com.zhbit.car.service.impl.AsyncTaskServiceImpl;
import com.zhbit.car.util.SpringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZhbitCarApplicationTests {

	@Resource
	public AsyncTaskService asyncTaskService;

	@Resource
	public ScheduleTaskService scheduleTaskService;

	@Autowired
	private JedisPool jedisPools;

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

	@Test
	public void tess3(){

		Jedis jedis = jedisPools.getResource();

		jedis.set("test3","123");

	}

}
