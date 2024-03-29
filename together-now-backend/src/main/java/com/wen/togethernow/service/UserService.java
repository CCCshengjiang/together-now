package com.wen.togethernow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.togethernow.common.PageRequest;
import com.wen.togethernow.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.togethernow.model.request.user.UserLoginRequest;
import com.wen.togethernow.model.request.user.UserRegisterRequest;
import com.wen.togethernow.model.request.user.UserSearchRequest;
import com.wen.togethernow.model.request.user.UserUpdateRequest;
import com.wen.togethernow.model.vo.PageUsersVO;
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
     * 判断是否为管理员（前端）
     *
     * @param request Http请求
     * @return 是否是管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断用户是否为管理员（当前用户）
     *
     * @param currentUser 当前用户
     * @return 是否为管理员
     */
    boolean isAdmin(User currentUser);

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
     * @param request 前端请求
     * @return 返回脱敏用户列表
     */
    List<User> userSearchByTags(List<String> tagNameList, HttpServletRequest request);

    /**
     * 修改用户信息的业务接口
     *
     * @param userUpdateRequest 要修改的用户
     * @param request           前端请求
     * @return 返回更新的用户数量
     */
    int updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 将用户信息脱敏并保存到 Redis中
     *
     * @param originUsers 原始用户数据
     * @param redisKey redis 的key
     * @return 列表
     */
    List<User> safetyUsersToRedis(List<User> originUsers, String redisKey);

    /**
     * 用户脱敏的业务层接口
     *
     * @param userPageList 分页的用户列表
     * @return 脱敏的用户分页列表
     */
    List<User> getSafetyUser(Page<User> userPageList);

    /**
     * 用户脱敏的业务层接口
     *
     * @param userList 用户列表
     * @return 脱敏的用户信息
     */
    List<User> getSafetyUser(List<User> userList);

    /**
     * 用户脱敏的业务层接口
     *
     * @param originUser 原始的用户信息
     * @return 脱敏的用户
     */
    User getSafetyUser(User originUser);

    /**
     * 用户推荐的业务层接口
     *
     * @param pageRequest 接收前端的分页参数
     * @return 返回脱敏的用户信息 + 用户总量
     */
    PageUsersVO recommendUsers(PageRequest pageRequest);

    /**
     * 用户匹配的业务层接口
     *
     * @param pageRequest 分页参数信息
     * @param request     前端请求
     * @return 脱敏的用户列表 + 用户总量
     */
    PageUsersVO matchUsers(PageRequest pageRequest, HttpServletRequest request);

    /**
     * 相似度匹配算法推荐用户
     *
     * @param currentUser 当前用户
     * @return 脱敏的用户列表
     */
    List<User> getMatchUsers(User currentUser);
}
