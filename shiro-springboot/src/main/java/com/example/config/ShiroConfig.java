package com.example.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    //1: 创建 realm对象,需要自定义
    @Bean(name = "userRealm")
    public UserRealm userRealm(){
        return new UserRealm();
    }

    //2: DefaultWebSecurityManager
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    //3: ShiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        //添加shiro的内置过滤器
        /**
         * anno: 无需认证就可以访问
         * authc: 必须认证了才能访问
         * user: 必须拥有 记住我 功能才能使用
         * perms: 拥有对某个资源的权限才能访问
         * role: 拥有某个角色权限才能访问
         *
         * filterMap.put("/user/add","authc");
         * filterMap.put("/user/update","authc");
         */

        //拦截
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();

        //授权,正常情况下,没有授权会跳转到未授权页面
        filterMap.put("/user/add","perms[user:add]");//user用户 add权限
        filterMap.put("/user/update","perms[user:update]");

        filterMap.put("/user/*","authc");

        bean.setFilterChainDefinitionMap(filterMap);

        //设置登录的请求
        bean.setLoginUrl("/toLogin");
        //未授权页面
        bean.setUnauthorizedUrl("/noauth");

        return bean;
    }

    //整合 ShiroDialect: 用来整合 Shiro Thymeleaf
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }

}
