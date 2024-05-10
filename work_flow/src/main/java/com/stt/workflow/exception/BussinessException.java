package com.stt.workflow.exception;

/**
 * @description: 业务异常
 * @author: shaott
 * @create: 2024-05-10 11:04
 * @Version 1.0
 **/
public class BussinessException extends RuntimeException {

    public BussinessException() {
    }

    public BussinessException(String message) {
        super(message);
    }

    public BussinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BussinessException(Throwable cause) {
        super(cause);
    }
}
