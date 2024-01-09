package com.wen.togethernow.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 队伍更新请求参数包装类
 *
 * @author wen
 */
@Data
public class TeamUpdateRequest implements Serializable {
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
     * 过期时间
     */
    private Date expireTime;

    /**
     * 状态，0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

    /**
     * 队伍密码
     */
    private String teamPassword;

    @Serial
    private static final long serialVersionUID = -793135974622838053L;
}
