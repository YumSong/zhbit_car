package com.zhbit.car.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by admin on 2017/7/19.
 */
@Service
public interface IService<T> {
    T selectByKey(Object key);

    int save(T entity);

    int delete(Object key);

    int updateAll(T entity);

    int updateNotNull(T entity);

    List<T> selectByExample(Object example);

    //TODO 其他...
}
