package com.bob.internalcommon.constant.dto;

import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@Data
@Accessors(chain = true)
public class ResponseResult<T> {
    private int code;
    private String message;
    private T data;

    public static ResponseResult success(){
        return new ResponseResult().setCode(CommonStatusEnum.SUCCESS.getCode()).setMessage(CommonStatusEnum.SUCCESS.getValue());
    }

    public static <T> ResponseResult success(T data){
        return new ResponseResult().setCode(CommonStatusEnum.SUCCESS.getCode()).setMessage(CommonStatusEnum.SUCCESS.getValue()).setData(data);
    }

    // 统一失败
    public static <T> ResponseResult fail(T data){
        return new ResponseResult().setData(data);
    }

    // 自定义失败：错误码，提示信息
    public static ResponseResult fail(int code, String message){
        return new ResponseResult().setCode(code).setMessage(message);
    }

    // 自定义失败：错误码，提示信息，具体信息
    public static ResponseResult fail(int code, String message, String err){
        return new ResponseResult().setCode(code).setMessage(message).setData(err);
    }



}
