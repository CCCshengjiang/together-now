package com.wen.togethernow.exception;

import com.wen.togethernow.common.BaseResponse;
import com.wen.togethernow.common.utils.ReturnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.wen.togethernow.common.BaseCode.INTERNAL_ERROR;

/**
 * 全局异常处理器
 *
 * @author wen
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param e 业务异常
     * @return 返回通用返回类
     * @param <T> 泛型
     */
    @ExceptionHandler(BusinessException.class)
    public <T> BaseResponse<T> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException：" + e.getMessage(), e);
        return ReturnUtil.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    /**
     * 捕获运行时异常
     *
     * @param e 运行时异常
     * @return 通用返回类
     * @param <T> 泛型
     */
    @ExceptionHandler(RuntimeException.class)
    public <T> BaseResponse<T> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ReturnUtil.error(INTERNAL_ERROR, e.getMessage());
    }
}
