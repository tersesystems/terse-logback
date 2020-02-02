package com.tersesystems.logback.tracing;

import com.google.auto.value.AutoValue;

/**
 * An event info is a span without a duration. It cannot be used as a parent.
 *
 * <p>https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#span-events
 */
@AutoValue
public abstract class EventInfo {

  public static Builder builder() {
    return new AutoValue_EventInfo.Builder();
  }

  public abstract Builder toBuilder();

  @Nullable
  public abstract String parentId();

  public abstract String traceId();

  public abstract String name();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setName(String name);

    public abstract Builder setParentId(String parentId);

    public abstract Builder setTraceId(String traceId);

    public abstract EventInfo build();
  }
}
