package com.kidsoncoffee.paramtests;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ParameterizedTest {
  String getName();

  TestCaseParameters getParameters();

  Optional<String> getBinding();
}
