package com.zhbit.car.controller;

import com.zhbit.car.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 2017/7/19.
 */
@Controller
public class HomeController {

    @RequestMapping(value="/login",method= RequestMethod.GET)
    public String login(){

        System.out.println("to login");

        return "login";

    }

    @RequestMapping(value="/login",method=RequestMethod.POST)
    public String login(HttpServletRequest request, User user,String rememberMe){

        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {

            request.setAttribute("msg", "用户名或密码不能为空！");

            return "login";

        }

        System.out.println("song "+rememberMe);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken(user.getUsername(),user.getPassword());

        try {

            if(rememberMe!=null && "on".equals(rememberMe)){

                token.setRememberMe(true);

            }

            subject.login(token);

            return "redirect:to_index";

        }catch (LockedAccountException lae) {

            token.clear();

            request.setAttribute("msg", "用户已经被锁定不能登录，请与管理员联系！");

            return "login";

        } catch (AuthenticationException e) {

            token.clear();

            request.setAttribute("msg", "用户或密码不正确！");

            return "login";

        }
    }


    @RequestMapping(value={"/to_index",""})
    public ModelAndView userPage(){

        ModelAndView modelAndView = new ModelAndView("index");

        Subject subject = SecurityUtils.getSubject();

        System.out.println("song");

        System.out.println(subject.getPrincipal().toString());


        return modelAndView.addObject("user",(User)subject.getPrincipal());

    }


    @RequestMapping("/rolesPage")
    public String rolesPage(){

        return "role/roles";

    }


    @RequestMapping("/resourcesPage")
    public String resourcesPage(){

        return "resources/resources";

    }

    @RequestMapping("/403")
    public String forbidden(){

        return "403";

    }


}
