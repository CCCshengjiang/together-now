package com.wen.togethernow.controller;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.common.utils.ReturnUtil;
import com.wen.togethernow.exception.BusinessException;
import com.wen.togethernow.model.domain.Team;
import com.wen.togethernow.model.request.TeamAddRequest;
import com.wen.togethernow.model.request.TeamSearchRequest;
import com.wen.togethernow.model.request.TeamUpdateRequest;
import com.wen.togethernow.model.vo.TeamUserVO;
import com.wen.togethernow.service.TeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
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
     * @param request http请求
     * @return 创建的队伍id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        long teamId = teamService.addTeam(teamAddRequest, request);
        return ReturnUtil.success(teamId);
    }

    /**
     * 查询队伍
     *
     * @param teamSearchRequest 查询条件
     * @param request 前端http请求
     * @return 队伍和队长的封装类
     */
    @PostMapping("/search")
    public BaseResponse<List<TeamUserVO>> searchTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request) {
        if (teamSearchRequest == null || request == null) {
            throw new BusinessException(PARAMS_NULL_ERROR);
        }
        List<TeamUserVO>  teamUserList = teamService.searchTeam(teamSearchRequest, request);
        return ReturnUtil.success(teamUserList);
    }

}
