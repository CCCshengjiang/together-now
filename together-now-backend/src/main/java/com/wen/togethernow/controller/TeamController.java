package com.wen.togethernow.controller;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.common.utils.ReturnUtil;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.request.team.*;
import com.wen.togethernow.model.vo.TeamUserVO;
import com.wen.togethernow.service.TeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.wen.togethernow.common.BaseCode.*;

/**
 * 队伍接口
 *
 * @author wen
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private TeamService teamService;

    /**
     * 创建队伍
     *
     * @param teamAddRequest 队伍信息
     * @param request        http请求
     * @return 创建的队伍id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        long teamId = teamService.addTeam(teamAddRequest, request);
        if (teamId <= 0) {
            throw new BusinessException(INTERNAL_ERROR, "创建队伍错误");
        }
        return ReturnUtil.success(teamId);
    }

    /**
     * 查询队伍
     *
     * @param teamSearchRequest 查询条件
     * @param request           前端http请求
     * @return 队伍和队长的封装类
     */
    @GetMapping ("/search")
    public BaseResponse<List<TeamUserVO>> searchTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request) {
        if (teamSearchRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        List<TeamUserVO> teamUserList = teamService.searchTeam(teamSearchRequest, request);
        return ReturnUtil.success(teamUserList);
    }

    /**
     * 查询当前用户是队长的队伍
     *
     * @param request 前端请求
     * @return 脱敏的队伍列表
     */
    @GetMapping ("/search/captain")
    public BaseResponse<List<TeamUserVO>> searchCaptainTeam(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        List<TeamUserVO> teamUserList = teamService.searchCaptainTeam(request);
        return ReturnUtil.success(teamUserList);
    }

    /**
     * 查询当前用户加入的队伍
     *
     * @param request 前端请求
     * @return 安全的队伍列表
     */
    @GetMapping ("/search/join")
    public BaseResponse<List<TeamUserVO>> searchJoinTeam(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        List<TeamUserVO> result = teamService.searchJoinTeam(request);
        return ReturnUtil.success(result);
    }

    /**
     * 查询单个队伍
     *
     * @param id 队伍id
     * @return 脱敏的队伍信息
     */
    @GetMapping("/get")
    public BaseResponse<TeamUserVO> getTeamById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        TeamUserVO teamUserVO = new TeamUserVO();
        BeanUtils.copyProperties(teamService.getById(id), teamUserVO);
        return ReturnUtil.success(teamUserVO);
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest 要更新的信息
     * @param request 前端请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        boolean result = teamService.updateTeam(teamUpdateRequest, request);
        if (!result) {
            throw new BusinessException(INTERNAL_ERROR, "加入队伍错误");
        }
        return ReturnUtil.success(result);
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 队伍的信息
     * @param request 前端请求
     * @return 是否加入成功
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null || request== null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        boolean result = teamService.joinTeam(teamJoinRequest, request);
        if (!result) {
            throw new BusinessException(INTERNAL_ERROR, "加入队伍错误");
        }
        return ReturnUtil.success(result);
    }

    /**
     * 用户退出
     *
     * @param teamQuitRequest 队伍信息
     * @param request 前端请求
     * @return 是否退出成功
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        boolean result = teamService.quitTeam(teamQuitRequest, request);
        if (!result) {
            throw new BusinessException(INTERNAL_ERROR, "退出队伍错误");
        }
        return ReturnUtil.success(result);
    }

    /**
     * 解散队伍
     *
     * @param teamDisbandRequest 队伍信息
     * @param request http请求
     * @return 是否解散成功
     */
    @PostMapping("/disband")
    public BaseResponse<Boolean> disbandTeam(@RequestBody TeamDisbandRequest teamDisbandRequest, HttpServletRequest request) {
        if (teamDisbandRequest == null || request== null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        boolean result = teamService.disbandTeam(teamDisbandRequest, request);
        if (!result) {
            throw new BusinessException(INTERNAL_ERROR, "解散队伍错误");
        }
        return ReturnUtil.success(result);
    }

}
