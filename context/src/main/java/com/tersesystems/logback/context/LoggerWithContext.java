package com.tersesystems.logback.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

public interface LoggerWithContext<M extends Marker, C extends Context<M, C>, THIS> extends Logger, ContextAware<M, C> {

     THIS withContext(C context);
}
