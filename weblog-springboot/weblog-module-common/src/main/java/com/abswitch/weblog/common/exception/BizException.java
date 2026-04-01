package com.abswitch.weblog.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-17 11:47
 * @Description：
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface){

        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }
}
