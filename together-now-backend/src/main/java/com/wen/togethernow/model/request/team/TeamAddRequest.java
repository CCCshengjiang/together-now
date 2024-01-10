package com.wen.togethernow.model.request;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 队伍添加请求参数包装类
 *
 * @author wen
 */
@Data
public class TeamAddRequest implements Serializable {
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
     * 状态，0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

    /**
     * 队伍密码
     */
    private String teamPassword;

    @Serial
    private static final long serialVersionUID = 2935509361810737902L;
}
