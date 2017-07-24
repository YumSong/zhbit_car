package com.zhbit.car.service;

import com.github.pagehelper.PageInfo;
import com.zhbit.car.model.Resources;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/19.
 */
public interface ResourcesService extends IService<Resources> {

    PageInfo<Resources> selectByPage(Resources resources,int start,int length);

    public List<Resources> queryAll();

    public List<Resources> loadUserResources(Map<String,Object> map);

    public List<Resources> queryResourcesListWithSelected(Integer rid);
}

