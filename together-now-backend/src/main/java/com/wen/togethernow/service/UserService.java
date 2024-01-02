package com.wen.togethernow.service;

import com.wen.togethernow.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.togethernow.model.request.UserSearchRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户服务接口类
 *
 * @author wen
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册接口
     *
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 确认密码
     * @return 返回用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String idCode);

    /**
     * 用户登录接口
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request 请求
     * @return 返回用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登陆的用户信息
     *
     * @param request http请求
     * @return 脱敏的用户信息
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 用户退出登录
     *
     * @param request 请求
     * @return 返回用户id
     */
    Integer userLogout(HttpServletRequest request);

    /**
     * 判断是否为管理员
     *
     * @param request Http请求
     * @return 是否是管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 查询用户接口
     *
     * @param userSearchRequest 用户查询请求体
     * @return 返回脱敏的用户列表
     */
    List<User> userSearch(UserSearchRequest userSearchRequest);


}
