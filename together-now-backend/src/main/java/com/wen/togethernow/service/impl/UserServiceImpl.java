package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.common.utils.ReturnUtil;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.request.UserSearchRequest;
import com.wen.togethernow.service.UserService;
import com.wen.togethernow.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wen.togethernow.common.BaseCode.*;
import static com.wen.togethernow.constant.UserConstant.*;

/**
 * 用户服务实现类
 *
 * @author Cwb
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册实现类
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @return 返回用户id
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword, String idCode) {
        // 1.校验是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, idCode)) {
            throw new BusinessException(PARAMS_NULL_ERROR, "请求参数为空");
        }
        // 2.长度校验
        if (userAccount.length() < 4 || userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR, "输入长度不符合要求");
        }
        // 2.账号校验
        // 不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(PARAMS_ERROR, "账号已经存在");
        }
        // 不包含特殊字符
        if (!userAccount.matches("^[0-9a-zA-Z]{4,}$")) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 3.密码
        // 两次密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(PARAMS_ERROR, "两次密码不同");
        }
        // 3.用户编号
        //不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_code", idCode);
        count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(PARAMS_ERROR, "编号重复");
        }
        // 5.对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 6.向数据库插入用户数据
        User user = new User();
        user.setUserPassword(encryptPassword);
        user.setUserAccount(userAccount);
        user.setIdCode(idCode);
        boolean res = this.save(user);
        if (!res) {
            throw new BusinessException(PARAMS_ERROR, "注册失败");
        }

        return user.getId();
    }

    /**
     * 用户登陆的实现类
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request 请求
     * @return 返回脱敏的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        //校验是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(PARAMS_NULL_ERROR, "请求参数为空");
        }
        //长度校验（账号不小于4位，密码不小于8位）
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR, "输入长度不符合要求");
        }
        //数据库查询是否有账号
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(PARAMS_NULL_ERROR, "账号不存在");
        }
        //密码是否正确（密码加密之后相比）
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        if (!user.getUserPassword().equals(encryptPassword)) {
            throw new BusinessException(INVALID_PASSWORD_ERROR, "密码错误");
        }
        // 3.返回脱敏后的用户信息
        User safetyUser = getSafetyUser(user);
        // 4.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATUS, safetyUser);
        return safetyUser;
    }

    /**
     * 获取当前登录的用户信息
     *
     * @param request http请求
     * @return 返回脱敏的用户信息
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        // 获取当前登录的用户信息
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (currentUser == null) {
            throw new BusinessException(AUTH_FAILURE, "未登录或登录过期");
        }
        return getSafetyUser(currentUser);
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }

    /**
     * 判断是否是管理员
     *
     * @param request Http请求
     * @return 是否是管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            throw new BusinessException(AUTH_FAILURE, "未登录或登陆过期");
        }
        return user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 查询用户实现类
     *
     * @param userSearchRequest 用户查询请求体
     * @return 返回脱敏的用户信息
     */
    @Override
    public List<User> userSearch(UserSearchRequest userSearchRequest) {
        List<User> safetyUsers = new ArrayList<>();
        if (userSearchRequest == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 拿到前端传递的信息
        String userAccount = userSearchRequest.getUserAccount();
        Integer userRole = userSearchRequest.getUserRole();
        Integer userStatus = userSearchRequest.getUserStatus();
        String phone = userSearchRequest.getPhone();
        String email = userSearchRequest.getEmail();
        Integer gender = userSearchRequest.getGender();
        String idCode = userSearchRequest.getIdCode();
        String username = userSearchRequest.getUsername();
        Date createTime = userSearchRequest.getCreateTime();
        // 根据信息查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userAccount != null) {
            queryWrapper.eq("user_account", userAccount);
        }
        if (userRole != null) {
            queryWrapper.eq("user_role", userRole);
        }
        if (userStatus != null) {
            queryWrapper.eq("user_status", userStatus);
        }
        if (phone != null) {
            queryWrapper.like("phone", phone);
        }
        if (email != null) {
            queryWrapper.like("email", email);
        }
        if (gender != null) {
            queryWrapper.eq("gender", gender);
        }
        if (idCode != null) {
            queryWrapper.eq("id_code", idCode);
        }
        if (username != null) {
            queryWrapper.like("username", username);
        }
        if (createTime != null) {
            queryWrapper.eq("create_time", createTime);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        // 用户信息脱敏
        for (User user : users) {
            safetyUsers.add(getSafetyUser(user));
        }
        return safetyUsers;
    }

    /**
     * 返回脱敏的用户信息
     *
     * @param originUser 原始的用户信息
     * @return 脱敏的用户
     */
    private User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setIdCode(originUser.getIdCode());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }
}




