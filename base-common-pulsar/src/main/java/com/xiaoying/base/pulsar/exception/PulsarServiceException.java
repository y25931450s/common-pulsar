package com.xiaoying.base.pulsar.exception;


/**
 * @author yushuo
 */
public class PulsarServiceException extends RuntimeException {

    private ErrorMessage errorMessage = PulsarErrorMessage.OK;
    private Object data;
    private Exception exception;

    public PulsarServiceException(ErrorMessage errorMessage, Exception exception) {
        this(errorMessage, errorMessage.getMessage(), null, exception);
    }

    public PulsarServiceException(String errorMessage) {
        this(null, errorMessage);
    }

    public PulsarServiceException(ErrorMessage errorMessage) {
        this(errorMessage, null, null, null);
    }

    public PulsarServiceException(ErrorMessage errorMessage, String message) {
        this(errorMessage, message, null, null);
    }

    public PulsarServiceException(ErrorMessage errorMessage, String message, Object data, Exception exception) {
        super(message);
        this.errorMessage = errorMessage;
        this.data = data;
        this.exception = exception;
    }

}
