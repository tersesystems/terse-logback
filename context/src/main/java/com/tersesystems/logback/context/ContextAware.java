package com.tersesystems.logback.context;

import org.slf4j.Marker;

public interface ContextAware<M extends Marker, C extends Context<M, C>> {
    C getContext();
}
