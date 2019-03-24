package com.kidsoncoffee.paramtests.runner;

import com.kidsoncoffee.paramtests.Scenario;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ParameterizedTestCase {
    String getName();

    Scenario getScenario();

    Optional<String> getBinding();
}
