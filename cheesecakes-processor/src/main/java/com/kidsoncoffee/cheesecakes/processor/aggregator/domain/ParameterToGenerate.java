package com.kidsoncoffee.cheesecakes.processor.aggregator.domain;

import com.kidsoncoffee.cheesecakes.Scenario.StepType;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ParameterToGenerate {
  String getName();

  TypeMirror getType();

  // TODO fchovich SHOULD THIS BE AT THE USER API?
  StepType getStepType();

  int getOverallOrder();
}
