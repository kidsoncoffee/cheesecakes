package com.kidsoncoffee.cheesecakes.processor.datadriven.domain;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface FeatureDefinition {
  String getTestClassPackage();

  String getTestClassName();

  List<ScenarioDefinition> getScenarios();
}
