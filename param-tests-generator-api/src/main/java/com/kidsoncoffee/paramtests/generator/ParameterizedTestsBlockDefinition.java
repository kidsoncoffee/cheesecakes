package com.kidsoncoffee.paramtests.generator;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ParameterizedTestsBlockDefinition {
  // TODO fchovich REMOVE AND USE REFLECTION
  int getOverallOrder();

  String getMethodName();

  String getParameterName();

  TypeMirror getType();
}
