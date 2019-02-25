package com.tersesystems.logback.censor;

/**
 * This interface potentially censors the contents of T, either in whole or in part.
 */
@FunctionalInterface
public interface Censor {
    CharSequence apply(CharSequence input);
}
