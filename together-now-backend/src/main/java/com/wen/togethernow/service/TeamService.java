package com.wen.togethernow.service;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.togethernow.model.request.TeamAddRequest;
import com.wen.togethernow.model.request.TeamSearchRequest;
import com.wen.togethernow.model.request.TeamUpdateRequest;
import com.wen.togethernow.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author wen
* @ description 针对表【team(队伍表)】的数据库操作Service
* @ createDate 2024-01-08 15:18:03
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍的业务层接口
     *
     * @param teamAddRequest 队伍信息
     * @param request http请求
     * @return 创建的队伍id
     */
    long addTeam(TeamAddRequest teamAddRequest, HttpServletRequest request);

    /**
     * 查询队伍的业务层接口
     *
     * @param teamSearchRequest 查询条件
     * @param request 前端http请求
     * @return 队伍和队长的封装类
     */
    List<TeamUserVO> searchTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request);

}
