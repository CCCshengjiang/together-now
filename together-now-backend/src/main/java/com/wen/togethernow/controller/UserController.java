package com.wen.togethernow.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.common.utils.ReturnUtil;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.request.UserLoginRequest;
import com.wen.togethernow.model.request.UserPageRequest;
import com.wen.togethernow.model.request.UserRegisterRequest;
import com.wen.togethernow.model.request.UserSearchRequest;
import com.wen.togethernow.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.wen.togethernow.common.BaseCode.*;

/**
 * 用户接口类
 *
 * @author wen
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = { "http://localhost:5173/" }, allowCredentials = "true")
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
            throw new BusinessException(PARAMS_NULL_ERROR, "输入为空");
        }
        Long id = userService.userRegister(userRegisterRequest);
        return ReturnUtil.success(id);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @param request          HTTP请求
     * @return 返回脱敏用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR, "输入为空");
        }
        User user = userService.userLogin(userLoginRequest, request);
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
        if (userSearchRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 鉴权
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ACCESS_DENIED);
        }
        // 查询用户
        List<User> users = userService.userSearch(userSearchRequest);
        return ReturnUtil.success(users);
    }

    /**
     * 根据标签查询用户
     *
     * @param tagNameList 标签列表
     * @return 脱敏的用户列表
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> userSearchByTags(@RequestParam(required = false) List<String> tagNameList) {
        // 判空
        if (tagNameList.isEmpty()) {
            throw new BusinessException(PARAMS_NULL_ERROR, "标签为空");
        }
        // 根据标签查询用户
        List<User> safetyUsers = userService.userSearchByTags(tagNameList);
        return ReturnUtil.success(safetyUsers);
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long id, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
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

    /**
     * 更新用户信息
     *
     * @param updateUser 要更新的用户
     * @param request http请求
     * @return 返回更新的用户数量
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User updateUser, HttpServletRequest request) {
        // 判断参数是否为空
        if (updateUser == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 得到当前登录的用户
        User loginUser = userService.getCurrentUser(request);
        // 修改用户
        int result = userService.updateUser(updateUser, loginUser);
        return ReturnUtil.success(result);
    }

    /**
     * 用户推荐
     *
     * @param request 前端请求
     * @return 返回分页用户列表
     */
    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommendUsers(@ModelAttribute UserPageRequest userPageRequest, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        List<User> safetyUsers = userService.recommendUsers(userPageRequest, request);

        return ReturnUtil.success(safetyUsers);
    }
}
