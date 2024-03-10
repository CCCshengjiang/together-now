package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wen.togethernow.common.PageRequest;
import com.wen.togethernow.common.utils.AlgorithmUtils;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.request.user.UserLoginRequest;
import com.wen.togethernow.model.request.user.UserRegisterRequest;
import com.wen.togethernow.model.request.user.UserSearchRequest;
import com.wen.togethernow.model.request.user.UserUpdateRequest;
import com.wen.togethernow.model.vo.PageUsersVO;
import com.wen.togethernow.model.vo.SafetyUserVO;
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
        // 设置初始头像和初始标签
        user.setAvatarUrl("https://img1.baidu.com/it/u=2985396150,1670050748&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1710003600&t=5293756f92c3a6922e0540ee28503bfd");
        user.setTags("[\"开心\"]");
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
        // 根据信息查询,如果信息全部为空，返回所有用户列表
        String userAccount = userSearchRequest.getUserAccount();
        if (userAccount != null) {
            queryWrapper.eq("user_account", userAccount);
        }
        Integer userRole = userSearchRequest.getUserRole();
        if (userRole != null) {
            queryWrapper.eq("user_role", userRole);
        }
        Integer userStatus = userSearchRequest.getUserStatus();
        if (userStatus != null) {
            queryWrapper.eq("user_status", userStatus);
        }
        String phone = userSearchRequest.getPhone();
        if (phone != null) {
            queryWrapper.like("phone", phone);
        }
        String email = userSearchRequest.getEmail();
        if (email != null) {
            queryWrapper.like("email", email);
        }
        Integer gender = userSearchRequest.getGender();
        if (gender != null) {
            queryWrapper.eq("gender", gender);
        }
        String idCode = userSearchRequest.getIdCode();
        if (idCode != null) {
            queryWrapper.eq("id_code", idCode);
        }
        String username = userSearchRequest.getUsername();
        if (username != null) {
            queryWrapper.like("username", username);
        }
        Date createTime = userSearchRequest.getCreateTime();
        if (createTime != null) {
            queryWrapper.lt("create_time", createTime);
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
     * @param request     前端请求
     * @return 查询到的脱敏用户列表
     */
    @Override
    public List<User> userSearchByTags(List<String> tagNameList, HttpServletRequest request) {
        if (tagNameList.isEmpty()) {
            throw new BusinessException(PARAMS_NULL_ERROR, "标签为空");
        }
        // 先查询缓存
        String redisKey = "togethernow:all:users";
        List<User> users = (List<User>) redisTemplate.opsForValue().get(redisKey);
        // 缓存中没有，写入缓存
        if (users == null) {
            List<User> originUsers = this.list();
            // 用户信息脱敏 + 写入缓存
            users = safetyUsersToRedis(originUsers, redisKey);
            removeCurrentUser(users, this.getCurrentUser(request));
        }
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
            // 保存标签匹配的用户
            for (String tagStr : tagNameList) {
                if (tagNameSet.contains(tagStr)) {
                    safetyUsers.add(user);
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
     * @param request           前端请求
     * @return 更新的用户数量
     */
    @Override
    public int updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        // 判空
        if (userUpdateRequest == null || request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 得到当前用户的登录态
        User currentUser = getCurrentUser(request);
        // 判断是否为管理员 或者 要修改的用户就是当前登录用户
        if (!isAdmin(currentUser) && currentUser.getId().equals(userUpdateRequest.getId())) {
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
     * 用户推荐的业务实现
     *
     * @param pageRequest 接收前端的分页参数
     * @return 返回脱敏的用户列表 + 用户总量
     */
    @Override
    public PageUsersVO recommendUsers(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 参数校验
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        if (pageNum == 0 || pageSize == 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 只要有一个用户查了，其他用户直接从缓存中取出就好
        String redisKey = "togethernow:user:recommend";
        List<User> safetyUsers = (List<User>) redisTemplate.opsForValue().get(redisKey);
        if (safetyUsers != null) {
            // 分页
            return getPageUsers(pageSize, pageNum, safetyUsers);
        }
        // 缓存没有：从数据库中取数据写入缓存，最多取前1000条数据
        List<User> users = this.list().stream().limit(100).toList();
        // 用户信息脱敏，并写入redis
        safetyUsers = this.safetyUsersToRedis(users, redisKey);
        // 6.根据分页信息返回脱敏的用户列表
        return getPageUsers(pageSize, pageNum, safetyUsers);
    }

    /**
     * 用户匹配的业务层实现
     *
     * @param pageRequest 分页参数信息
     * @param request     前端请求
     * @return 脱敏的用户列表 + 用户总量
     */
    @Override
    public PageUsersVO matchUsers(PageRequest pageRequest, HttpServletRequest request) {
        // 参数校验
        if (pageRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        // 如果需要的用户数量为0，直接返回空列表
        if (pageNum == 0 || pageSize == 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 1.如果缓存中有，就从缓存中拿数据
        User currentUser = this.getCurrentUser(request);
        Long currentUserId = currentUser.getId();
        String redisKey = String.format("togethernow:user:match:%s", currentUserId);
        List<User> userPage = (List<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return getPageUsers(pageSize, pageNum, getSafetyUser(userPage));
        }
        // 2.根据相似度匹配算法得到符合要求的脱敏用户列表
        List<User> safetyUsers = getMatchUsers(currentUser);
        // 3.写入缓存，设置过期时间为5min
        try {
            redisTemplate.opsForValue().set(redisKey, safetyUsers, 60, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis set key error", e);
        }
        return getPageUsers(pageSize, pageNum, safetyUsers);
    }

    /**
     * 相似度匹配用户
     *
     * @param currentUser 当前用户
     * @return 脱敏的用户列表
     */
    @Override
    public List<User> getMatchUsers(User currentUser) {
        // 1.得到当前用户的标签信息
        String curUserTags = currentUser.getTags();
        Gson gson = new Gson();
        List<String> curTags = gson.fromJson(curUserTags, new TypeToken<List<String>>() {
        }.getType());
        // 2.得到所有用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.isNotNull("tags");
        userQueryWrapper.select("id", "tags");
        List<User> userList = this.list(userQueryWrapper);
        // 3.每个用户都和当前用户进行相似度匹配，存储匹配结果
        HashMap<Long, Integer> getMatchUsers = new HashMap<>();
        for (User user : userList) {
            String userTags = user.getTags();
            // 剔除自己
            if (user.getId().equals(currentUser.getId())) {
                continue;
            }
            List<String> tags = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 根据标签进行相似度匹配
            int distance = AlgorithmUtils.editDistance(curTags, tags);
            getMatchUsers.put(user.getId(), distance);
        }
        // 4.根据相似度进行排序
        List<Map.Entry<Long, Integer>> sortedMatchUsers = getMatchUsers.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(100)
                .toList();
        // 5.根据分页信息返回脱敏的用户列表
        List<User> safetyUsers = new ArrayList<>();
        for (Map.Entry<Long, Integer> sortedMatchUser : sortedMatchUsers) {
            Long userId = sortedMatchUser.getKey();
            safetyUsers.add(this.getSafetyUser(this.getById(userId)));
        }
        return safetyUsers;
    }

    /**
     * 使用迭代器从用户列表中移除当前用户
     *
     * @param safetyUsers 脱敏的用户列表
     * @param currentUser 当前用户
     */
    private void removeCurrentUser(List<User> safetyUsers, User currentUser) {
        Iterator<User> iterator = safetyUsers.iterator();
        while (iterator.hasNext()) {
            User safetyUser = iterator.next();
            if (Objects.equals(safetyUser.getId(), currentUser.getId())) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * 将用户信息脱敏并写入 redis
     *
     * @param originUsers 原始用户列表
     * @param redisKey    redis的 key
     * @return 脱敏的用户列表
     */
    @Override
    public List<User> safetyUsersToRedis(List<User> originUsers, String redisKey) {
        List<User> safetyUsers = new ArrayList<>();
        for (User user : originUsers) {
            safetyUsers.add(this.getSafetyUser(user));
        }
        // 将查询到的数据写到缓存，设置过期时间为12h
        try {
            redisTemplate.opsForValue().set(redisKey, safetyUsers, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Redis set key error", e);
        }
        return safetyUsers;
    }

    /**
     * 返回分页结果
     *
     * @param pageSize    页数大小
     * @param pageNum     页数
     * @param safetyUsers 脱敏的用户列表
     * @return 脱敏的用户信息 + 用户总量
     */
    private static PageUsersVO getPageUsers(int pageSize, int pageNum, List<User> safetyUsers) {
        // 计算索引
        int start = pageSize * (pageNum - 1);
        int end = start + pageSize;
        int total = safetyUsers.size();
        List<User> res = new ArrayList<>();
        for (int i = start; i < total && i < end; i++) {
            User user = safetyUsers.get(i);
            res.add(user);
        }
        PageUsersVO pageUsersVO = new PageUsersVO();
        pageUsersVO.setSafetyUsers(res);
        pageUsersVO.setTotalUsers(total);
        return pageUsersVO;
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
        return getSafetyUser(userListRecords);
    }

    /**
     * 用户脱敏的实现
     *
     * @param userList 用户列表
     * @return 脱敏的用户信息
     */
    @Override
    public List<User> getSafetyUser(List<User> userList) {
        List<User> safetyUserList = new ArrayList<>();
        for (User userListRecord : userList) {
            safetyUserList.add(getSafetyUser(userListRecord));
        }
        return safetyUserList;
    }

    /**
     * 返回脱敏的用户信息
     *
     * @param originUser 原始的用户信息
     * @return 脱敏的用户
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        SafetyUserVO safetyUserVO = new SafetyUserVO();
        BeanUtils.copyProperties(originUser, safetyUserVO);
        User safetyUser = new User();
        BeanUtils.copyProperties(safetyUserVO, safetyUser);
        return safetyUser;
    }
}




