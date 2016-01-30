package com.sarahproto.storeleaks.Response;

public class UserRegisterResponse {
    private Boolean error;
    private String message;

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UserRegisterResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}