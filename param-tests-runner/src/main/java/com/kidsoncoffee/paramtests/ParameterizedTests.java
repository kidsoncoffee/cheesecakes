package com.kidsoncoffee.paramtests;

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
    for (FrameworkField def : this.getTestClass().getAnnotatedFields(TestCaseDefinition.class)) {
      final TestCaseParameters parameters = getTestCaseParameters(def);
      final TestCaseBinding binding = def.getAnnotation(TestCaseBinding.class);
      final ParameterizedTest test =
          ImmutableParameterizedTest.builder()
              .name(parameters.toString())
              .parameters(parameters)
              .binding(binding != null ? Optional.of(binding.value()) : Optional.empty())
              .build();
      runners.add(new ParameterizedTestMethodRunner(getTestClass().getJavaClass(), test));
    }
    return runners;
  }

  private TestCaseParameters getTestCaseParameters(FrameworkField def) {
    // TODO fchovich CREATE SUGARY INTERFACES
    if (!def.getType().isAssignableFrom(TestCaseParameters.class)
        && !def.getType().isAssignableFrom(TestCaseParametersBlock.class)) {
      throw new IllegalArgumentException(
          String.format(
              "Test case definition '%s' of the wrong type '%s'.", def.getName(), def.getType()));
    }
    try {
      def.getField().setAccessible(true);
      final Object defValue = def.get(null);
      return TestCaseParameters.class.isInstance(defValue)
          ? (TestCaseParameters) defValue
          : ((TestCaseParametersBlock) defValue).getTestCase();
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          String.format("Unable to retrieve test case definition '%s'.", def.getName()), e);
    }
  }

  @Override
  protected List<Runner> getChildren() {
    return this.runners;
  }
}
