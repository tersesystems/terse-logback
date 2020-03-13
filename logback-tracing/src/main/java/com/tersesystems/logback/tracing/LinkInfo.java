/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */

package com.tersesystems.logback.tracing;

import com.google.auto.value.AutoValue;

/** https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#links */
@AutoValue
public abstract class LinkInfo {

  public static Builder builder() {
    return new AutoValue_LinkInfo.Builder();
  }

  public abstract Builder toBuilder();

  @Nullable
  public abstract String parentId();

  public abstract String traceId();

  public abstract String linkedSpanId();

  public abstract String linkedTraceId();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setLinkedSpanId(String linkedSpanId);

    public abstract Builder setLinkedTraceId(String linkedTraceId);

    public abstract Builder setParentId(String parentId);

    public abstract Builder setTraceId(String traceId);

    public abstract LinkInfo build();
  }
}
