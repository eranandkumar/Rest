package com.rest.ws.exception;

public class UserServiceException extends RuntimeException {

    public UserServiceException(String message){
        super(message);
    }
}
