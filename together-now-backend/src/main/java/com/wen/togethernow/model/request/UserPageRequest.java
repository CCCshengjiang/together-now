package com.wen.togethernow.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页请求
 *
 * @author wen
 */
@Data
public class UserPageRequest implements Serializable {
    /**
     * 页码
     */
    private int pageNum;

    /**
     * 每页的数量
     */
    private int pageSize;

    @Serial
    private static final long serialVersionUID = -5681567894907233445L;
}
