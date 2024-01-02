package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.service.UserService;
import com.wen.togethernow.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @ description 针对表【user(用户)】的数据库操作Service实现
* @ createDate 2024-01-02 15:49:00
 * @author Cwb
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




