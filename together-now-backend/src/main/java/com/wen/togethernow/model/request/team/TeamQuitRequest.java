package com.wen.togethernow.model.request.team;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户退出的参数请求封装类
 *
 * @author wen
 */
@Data
public class TeamQuitRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 4227432850887935501L;
}
