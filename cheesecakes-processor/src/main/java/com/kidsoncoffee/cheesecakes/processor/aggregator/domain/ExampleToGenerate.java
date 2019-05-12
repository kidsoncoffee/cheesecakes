package com.kidsoncoffee.cheesecakes.processor.aggregator.domain;

import org.immutables.value.Value;

import java.util.Map;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ExampleToGenerate {
  Map<String, String> getValue();
}
