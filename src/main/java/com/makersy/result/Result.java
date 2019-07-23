package com.makersy.result;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by makersy on 2019
 */

@Getter
@Setter
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    /**
     *  成功时候的调用
     * */
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }

    /**
     *  失败时候的调用
     * */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    private Result(T data) {
        this.data = data;
    }

    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(CodeMsg codeMsg) {
        if(codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

}

