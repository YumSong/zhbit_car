package com.zhbit.car.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by admin on 2017/7/24.
 */
@Configuration
@ComponentScan("com.zhbit.car.service.impl")
@EnableScheduling
public class ScheduledTaskConfig {
}
