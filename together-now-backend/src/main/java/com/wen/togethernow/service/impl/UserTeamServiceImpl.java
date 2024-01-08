package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.model.domain.UserTeam;
import com.wen.togethernow.service.UserTeamService;
import com.wen.togethernow.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author wen
* @ description 针对表【user_team(用户-队伍关系表)】的数据库操作Service实现
* @ createDate 2024-01-08 13:57:54
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




