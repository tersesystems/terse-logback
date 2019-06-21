package com.tersesystems.logback.audio;

public class PlayerException extends RuntimeException {
    public PlayerException() {
    }

    public PlayerException(String s) {
        super(s);
    }

    public PlayerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PlayerException(Throwable throwable) {
        super(throwable);
    }

    public PlayerException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
