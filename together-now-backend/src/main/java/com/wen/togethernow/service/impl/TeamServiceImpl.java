package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.Team;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.model.domain.UserTeam;
import com.wen.togethernow.model.request.TeamAddRequest;
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


import java.util.Date;
import java.util.Optional;

import static com.wen.togethernow.common.BaseCode.*;
import static com.wen.togethernow.constant.TeamConstant.*;

/**
* @author wen
* @ description 针对表【team(队伍表)】的数据库操作Service实现
* @ createDate 2024-01-08 15:18:03
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍的业务实现
     *
     * @param teamAddRequest 队伍信息
     * @param request http请求
     * @return 创建后的队伍id
     */
    @Override
    @Transactional(rollbackFor = Exception.class) //事务
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
        if (!result) {
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
     * 验证队伍信息的有效性
     *
     * @param teamAddRequest 队伍信息
     * @param currentUser 当前登录用户
     */
    private void validateTeamInfo(TeamAddRequest teamAddRequest, User currentUser) {
        //   1. 创建人最多创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        if (count(queryWrapper) > 5) {
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
        if (new Date().after(expireTime)) {
            throw new BusinessException(PARAMS_ERROR, "过期时间不符合要求");
        }
    }
}




