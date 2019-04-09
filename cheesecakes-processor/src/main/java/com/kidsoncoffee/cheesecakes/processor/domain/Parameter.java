package com.kidsoncoffee.cheesecakes.processor.domain;

import com.kidsoncoffee.cheesecakes.SpecificationStepType;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface Parameter {
  String getName();

  TypeMirror getType();

  // TODO fchovich SHOULD THIS BE AT THE USER API?
  SpecificationStepType getStepType();

  int getOverallOrder();
}
