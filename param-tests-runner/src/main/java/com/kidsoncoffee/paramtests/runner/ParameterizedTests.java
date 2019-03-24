package com.kidsoncoffee.paramtests.runner;

import com.kidsoncoffee.paramtests.Scenario;
import com.kidsoncoffee.paramtests.ScenarioDefiner;
import com.kidsoncoffee.paramtests.annotations.Parameters;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterizedTests extends Suite {

  private final List<Runner> runners;

  public ParameterizedTests(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());
    this.runners = createParameterizedTest();
  }

  private List<Runner> createParameterizedTest() throws InitializationError {
    final List<Runner> runners = new ArrayList<>();

    // TODO fchovich USE TYPE INSTEAD OF ANNOTATION
    for (FrameworkField def : this.getTestClass().getAnnotatedFields(Parameters.Scenario.class)) {
      final List<Scenario> scenarios = retrieveScenarios(def);
      final Parameters.ScenarioBinding binding =
          def.getAnnotation(Parameters.ScenarioBinding.class);

      for (final Scenario scenario : scenarios) {
        final ParameterizedTestCase test =
            ImmutableParameterizedTestCase.builder()
                .name(def.getAnnotation(Parameters.Scenario.class).value())
                .scenario(scenario)
                .binding(binding != null ? Optional.of(binding.value()) : Optional.empty())
                .build();

        runners.add(new ParameterizedTestMethodRunner(getTestClass().getJavaClass(), test));
      }
    }
    return runners;
  }

  private static List<Scenario> retrieveScenarios(final FrameworkField field) {
    // TODO fchovich CREATE SUGARY INTERFACES
    if (!ScenarioDefiner.class.isAssignableFrom(field.getType())) {
      throw new IllegalArgumentException(
          String.format(
              "Test case definition '%s' of the wrong type '%s'.",
              field.getName(), field.getType()));
    }
    try {
      field.getField().setAccessible(true);
      final ScenarioDefiner definer = (ScenarioDefiner) field.get(null);
      return definer.getScenarios();
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          String.format("Unable to retrieve test case definition '%s'.", field.getName()), e);
    }
  }

  @Override
  protected List<Runner> getChildren() {
    return this.runners;
  }
}
