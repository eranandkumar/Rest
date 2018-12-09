package com.rest.ws.exception;

public enum ErrorMessages {
    MISSING_REQUIRED_FIELD("Missing Required field. Please check documentation for required fields"),
    RECORD_ALREAY_EXIST("Record Already exist"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    NO_RECORD_FOUND("Record with provided id not Found"),
    AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email Address could not be verified");

    private String errorMessage;

    ErrorMessages(String errMessage){
        this.errorMessage = errMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
