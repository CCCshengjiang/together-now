package com.wen.togethernow.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用的分页请求参数
 * 
 * @author wen
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 页码
     */
    protected int pageNum;

    /**
     * 每页的数量
     */
    protected int pageSize;

    @Serial
    private static final long serialVersionUID = 3936179545360587820L;
}