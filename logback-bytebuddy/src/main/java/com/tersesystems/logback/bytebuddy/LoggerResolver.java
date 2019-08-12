package com.tersesystems.logback.bytebuddy;

import org.slf4j.Logger;

public interface LoggerResolver {
    Logger resolve(String origin);
}
