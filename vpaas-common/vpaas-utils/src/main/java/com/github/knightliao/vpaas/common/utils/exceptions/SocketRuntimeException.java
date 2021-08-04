package com.github.knightliao.vpaas.common.utils.exceptions;

/**
 * 断言
 *
 * @author knightliao
 * @date 2021/8/4 15:29
 */
public class SocketRuntimeException extends RuntimeException {

    public SocketRuntimeException() {
        super();
    }

    public SocketRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketRuntimeException(String message) {
        super(message);
    }

    public SocketRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
