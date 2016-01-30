package com.sarahproto.storeleaks.Response;

public class LoginDataResult {
    private String id;
    private String login;
    private String pass;
    private String username;
    private String avatar;
    private String data_reg;
    private String date_visit;
    private String md5;
    private String confirm;

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getData_reg() {
        return data_reg;
    }

    public String getDate_visit() {
        return date_visit;
    }

    public String getMd5() {
        return md5;
    }

    public String getConfirm() {
        return confirm;
    }

    @Override
    public String toString() {
        return "LoginDataResult{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", pass='" + pass + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", data_reg='" + data_reg + '\'' +
                ", date_visit='" + date_visit + '\'' +
                ", md5='" + md5 + '\'' +
                ", confirm='" + confirm + '\'' +
                '}';
    }
}