package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wen.togethernow.common.PageRequest;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.request.UserLoginRequest;
import com.wen.togethernow.model.request.UserRegisterRequest;
import com.wen.togethernow.model.request.UserSearchRequest;
import com.wen.togethernow.model.request.UserUpdateRequest;
import com.wen.togethernow.service.UserService;
import com.wen.togethernow.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wen.togethernow.common.BaseCode.*;
import static com.wen.togethernow.constant.UserConstant.*;

/**
 * 用户服务实现类
 *
 * @author wen
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册实现类
     *
     * @param userRegisterRequest 前端输入的注册信息
     * @return 返回注册的用户id
     */
    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        // 校验输入是否为空
        if (userRegisterRequest == null) {
            throw new BusinessException(PARAMS_NULL_ERROR, "注册输入参数为空");
        }
        // 拿到信息
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String idCode = userRegisterRequest.getIdCode();
        // 1.校验输入信息是否完整
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
     * @param userLoginRequest 前端输入的用户登录信息
     * @param request          请求
     * @return 返回脱敏的用户信息
     */
    @Override
    public User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验登陆参数是否为空
        if (userLoginRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(PARAMS_NULL_ERROR, "输入为空");
        }
        // 1.校验
        //校验参数是否完整
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
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 获取当前登录的用户信息
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        // 防止更新用户之后，返回的还是缓存中的用户信息
        if (currentUser == null) {
            throw new BusinessException(AUTH_FAILURE, "未登录或登录过期");
        }
        currentUser = userMapper.selectById(currentUser.getId());
        return getSafetyUser(currentUser);
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }

    /**
     * 判断是否是管理员（前端请求）
     *
     * @param request Http请求
     * @return 是否是管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            throw new BusinessException(AUTH_FAILURE, "未登录或登陆过期");
        }
        return user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 判断用户是否为管理员（当前用户）
     *
     * @param currentUser 当前用户
     * @return 是否是管理员
     */
    @Override
    public boolean isAdmin(User currentUser) {
        return currentUser != null && currentUser.getUserRole() == ADMIN_ROLE;
    }


    /**
     * 查询用户实现类
     *
     * @param userSearchRequest 用户查询请求体
     * @return 返回脱敏的用户信息
     */
    @Override
    public List<User> userSearch(UserSearchRequest userSearchRequest) {
        if (userSearchRequest == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        List<User> safetyUsers = new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
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
        // 根据信息查询,如果信息全部为空，返回所有用户列表
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
     * 使用缓存根据标签查询用户
     *
     * @param tagNameList 标签名
     * @return 查询到的脱敏用户列表
     */
    @Override
    public List<User> userSearchByTags(List<String> tagNameList) {
        if (tagNameList.isEmpty()) {
            throw new BusinessException(PARAMS_NULL_ERROR, "标签为空");
        }
        // 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);
        // 根据标签查询
        List<User> safetyUsers = new ArrayList<>();
        Gson gson = new Gson();
        for (User user : users) {
            String tags = user.getTags();
            // tags从JSON转化为String格式
            Set<String> tagNameSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
            }.getType());
            // 判断是否为空
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            // 用户信息脱敏
            for (String tagStr : tagNameList) {
                if (tagNameSet.contains(tagStr)) {
                    safetyUsers.add(getSafetyUser(user));
                    break;
                }
            }
        }
        return safetyUsers;
    }

    /**
     * 更新用户的实现
     *
     * @param userUpdateRequest 要修改的用户
     * @param loginUser 当前登录用户
     * @return 更新的用户数量
     */
    @Override
    public int updateUser(UserUpdateRequest userUpdateRequest, User loginUser) {
        // 判空
        if (userUpdateRequest == null || loginUser == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 判断是否为管理员 或者 要修改的用户就是当前登录用户
        if (!isAdmin(loginUser) && loginUser.getId().equals(userUpdateRequest.getId())) {
            throw new BusinessException(ACCESS_DENIED, "非管理员用户或要修改的不是当前登录用户");
        }
        // 查询要修改的用户在数据库中是否存在
        User oldUser = userMapper.selectById(userUpdateRequest.getId());
        if (oldUser == null || oldUser.getIsDelete() == USER_DELETED) {
            throw new BusinessException(RESOURCE_NOT_FOUND, "要修改的用户不存在");
        }
        User updateUser = new User();
        BeanUtils.copyProperties(userUpdateRequest, updateUser);
        return userMapper.updateById(updateUser);
    }

    /**
     * 用户脱敏的实现
     *
     * @param userPageList 分页的用户列表
     * @return 脱敏的用户分页列表
     */
    @Override
    public List<User> getSafetyUser(Page<User> userPageList) {
        List<User> userListRecords = userPageList.getRecords();
        List<User> safetyUserList = new ArrayList<>();
        for (User userListRecord : userListRecords) {
            safetyUserList.add(getSafetyUser(userListRecord));
        }
        return safetyUserList;
    }

    /**
     * 用户推进的业务实现
     *
     * @param pageRequest 接收前端的分页参数
     * @param request     前端http请求
     * @return 返回脱敏的用户列表
     */
    @Override
    public List<User> recommendUsers(PageRequest pageRequest, HttpServletRequest request) {
        // 如果缓存中有，就从缓存中拿数据
        User currentUser = getCurrentUser(request);
        String redisKey = String.format("togethernow:user:recommend:%s", currentUser.getId());
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return getSafetyUser(userPage);
        }
        // 缓存没有就查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userMapper.selectPage(new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize()), queryWrapper);
        // 将查询到的数据写到缓存，设置过期时间为5min
        try {
            redisTemplate.opsForValue().set(redisKey, userPage, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis set key error", e);
        }
        // 返回脱敏的用户信息
        return getSafetyUser(userPage);
    }

    /**
     * 使用SQL根据标签查询用户（暂时弃用）
     *
     * @param tagNameList 标签
     * @return 脱敏的用户信息
     */
    @Deprecated(since = "2.0", forRemoval = true)
    private List<User> userSearchByTagsUseSql(List<String> tagNameList) {
        if (tagNameList.isEmpty()) {
            throw new BusinessException(PARAMS_NULL_ERROR, "标签为空");
        }
        // 模糊匹配查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream()
                .map(this::getSafetyUser)
                .collect(Collectors.toList());
    }

    /**
     * 返回脱敏的用户信息
     *
     * @param originUser 原始的用户信息
     * @return 脱敏的用户
     */
    public User getSafetyUser(User originUser) {
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
        safetyUser.setUserProfile(originUser.getUserProfile());
        return safetyUser;
    }
}




