package com.kidsoncoffee.cheesecakes.processor.domain;

import com.kidsoncoffee.cheesecakes.Scenario.StepType;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
@Value.Style(builder = "parameter")
public interface Parameter {
  String getName();

  TypeMirror getType();

  // TODO fchovich SHOULD THIS BE AT THE USER API?
  StepType getStepType();

  int getOverallOrder();
}
