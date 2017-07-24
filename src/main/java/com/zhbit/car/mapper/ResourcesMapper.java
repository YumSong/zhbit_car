package com.zhbit.car.mapper;

import com.zhbit.car.model.Resources;
import com.zhbit.car.util.MyMapper;

import java.util.List;
import java.util.Map;

public interface ResourcesMapper extends MyMapper<Resources> {

    List<Resources> queryAll();

    List<Resources> loadUserResources(Map<String, Object> map);

    List<Resources> queryResourcesListWithSelected(Integer rid);

}