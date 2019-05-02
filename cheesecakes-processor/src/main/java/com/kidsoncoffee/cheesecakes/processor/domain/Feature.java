package com.kidsoncoffee.cheesecakes.processor.domain;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
@Value.Style(builder = "feature")
public interface Feature {
  String getTestClassName();

  String getTestClassPackage();

  List<Scenario> getScenarios();
}
