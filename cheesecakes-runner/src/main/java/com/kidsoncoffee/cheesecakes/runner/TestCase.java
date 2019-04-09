package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface TestCase {
    String getName();

    Specification getSpecification();

    Optional<Parameters.ScenarioBinding> getBinding();

    boolean isDataDriven();
}
