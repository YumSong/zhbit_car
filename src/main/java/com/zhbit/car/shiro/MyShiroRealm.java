package com.zhbit.car.shiro;

import com.zhbit.car.model.Resources;
import com.zhbit.car.model.User;
import com.zhbit.car.service.ResourcesService;
import com.zhbit.car.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/19.
 */
public class MyShiroRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Resource
    private ResourcesService resourceService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        System.out.println("enter into doGetAuthorizationInfo");

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        Map<String,Object> map = new HashMap<String, Object>();

        map.put("userid",user.getId());

        List<Resources> resourcesList = resourceService.loadUserResources(map);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        for(Resources resources : resourcesList){

            info.addStringPermission(resources.getResurl());

        }

        return info;

    }


    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //获取用户输入的账号
        String username = (String)token.getPrincipal();

        User user = userService.selectByUsername(username);

        if(user == null) throw new UnknownAccountException();

        if(0 == user.getEnable()){

            throw new LockedAccountException();

        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(

                user,//用户

                user.getPassword(),//密码

                ByteSource.Util.bytes(username),

                getName()
        );

        Session session = SecurityUtils.getSubject().getSession();

        session.setAttribute("userSession",user);

        session.setAttribute("userSessionId",user.getId());

        return info;
    }
}
