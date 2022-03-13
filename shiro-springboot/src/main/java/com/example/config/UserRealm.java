package com.example.config;

import com.example.pojo.User;
import com.example.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的 UserRealm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了=>授权");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addStringPermission("user:add");

        //获取当前的用户
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal(); //拿到User对象

        //设置当前用户的权限
        info.addStringPermission(currentUser.getPerms());

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了=>认证");

        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        //连接真实的数据库
        User user = userService.queryUserByName(userToken.getUsername());

        if (user==null){ //没有这个人
            return null; //抛出异常  UnknownAccountException
        }

        Subject currentSubject = SecurityUtils.getSubject();
        Session session = currentSubject.getSession();
        session.setAttribute("loginUser",user);

        //可以加密  MD5  MD5盐值加密
        //密码认证,shiro做~
        return new SimpleAuthenticationInfo(user,user.getPwd(),"");
    }

}
