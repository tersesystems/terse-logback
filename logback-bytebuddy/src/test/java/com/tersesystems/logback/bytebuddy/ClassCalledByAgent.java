package com.tersesystems.logback.bytebuddy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

// This is a class we're going to redefine completely.
public class ClassCalledByAgent {
    public void doesNotUseLogging() {
        System.out.println("I am a simple println method with no logging");
    }

    public void printArgument(String arg) {
        System.out.println("I am a simple println, printing " + arg);
    }

    public void throwException(String arg) {
        throw new RuntimeException("I'm a squirrel!");
    }

    public CompletionStage<Integer> printFuture() {
        return CompletableFuture.supplyAsync(() -> Math.toIntExact(System.currentTimeMillis()));
    }
}
