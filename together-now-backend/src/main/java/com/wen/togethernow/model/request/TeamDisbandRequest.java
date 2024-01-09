package com.wen.togethernow.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 解散队伍请求参数封装类
 *
 * @author wen
 */
@Data
public class TeamDisbandRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1764098786743891528L;
}
