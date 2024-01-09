package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.Team;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.domain.UserTeam;
import com.wen.togethernow.model.request.TeamAddRequest;
import com.wen.togethernow.model.request.TeamSearchRequest;
import com.wen.togethernow.model.request.TeamUpdateRequest;
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

        if (currentUser == null) {
            throw new BusinessException(AUTH_FAILURE);
        }
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
        //1. 判断是否登录
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(AUTH_FAILURE);
        }
        //2. 从请求参数中取出队伍信息，如果存在则作为查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        getTeamSearchQuery(teamSearchRequest, queryWrapper);
        //3. 只有管理员才能查询非公开的房间
        Integer teamStatus;
        if (userService.isAdmin(currentUser)) {
            teamStatus = teamSearchRequest.getTeamStatus();
        } else {
            teamStatus = PUBLIC_TEAM_STATUS;
        }
        if (teamStatus != null) {
            queryWrapper.eq("team_status", teamStatus);
        }
        // 4. 队伍查询完成之后,判空
        List<Team> teamList = list(queryWrapper);
        if (teamList.isEmpty()) {
            return new ArrayList<>();
        }
        // 5. 关联查询队长的信息
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
            teamUserList.add(teamUserVO);
        }
        return teamUserList;
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
        Long teamId = teamUpdateRequest.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }
        //3. 只有管理员或者队伍的创建者可以修改
        User currentUser = userService.getCurrentUser(request);
        Team oldTeam = getById(teamId);
        if (!userService.isAdmin(currentUser) && !Objects.equals(oldTeam.getUserId(), currentUser.getId())) {
            throw new BusinessException(ACCESS_DENIED);
        }
        //4. 如果队伍状态改为加密，需要设置密码
        Integer teamStatus = teamUpdateRequest.getTeamStatus();
        String teamPassword = teamUpdateRequest.getTeamPassword();
        if (teamStatus != null && teamStatus == SECRET_TEAM_STATUS) {
            if (StringUtils.isBlank(teamPassword)) {
                throw new BusinessException(PARAMS_NULL_ERROR, "加密房间必须要设置密码");
            }
        }
        // 如果要修改密码，先判断是否是加密队伍
        if (StringUtils.isNotBlank(teamPassword) && oldTeam.getTeamStatus() != SECRET_TEAM_STATUS) {
            throw new BusinessException(PARAMS_ERROR, "当前队伍不是加密队伍");
        }
        //5. 更新队伍
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        boolean result = updateById(updateTeam);
        if (!result) {
            throw new BusinessException(INTERNAL_ERROR);
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
            queryWrapper.like("team_name", searchText).or().like("team_profile", searchText);
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
        //   1. 创建人最多创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        if (count(queryWrapper) >= 5) {
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




