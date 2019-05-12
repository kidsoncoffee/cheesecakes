package com.kidsoncoffee.cheesecakes.processor.aggregator.domain;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface FeatureToGenerate {
  String getTestClassName();

  String getTestClassPackage();

  List<ScenarioToGenerate> getScenarios();
}
