package com.zhbit.car.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhbit.car.mapper.ResourcesMapper;
import com.zhbit.car.model.Resources;
import com.zhbit.car.service.ResourcesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/19.
 */
@Service("resourcesService")
public class ResourcesServiceImpl extends BaseService<Resources> implements ResourcesService {
    @Resource
    private ResourcesMapper resourcesMapper;

    @Override
    public PageInfo<Resources> selectByPage(Resources resources, int start, int length) {

        int page = start/length+1;

        Example example = new Example(Resources.class);

        //分页查询
        PageHelper.startPage(page, length);

        List<Resources> userList = selectByExample(example);

        return new PageInfo<>(userList);

    }

    @Override
    public List<Resources> queryAll(){
        return resourcesMapper.queryAll();
    }

    @Override
    @Cacheable(cacheNames="resources",key="#map['userid'].toString()+#map['type']")
    public List<Resources> loadUserResources(Map<String, Object> map) {
        return resourcesMapper.loadUserResources(map);
    }

    @Override
    public List<Resources> queryResourcesListWithSelected(Integer rid) {
        return resourcesMapper.queryResourcesListWithSelected(rid);
    }
}
