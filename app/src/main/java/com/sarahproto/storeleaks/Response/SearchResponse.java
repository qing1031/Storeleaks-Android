package com.sarahproto.storeleaks.Response;

import java.util.List;

public class SearchResponse {
    private Boolean error;
    private List<SearchItemResult> data;

    public Boolean getError() {
        return error;
    }

    public List<SearchItemResult> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "error=" + error +
                ", data=" + data +
                '}';
    }
}
