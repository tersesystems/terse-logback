package com.tersesystems.logback.uniqueid;

import com.tersesystems.logback.core.Component;

/** This interface returns a unique id identifying the entity. */
public interface UniqueIdProvider extends Component {
  String uniqueId();
}
