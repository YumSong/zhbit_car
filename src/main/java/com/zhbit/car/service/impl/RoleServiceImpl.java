package com.zhbit.car.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhbit.car.mapper.RoleMapper;
import com.zhbit.car.mapper.RoleResourcesMapper;
import com.zhbit.car.model.Role;
import com.zhbit.car.model.RoleResources;
import com.zhbit.car.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by admin on 2017/7/21.
 */
@Service("roleService")
public class RoleServiceImpl extends BaseService<Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleResourcesMapper roleResourcesMapper;

    @Override
    public List<Role> queryRoleListWithSelected(Integer uid) {
        return roleMapper.queryRoleListWithSelected(uid);
    }

    @Override
    public PageInfo<Role> selectByPage(Role role, int start, int length) {

        int page = start/length+1;

        Example example = new Example(Role.class);

        PageHelper.startPage(page,length);

        List<Role> roleList = selectByExample(example);

        return new PageInfo<>(roleList);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,readOnly=false,rollbackFor={Exception.class})
    public void delRole(Integer roleid) {
        //删除角色
        mapper.deleteByPrimaryKey(roleid);

        //删除角色资源
        Example example = new Example(RoleResources.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("roleid",roleid);

        roleResourcesMapper.deleteByExample(example);

    }
}
