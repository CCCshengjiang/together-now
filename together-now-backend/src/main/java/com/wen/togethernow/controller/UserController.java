package com.wen.togethernow.controller;

import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.request.UserLoginRequest;
import com.wen.togethernow.model.request.UserRegisterRequest;
import com.wen.togethernow.model.request.UserSearchRequest;
import com.wen.togethernow.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *用户接口类
 *
 * @author Cwb
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 注册后的用户id
     */
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String idCode = userRegisterRequest.getIdCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, idCode)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword, idCode);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @param request HTTP请求
     * @return 返回脱敏用户信息
     */
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 获取当前登录的用户信息
     *
     * @param request 前端传递的http请求
     * @return 返回脱敏的用户信息
     */
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        return userService.getCurrentUser(request);
    }

    /**
     * 用户退出登录
     *
     * @param request 前端HTTP请求
     * @return 返回
     */
    @PostMapping("logout")
    public Integer userLogout(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return userService.userLogout(request);
    }

    /**
     * 查询用户
     *
     * @param userSearchRequest 用户查询请求体
     * @return 查询到的脱敏用户列表
     */
    @GetMapping("/search")
    public List<User> userSearch(@RequestBody UserSearchRequest userSearchRequest, HttpServletRequest request) {
        // 鉴权
        if (userService.isAdmin(request)) {
            return new ArrayList<>();
        }
        if (userSearchRequest == null) {
            return new ArrayList<>();
        }
        return userService.userSearch(userSearchRequest);
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean userDelete(@RequestBody long id, HttpServletRequest request) {
        // 鉴权
        if (userService.isAdmin(request)) {
            return false;
        }
        if (id < 0) {
            return false;
        }
        return userService.removeById(id);
    }





}
