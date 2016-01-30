package com.sarahproto.storeleaks.Response;

public class AddItemResponse {
    private String error;
    private String message;
    private String itemId;

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        return "AddItemResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", itemId='" + itemId + '\'' +
                '}';
    }
}
