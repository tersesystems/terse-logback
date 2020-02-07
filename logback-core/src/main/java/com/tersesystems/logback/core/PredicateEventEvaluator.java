package com.tersesystems.logback.core;

import static java.util.Objects.requireNonNull;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import java.util.function.Predicate;

public class PredicateEventEvaluator<I> extends EventEvaluatorBase<I> {
  private final Predicate<I> evaluateFunction;

  public PredicateEventEvaluator(Predicate<I> fn) {
    this.evaluateFunction = requireNonNull(fn);
  }

  @Override
  public boolean evaluate(I event) throws NullPointerException, EvaluationException {
    return evaluateFunction.test(event);
  }
}
