package com.zhbit.car.service.impl;

import com.zhbit.car.model.RoleResources;
import com.zhbit.car.service.RoleResourceService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by admin on 2017/7/21.
 */
@Service("roleResourceService")
public class RoleResourceServiceImpl extends BaseService<RoleResources> implements RoleResourceService {

    @Override
    @Transactional(propagation= Propagation.REQUIRED,readOnly=false,rollbackFor={Exception.class})
    @CacheEvict(cacheNames="resources", allEntries=true)
    public void addRoleResources(RoleResources roleResources) {

        Example example = new Example(RoleResources.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("roleid",roleResources.getRoleid());

        mapper.deleteByExample(example);

        if(!StringUtils.isEmpty(roleResources.getResourcesid())){

            mapper.insert(roleResources);

        }

    }

}
