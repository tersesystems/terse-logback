package com.tersesystems.logback.context;

import org.slf4j.Marker;

public interface ContextAware<T extends Marker> {
    Context<T> getContext();
}
