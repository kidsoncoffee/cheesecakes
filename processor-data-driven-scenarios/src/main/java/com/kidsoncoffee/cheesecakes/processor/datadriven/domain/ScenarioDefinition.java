package com.kidsoncoffee.cheesecakes.processor.datadriven.domain;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ScenarioDefinition {
  String getTestMethodName();

  List<ScenarioExampleDefinition> getExamples();
}
