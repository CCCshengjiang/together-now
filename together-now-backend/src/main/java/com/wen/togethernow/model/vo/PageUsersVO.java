package com.wen.togethernow.model.vo;

import com.wen.togethernow.model.domain.User;
import lombok.Data;

import java.util.List;

/**
 *
 * 分页展示数据的封装类
 *
 * @author wen
 */
@Data
public class PageUsersVO {
    /**
     * 脱敏的用户信息
     */
    private List<User> safetyUsers;

    /**
     * 用户总量
     */
    private int totalUsers;
}
