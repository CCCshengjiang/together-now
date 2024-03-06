package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.Team;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.domain.UserTeam;
import com.wen.togethernow.model.request.team.*;
import com.wen.togethernow.model.vo.TeamUserVO;
import com.wen.togethernow.service.TeamService;
import com.wen.togethernow.mapper.TeamMapper;
import com.wen.togethernow.service.UserService;
import com.wen.togethernow.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.wen.togethernow.common.BaseCode.*;
import static com.wen.togethernow.constant.TeamConstant.*;

/**
 * @author wen
 * @ description 针对表【team(队伍表)】的数据库操作Service实现
 * @ createDate 2024-01-08 15:18:03
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍的业务实现
     *
     * @param teamAddRequest 队伍信息
     * @param request        http请求
     * @return 创建后的队伍id
     */
    @Override
    @Transactional(rollbackFor = Exception.class) //开启事务
    public long addTeam(TeamAddRequest teamAddRequest, HttpServletRequest request) {
        //1. 请求参数是否为空
        if (teamAddRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        //2. 是否登录，未登录不允许创建
        User currentUser = userService.getCurrentUser(request);
        //3. 校验队伍信息
        validateTeamInfo(teamAddRequest, currentUser);
        //4. 插入队伍信息到队伍表
        Long userId = currentUser.getId();
        Team team = new Team();
        team.setUserId(userId);
        BeanUtils.copyProperties(teamAddRequest, team);
        boolean result = save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        //5. 插入用户、队伍到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(PARAMS_ERROR);
        }
        return teamId;
    }

    /**
     * 查询队伍的业务实现
     *
     * @param teamSearchRequest 查询条件
     * @param request           前端http请求
     * @return 队伍和队长的封装类
     */
    @Override
    public List<TeamUserVO> searchTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request) {
        // 非空判断
        if (teamSearchRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 1. 判断是否登录
        User currentUser = userService.getCurrentUser(request);
        // 3. 只有管理员才能查询私密的队伍,普通用户只能查询公开和加密的队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (userService.isAdmin(currentUser)) {
            Integer teamRequestStatus = teamSearchRequest.getTeamStatus();
            if (teamRequestStatus != null) {
                queryWrapper.eq("team_status", teamRequestStatus);
            }
        } else {
            queryWrapper.and(wrapper -> wrapper.ne("team_status", PRIVATE_TEAM_STATUS));
        }
        // 2. 从请求参数中取出队伍信息，如果存在则作为查询条件
        getTeamSearchQuery(teamSearchRequest, queryWrapper);
        // 3. 队伍查询完成之后,判空
        List<Team> teamListPre = list(queryWrapper);
        if (teamListPre.isEmpty()) {
            return new ArrayList<>();
        }
        //4. 不展示已过期的队伍（根据过期时间筛选）
        List<Team> teamList = new ArrayList<>();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        for (Team team : teamListPre) {
            if (team.getExpireTime().before(new Date())) {
                Long teamId = team.getId();
                // 删除关系表数据
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.select("team_id");
                userTeamQueryWrapper.eq("team_id", teamId);
                userTeamService.remove(userTeamQueryWrapper);
                this.removeById(teamId);
            }else {
                teamList.add(team);
            }
        }
        // 5. 得到当前用户已加入的队伍id
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", currentUser.getId());
        List<UserTeam> hasJoinTeam = userTeamService.list(userTeamQueryWrapper);
        Set<Long> hasJoinTeamId = new HashSet<>();
        for (UserTeam userTeam : hasJoinTeam) {
            hasJoinTeamId.add(userTeam.getTeamId());
        }
        // 6. 关联查询队长的信息
        List<TeamUserVO> teamUserList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            // 用户信息脱敏
            User user = userService.getById(userId);
            User safetyUser = userService.getSafetyUser(user);
            // 队伍信息脱敏
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVO.setCaptainUser(safetyUser);
            // 当前用户是否加入队伍
            Long teamId = team.getId();
            teamUserVO.setHasJoin(hasJoinTeamId.contains(teamId));
            // 查询已加入的队伍的人数
            teamUserVO.setHasJoinNum(getJoinNum(team));
            teamUserList.add(teamUserVO);
        }
        return teamUserList;
    }

    /**
     * 查询当前用户是队长的队伍
     *
     * @param request 前端请求
     * @return 脱敏的队伍列表
     */
    @Override
    public List<TeamUserVO> searchCaptainTeam(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 根据当前用户id（队长id）查询队伍
        User currentUser = userService.getCurrentUser(request);
        Long currentUserId = currentUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId);
        List<Team> teamList = this.list(queryWrapper);
        // 队伍信息脱敏,设置队长信息
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        User captain = userService.getById(currentUserId);
        User safetyCaptain = userService.getSafetyUser(captain);
        for (Team team : teamList) {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVO.setCaptainUser(safetyCaptain);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 查询当前用户已加入的队伍业务层接口
     *
     * @param request 前端请求
     * @return 脱敏的用户列表
     */
    @Override
    public List<TeamUserVO> searchJoinTeam(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser(request);
        Long currentUserId = currentUser.getId();
        // 在用户队伍关系表中查询队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", currentUserId);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        // 根据查询到的队伍id，在队伍表中得到当前用户加入的队伍信息
        List<TeamUserVO> safetyTeamList = new ArrayList<>();
        for (UserTeam userTeam : userTeamList) {
            // 得到队伍信息
            Long teamId = userTeam.getTeamId();
            Team team = this.getById(teamId);
            // 队伍信息脱敏
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 得到队长信息
            Long captainId = team.getUserId();
            User safetyCaptain = userService.getSafetyUser(userService.getById(captainId));
            // 脱敏的队伍列表中设置队长信息
            teamUserVO.setCaptainUser(safetyCaptain);
            safetyTeamList.add(teamUserVO);
        }
        return safetyTeamList;

    }

    /**
     * 更新队伍的业务层实现
     *
     * @param teamUpdateRequest 要更新的信息
     * @param request           前端请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        // 1. 请求参数是否为空
        if (teamUpdateRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        //2. 查询队伍是否存在
        Long teamRequestId = teamUpdateRequest.getId();
        Team oldTeam = isTeamExist(teamRequestId);
        //3. 只有管理员或者队伍的创建者可以修改
        User currentUser = userService.getCurrentUser(request);
        if (!userService.isAdmin(currentUser) && !Objects.equals(oldTeam.getUserId(), currentUser.getId())) {
            throw new BusinessException(ACCESS_DENIED);
        }
        //4. 如果队伍状态改为加密，需要设置密码
        Integer teamRequestStatus = teamUpdateRequest.getTeamStatus();
        String teamRequestPassword = teamUpdateRequest.getTeamPassword();
        if (teamRequestStatus != null && teamRequestStatus == SECRET_TEAM_STATUS && StringUtils.isBlank(teamRequestPassword)) {
            throw new BusinessException(PARAMS_NULL_ERROR, "加密房间必须要设置密码");

        }
        // 如果要修改密码，先判断是否是加密队伍
        if (StringUtils.isNotBlank(teamRequestPassword) && oldTeam.getTeamStatus() != SECRET_TEAM_STATUS && teamUpdateRequest.getTeamStatus() != SECRET_TEAM_STATUS) {
            throw new BusinessException(PARAMS_ERROR, "当前队伍不是加密队伍");
        }
        //5. 更新队伍
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return updateById(updateTeam);
    }

    /**
     * 加入队伍的业务层实现
     *
     * @param teamJoinRequest 队伍信息
     * @param request         前端请求
     * @return 是否加入成功
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        // 参数判空
        if (teamJoinRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        //1. 用户要登录且队伍要存在
        User currentUser = userService.getCurrentUser(request);
        Long teamRequestId = teamJoinRequest.getId();
        Team team = isTeamExist(teamRequestId);
        //2. 只能加入未过期的队伍
        if (team.getExpireTime().before(new Date())) {
            throw new BusinessException(PARAMS_ERROR, "要加入的队伍已过期");
        }
        //3. 如果队伍是加密的，必须密码匹配
        String teamRequestPassword = teamJoinRequest.getTeamPassword();
        Integer status = team.getTeamStatus();
        if (status == SECRET_TEAM_STATUS && (teamRequestPassword == null || !Objects.equals(team.getTeamPassword(), teamRequestPassword))) {
            throw new BusinessException(PARAMS_ERROR, "密码为空或密码不正确");
        }
        //4. 不能加入私有队伍
        if (status == PRIVATE_TEAM_STATUS) {
            throw new BusinessException(PARAMS_ERROR, "私密队伍不允许加入");
        }
        //5. 用户最多或创建加入5个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        Long userId = currentUser.getId();
        queryWrapper.eq("user_id", userId);
        if (userTeamService.count(queryWrapper) >= 5) {
            throw new BusinessException(RESOURCE_NOT_FOUND, "每人最多有五个队伍");
        }
        //6. 不能重复加入同一个队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamRequestId);
        queryWrapper.eq("user_id", userId);
        if (userTeamService.count(queryWrapper) > 0) {
            throw new BusinessException(PARAMS_ERROR, "不能重复加入同一个队伍");
        }
        //6. 只能加入未满员的队伍
        queryWrapper = queryWrapper.eq("team_id", teamRequestId);
        if (userTeamService.count(queryWrapper) >= team.getMaxNum()) {
            throw new BusinessException(PARAMS_ERROR, "当前队伍已满员");
        }
        //7. 新增队伍用户的关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamRequestId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    /**
     * 用户退出的业务实现
     *
     * @param teamQuitRequest 队伍信息
     * @param request         http请求
     * @return 是否退出成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        //1. 判空
        if (teamQuitRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        //2. 用户是否登录且队伍是否存在
        User currentUser = userService.getCurrentUser(request);
        Long teamRequestId = teamQuitRequest.getId();
        Team team = isTeamExist(teamRequestId);
        //3. 检验是否已经加入这个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        Long currentUserId = currentUser.getId();
        queryWrapper.eq("team_id", teamRequestId);
        queryWrapper.eq("user_id", currentUserId);
        if (userTeamService.count(queryWrapper) == 0) {
            throw new BusinessException(PARAMS_ERROR, "用户未加入改队伍");
        }
        //4. 队伍还有多人
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamRequestId);
        if (userTeamService.count(queryWrapper) > 1) {
            return quitTeamMore(queryWrapper, teamRequestId, currentUserId);
        }
        //5. 如果队伍只剩一人，队伍解散
        boolean result = userTeamService.removeById(team.getId());
        boolean teamResult = this.removeById(teamRequestId);
        return result && teamResult;
    }

    /**
     * 解散队伍的业务层实现
     *
     * @param teamDisbandRequest 队伍信息
     * @param request            http请求
     * @return 是否解散成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disbandTeam(TeamDisbandRequest teamDisbandRequest, HttpServletRequest request) {
        // 1. 判空
        if (teamDisbandRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        // 2. 是否登录且队伍是否存在
        User currentUser = userService.getCurrentUser(request);
        Long teamRequestId = teamDisbandRequest.getId();
        Team team = isTeamExist(teamRequestId);
        // 3. 判断当前用户是不是队长
        if (!team.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ACCESS_DENIED);
        }
        // 4. 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", team.getId());
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(INTERNAL_ERROR, "关联信息删除失败");
        }
        // 5. 删除队伍
        return this.removeById(teamRequestId);
    }

    /**
     * 已经加入当前队伍的人数
     *
     * @param team 当前队伍
     * @return 已加入人数
     */
    private int getJoinNum(Team team) {
        if (team == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.select("team_id");
        userTeamQueryWrapper.eq("team_id", team.getId());
        return (int) userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 判断队伍是否存在
     *
     * @param teamRequestId 队伍id
     * @return 返回存在的队伍
     */
    private Team isTeamExist(Long teamRequestId) {
        if (teamRequestId == null || teamRequestId <= 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        Team team = this.getById(teamRequestId);
        if (team == null) {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }
        return team;
    }

    /**
     * 队伍中还有多人的退出队伍方法
     *
     * @param queryWrapper  查询条件
     * @param teamRequestId 当前队伍id
     * @param currentUserId 当前用户id
     */
    private boolean quitTeamMore(QueryWrapper<UserTeam> queryWrapper, long teamRequestId, long currentUserId) {
        boolean result;
        Team currentTeam = this.getById(teamRequestId);
        //  1. 如果是队长，退出后权限转移给先来后到的用户
        if (Objects.equals(currentTeam.getUserId(), currentUserId)) {
            // 在用户队伍关系表中删除队长信息
            queryWrapper.eq("user_id", currentUserId);
            result = userTeamService.remove(queryWrapper);
            if (!result) {
                throw new BusinessException(RESOURCE_NOT_FOUND);
            }
            //获取整个队伍剩下的所有用户
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("team_id", teamRequestId);
            List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
            //看谁先来后到，队长权限给谁
            Date firstJoinTime = new Date();
            long captionUserId = 0;
            for (UserTeam userTeam : userTeamList) {
                queryWrapper.eq("user_id", userTeam.getUserId());
                Date joinTime = userTeamService.getOne(queryWrapper).getJoinTime();
                if (firstJoinTime.after(joinTime)) {
                    captionUserId = userTeam.getUserId();
                    firstJoinTime = joinTime;
                }
            }
            currentTeam.setUserId(captionUserId);
            this.updateById(currentTeam);
        } else {
            //  2. 不是队长就自己退出
            queryWrapper.eq("user_id", currentUserId);
            result = userTeamService.remove(queryWrapper);
        }
        return result;
    }

    /**
     * 搜索队伍的查询条件
     *
     * @param teamSearchRequest 查询信息
     * @param queryWrapper      查询
     */
    private void getTeamSearchQuery(TeamSearchRequest teamSearchRequest, QueryWrapper<Team> queryWrapper) {
        Long userId = teamSearchRequest.getUserId();
        if (userId != null && userId >= 0) {
            queryWrapper.eq("user_id", userId);
        }
        Long teamId = teamSearchRequest.getId();
        if (teamId != null && teamId >= 0) {
            queryWrapper.eq("id", teamId);
        }
        String teamName = teamSearchRequest.getTeamName();
        if (StringUtils.isNotBlank(teamName)) {
            queryWrapper.like("team_name", teamName);
        }
        String teamProfile = teamSearchRequest.getTeamProfile();
        if (StringUtils.isNotBlank(teamProfile)) {
            queryWrapper.like("team_profile", teamProfile);
        }
        Integer maxNum = teamSearchRequest.getMaxNum();
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq("max_num", maxNum);
        }
        //3. 不展示已过期的队伍（根据过期时间筛选）
        Date expireTime = teamSearchRequest.getExpireTime();
        if (expireTime != null && expireTime.after(new Date())) {
            queryWrapper.lt("expire_time", expireTime);
        }
        //4. 可以通过某个关键词同时对名称和描述查询
        String searchText = teamSearchRequest.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(wrapper -> wrapper.like("team_name", searchText).or().like("team_profile", searchText));
        }
    }

    /**
     * 验证队伍信息的有效性
     *
     * @param teamAddRequest 队伍信息
     * @param currentUser    当前登录用户
     */
    private void validateTeamInfo(TeamAddRequest teamAddRequest, User currentUser) {
        // TODO 有bug
        //   1. 创建人最多创建和加入5个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        if (userTeamService.count(queryWrapper) >= 5) {
            throw new BusinessException(PARAMS_ERROR, "最多创建五个队伍");
        }
        //   2. 队伍人数【1-10】
        Integer maxNum = teamAddRequest.getMaxNum();
        if (maxNum == null || maxNum < 1 || maxNum > 20) {
            throw new BusinessException(PARAMS_ERROR, "队伍人数错误");
        }
        //   3. 队伍标题【1-20】
        String teamName = teamAddRequest.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(PARAMS_ERROR, "队伍标题错误");
        }
        //   4. 描述【0-512】
        String teamProfile = teamAddRequest.getTeamProfile();
        if (StringUtils.isNotBlank(teamProfile) && teamProfile.length() > 512) {
            throw new BusinessException(PARAMS_ERROR, "队伍介绍过长");
        }
        //   5. 是否公开（默认为0）
        Integer teamStatus = teamAddRequest.getTeamStatus();
        teamStatus = Optional.ofNullable(teamStatus).orElse(PUBLIC_TEAM_STATUS);
        //   6. 加密状态，密码【1-32】
        String teamPassword = teamAddRequest.getTeamPassword();
        if (teamStatus == SECRET_TEAM_STATUS && (StringUtils.isBlank(teamPassword) || teamPassword.length() > 32)) {
            throw new BusinessException(PARAMS_ERROR, "密码不符合要求");
        }
        //   7. 超时时间 > 当前时间
        Date expireTime = teamAddRequest.getExpireTime();
        // 默认三天过期
        expireTime = Optional.ofNullable(expireTime).orElseGet(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, 3);
            return calendar.getTime();
        });
        if (new Date().after(expireTime)) {
            throw new BusinessException(PARAMS_ERROR, "过期时间不符合要求");
        } else {
            teamAddRequest.setExpireTime(expireTime);
        }

    }
}




