package com.kuang.config;

import com.kuang.pojo.User;
import com.kuang.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: springboot-shiro
 * @description:
 * @version: 1.0
 * @author: LiuJiaQi
 * @create: 2021-02-01 12:54
 **/
//自定义Realm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了=>授权逻辑PrincipalCollection");
        //给资源进行授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //添加资源的授权字符串
//        info.addStringPermission("user:add");
        Subject subject = SecurityUtils.getSubject(); //获得当前对象
        User currentUser = (User) subject.getPrincipal();
        info.addStringPermission(currentUser.getPerms()); //设置权限
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了=>认证逻辑AuthenticationToken");

        //假设数据库的用户名和密码
        String name = "root";
        String password = "123456";
        //1.判断用户名
        UsernamePasswordToken userToken = (UsernamePasswordToken)token;

        //连接真实数据库
        User user = userService.queryUserByName(userToken.getUsername());
        if (user == null) {
            //没有这个人
            return null;
        }
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().setAttribute("loginUser",user);

        //2. 验证密码,我们可以使用一个AuthenticationInfo实现类 SimpleAuthenticationInfo
        // shiro会自动帮我们验证！重点是第二个参数就是要验证的密码！
        return new SimpleAuthenticationInfo(user,user.getPwd(),"");
    }
}
