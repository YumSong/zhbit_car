package com.zhbit.car.service.impl;

import com.zhbit.car.model.UserRole;
import com.zhbit.car.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by admin on 2017/7/21.
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends BaseService<UserRole> implements UserRoleService {

    @Override
    @Transactional(propagation = Propagation.REQUIRED,readOnly = false,rollbackFor = {Exception.class})
    public void addUserRole(UserRole userRole) {

        Example example = new Example(UserRole.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userid",userRole.getUserid());

        mapper.deleteByExample(example);

        mapper.insert(userRole);

    }
}
