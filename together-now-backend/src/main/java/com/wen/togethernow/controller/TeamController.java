package com.wen.togethernow.controller;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.model.request.TeamAddRequest;
import com.wen.togethernow.service.TeamService;
import com.wen.togethernow.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 队伍接口
 *
 * @author wen
 */
@Controller
@RequestMapping("/team")
@CrossOrigin(origins = { "http://localhost:5173/" }, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<> addTeam(@RequestBody TeamAddRequest teamAddRequest) {

    }


}
