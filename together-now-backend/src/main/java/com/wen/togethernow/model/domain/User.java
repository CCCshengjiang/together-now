package com.wen.togethernow.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户实体类
 * @ TableName user
 * @author wen
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像，url地址存储
     */
    private String avatarUrl;

    /**
     * 性别，0-女，1-男
     */
    private Integer gender;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态，0-正常，1-不正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除，0-未删除，1-删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色，0-普通用户，1-管理员
     */
    private Integer userRole;

    /**
     * 编号
     */
    private String idCode;

    /**
     * 用户标签 JSON
     */
    private String tags;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}