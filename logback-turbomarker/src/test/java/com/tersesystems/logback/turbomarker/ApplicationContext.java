package com.tersesystems.logback.turbomarker;

public class ApplicationContext {

    private final String userId;

    public ApplicationContext(String userId) {
        this.userId = userId;
    }

    public String currentUserId() {
        return userId;
    }
}
