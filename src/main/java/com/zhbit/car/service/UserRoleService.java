package com.zhbit.car.service;

import com.zhbit.car.model.UserRole;

/**
 * Created by admin on 2017/7/21.
 */
public interface UserRoleService extends IService<UserRole> {
    void addUserRole(UserRole userRole);
}
