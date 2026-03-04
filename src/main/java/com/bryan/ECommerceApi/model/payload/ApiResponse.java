package com.bryan.ECommerceApi.model.payload;

import java.time.Instant;

public class ApiResponse {
    private String uri;
    private Instant time;
    private String message;

    public ApiResponse(String url, String message) {
        this.uri = url.replace("uri=", "") ;
        time = Instant.now();
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
