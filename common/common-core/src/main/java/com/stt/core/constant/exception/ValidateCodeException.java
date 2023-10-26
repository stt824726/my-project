package com.stt.core.constant.exception;

/**
 * 校验异常
 */
public class ValidateCodeException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    protected final String message;

    public ValidateCodeException(String message)
    {
        this.message = message;
    }

    public ValidateCodeException(String message, Throwable e)
    {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
