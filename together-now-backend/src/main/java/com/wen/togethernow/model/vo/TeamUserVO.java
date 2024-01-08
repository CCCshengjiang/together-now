package com.wen.togethernow.model.vo;

import com.wen.togethernow.model.domain.User;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 队伍信息和队长信息封装类
 *
 * @author wen
 */
@Data
public class TeamUserVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamProfile;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队长的id
     */
    private Long userId;

    /**
     * 状态，0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 队长
     */
    private User captainUser;

    @Serial
    private static final long serialVersionUID = 7247578120918805413L;
}
