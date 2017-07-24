package com.zhbit.car.service;

import com.github.pagehelper.PageInfo;
import com.zhbit.car.model.Role;

import java.util.List;

/**
 * Created by admin on 2017/7/21.
 */
public interface RoleService extends IService<Role> {
    public List<Role> queryRoleListWithSelected(Integer uid);

    PageInfo<Role> selectByPage(Role role, int start, int length);

    /**
     * 删除角色 同时删除角色资源表中的数据
     * @param roleid
     */
    public void delRole(Integer roleid);
}
