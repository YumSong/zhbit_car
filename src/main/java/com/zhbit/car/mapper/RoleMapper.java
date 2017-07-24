package com.zhbit.car.mapper;

import com.zhbit.car.model.Role;
import com.zhbit.car.util.MyMapper;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {
    List<Role> queryRoleListWithSelected(Integer uid);
}