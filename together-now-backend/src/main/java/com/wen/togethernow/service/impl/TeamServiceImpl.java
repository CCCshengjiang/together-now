package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.service.TeamService;
import com.wen.togethernow.model.domain.Team;
import com.wen.togethernow.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author wen
* @ description 针对表【team(队伍表)】的数据库操作Service实现
* @ createDate 2024-01-08 13:54:39
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




