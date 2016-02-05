package com.sarahproto.storeleaks.Response;

import java.util.List;

public class UserLoginResponse {
    private Boolean error;
    private List<LoginDataResult> data;
    private String message;

    public Boolean getError() {
        return error;
    }

    public List<LoginDataResult> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UserLoginResponse{" +
                "error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}