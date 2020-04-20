package com.ha.net.eautoopen.config.exception;

public class BussinessException extends RuntimeException{

    private String errorCode = null;
    private String errorMessage = null;

    public BussinessException() {
    }

    public BussinessException(String message) {
        super(message);
    }

    public BussinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BussinessException(String errorCode, String errorMessage, String statusCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BussinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
