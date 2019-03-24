package com.kidsoncoffee.paramtests.scenario.domain;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ScenarioBlockDefinition {
  String getParameterName();

  TypeMirror getParameterType();
}
