package com.tersesystems.logback.turbomarker;

public class UserMarkerAware extends ContextAwareTurboMarker<ApplicationContext, UserMarkerFactory> {
    public UserMarkerAware(String name, ApplicationContext applicationContext, UserMarkerFactory factory) {
        super(name, applicationContext, factory);
    }
}
