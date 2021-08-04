package com.github.knightliao.vpaas.common.utils.exceptions;

/**
 * 断言
 *
 * @author knightliao
 * @date 2021/8/4 15:29
 */
public class AssertException extends RuntimeException {

    public AssertException() {
        super();
    }

    public AssertException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertException(String message) {
        super(message);
    }

    public AssertException(Throwable throwable) {
        super(throwable);
    }
}
