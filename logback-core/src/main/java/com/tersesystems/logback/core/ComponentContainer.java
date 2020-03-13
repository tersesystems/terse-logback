package com.tersesystems.logback.core;

/**
 * A component container.
 *
 * <p>Entries are encouraged but not required to extend Component.
 */
public interface ComponentContainer {
  <T> void putComponent(Class<T> type, T instance);

  <T> T getComponent(Class<T> type);

  <T> boolean hasComponent(Class<T> type);
}
