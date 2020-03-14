package com.tersesystems.logback.classic;

import com.tersesystems.logback.core.Component;

public interface NanoTimeSupplier extends Component {
  long getNanoTime();
}
