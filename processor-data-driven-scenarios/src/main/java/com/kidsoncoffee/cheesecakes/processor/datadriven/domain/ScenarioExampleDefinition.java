package com.kidsoncoffee.cheesecakes.processor.datadriven.domain;

import org.immutables.value.Value;

import java.util.Map;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ScenarioExampleDefinition {
    Map<String, String> getParameters();
}
