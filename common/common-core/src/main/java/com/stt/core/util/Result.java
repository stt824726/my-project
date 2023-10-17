package com.stt.core.util;

import com.stt.core.constant.DataConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "响应信息主体")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回标记：成功标记=0，失败标记=1")
    private int code;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回信息")
    private String msg;

    @Getter
    @Setter
    @ApiModelProperty(value = "数据")
    private T data;

    public static <T> Result<T> ok() {
        return restResult(null, DataConstant.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, DataConstant.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data, String msg) {
        return restResult(data, DataConstant.SUCCESS, msg);
    }

    public static <T> Result<T> failed() {
        return restResult(null, DataConstant.FAIL, null);
    }

    public static <T> Result<T> failed(String msg) {
        return restResult(null, DataConstant.FAIL, msg);
    }

    public static <T> Result<T> failed(T data) {
        return restResult(data, DataConstant.FAIL, null);
    }

    public static <T> Result<T> failed(T data, String msg) {
        return restResult(data, DataConstant.FAIL, msg);
    }


    private static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
