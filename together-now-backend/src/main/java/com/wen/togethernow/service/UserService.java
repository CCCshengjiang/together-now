package com.wen.togethernow.service;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.togethernow.model.request.UserLoginRequest;
import com.wen.togethernow.model.request.UserRegisterRequest;
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
     * 用户注册业务层接口
     *
     * @param userRegisterRequest 前端输入的注册信息
     * @return 返回注册的用户id
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登陆的业务层接口
     *
     * @param userLoginRequest 前端输入的用户登录信息
     * @param request 请求
     * @return 返回脱敏的用户信息
     */
    User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

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

    /**
     * 根据标签搜索用户接口
     *
     * @param tagNameList 标签名
     * @return 返回脱敏用户列表
     */
    List<User> userSearchByTags(List<String> tagNameList);

}
