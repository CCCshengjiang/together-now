package com.wen.togethernow.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 加入队伍参数请求封装类
 *
 * @author wem
 */
@Data
public class TeamJoinRequest implements Serializable {

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍密码
     */
    private String teamPassword;

    @Serial
    private static final long serialVersionUID = 1882322256894419669L;
}
