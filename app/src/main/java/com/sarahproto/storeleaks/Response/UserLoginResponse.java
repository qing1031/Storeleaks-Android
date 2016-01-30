package com.sarahproto.storeleaks.Response;

import java.util.List;

public class UserLoginResponse {
    private Boolean error;
    private List<LoginDataResult> data;

    public Boolean getError() {
        return error;
    }

    public List<LoginDataResult> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "UserLoginResponse{" +
                "error='" + error + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}