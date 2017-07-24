package com.zhbit.car.service;

import com.github.pagehelper.PageInfo;
import com.zhbit.car.model.User;

/**
 * Created by admin on 2017/7/19.
 */
public interface UserService extends IService<User> {

    PageInfo<User> selectByPage(User user,int start,int length);

    User selectByUsername(String username);

    void delUser(Integer userid);
}
