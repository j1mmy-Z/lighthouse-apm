package com.jimmy.lighthouse.apm.common.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    public boolean success;

    public String message;

    public T data;

    public static Result<Boolean> ok() {
        return new Result<>(true, "success", true);
    }

    public static Result<Boolean> error(String msg) {
        return new Result<>(false, msg, false);
    }
}
