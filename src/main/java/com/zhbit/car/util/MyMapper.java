package com.zhbit.car.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by admin on 2017/7/19.
 */
public interface MyMapper<T> extends Mapper<T>,MySqlMapper<T> {
}
