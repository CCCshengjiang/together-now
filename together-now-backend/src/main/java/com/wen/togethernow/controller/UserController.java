package com.wen.togethernow.controller;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.common.utils.ReturnUtil;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.request.UserLoginRequest;
import com.wen.togethernow.model.request.UserRegisterRequest;
import com.wen.togethernow.model.request.UserSearchRequest;
import com.wen.togethernow.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.wen.togethernow.common.BaseCode.*;

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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
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
        Long id = userService.userRegister(userAccount, userPassword, checkPassword, idCode);
        return ReturnUtil.success(id);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @param request HTTP请求
     * @return 返回脱敏用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ReturnUtil.success(user);
    }

    /**
     * 获取当前登录的用户信息
     *
     * @param request 前端传递的http请求
     * @return 返回脱敏的用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser(request);
        return ReturnUtil.success(currentUser);
    }

    /**
     * 用户退出登录
     *
     * @param request 前端HTTP请求
     * @return 返回
     */
    @PostMapping("logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        Integer i = userService.userLogout(request);
        return ReturnUtil.success(i);
    }

    /**
     * 查询用户
     *
     * @param userSearchRequest 用户查询请求体
     * @return 查询到的脱敏用户列表
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> userSearch(@ModelAttribute UserSearchRequest userSearchRequest, HttpServletRequest request) {
        // 鉴权
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ACCESS_DENIED);
        }
        // 展示所有用户信息
        if (userSearchRequest == null) {
            return ReturnUtil.success(userService.list());
        }
        // 根据字段查询
        List<User> users = userService.userSearch(userSearchRequest);
        return ReturnUtil.success(users);
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long id, HttpServletRequest request) {
        // 鉴权
        if (userService.isAdmin(request)) {
            throw new BusinessException(ACCESS_DENIED);
        }
        if (id < 0) {
            throw new BusinessException(RESOURCE_NOT_FOUND, "用户不存在");
        }
        boolean removedById = userService.removeById(id);
        return ReturnUtil.success(removedById);
    }





}
