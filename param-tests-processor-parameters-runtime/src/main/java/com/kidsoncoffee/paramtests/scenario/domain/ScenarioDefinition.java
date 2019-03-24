package com.kidsoncoffee.paramtests.scenario.domain;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ScenarioDefinition {
  String getTestClassName();

  String getTestClassPackage();

  String getTestMethodName();

  List<ScenarioBlockDefinition> getRequisites();

  List<ScenarioBlockDefinition> getExpectations();
}
