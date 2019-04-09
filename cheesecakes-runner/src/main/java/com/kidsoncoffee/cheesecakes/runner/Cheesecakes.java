package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.DataDrivenScenario;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
import com.kidsoncoffee.cheesecakes.SpecificationReference;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class Cheesecakes extends Suite {

  private final TestCaseInjectablesResolver testBinder;

  private final List<Runner> runners;

  public Cheesecakes(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());
    this.testBinder = new TestCaseInjectablesResolver();
    this.runners = createTestCaseRunners();
  }

  private List<Runner> createTestCaseRunners() throws InitializationError {
    final List<Runner> runners = new ArrayList<>();

    // TODO fchovich USE TYPE INSTEAD OF ANNOTATION
    for (FrameworkField def : this.getTestClass().getAnnotatedFields(Parameters.Scenario.class)) {
      final List<Specification> specifications = retrieveScenarios(def);
      final Parameters.ScenarioBinding testMethodBinding =
          def.getAnnotation(Parameters.ScenarioBinding.class);

      for (final Specification specification : specifications) {
        final TestCase test =
            ImmutableTestCase.builder()
                .name(def.getAnnotation(Parameters.Scenario.class).value())
                .specification(specification)
                .binding(Optional.ofNullable(testMethodBinding))
                .isDataDriven(false)
                .build();

        runners.add(new TestMethodRunner(getTestClass().getJavaClass(), test, this.testBinder));
      }
    }

    for (final FrameworkMethod def :
        this.getTestClass().getAnnotatedMethods(Parameters.DataDriven.class)) {
      final Pair<Parameters.ScenarioBinding, List<Specification>> specifications =
          retrieveDataDrivenScenarios(def);

      for (Specification specification : specifications.getRight()) {
        final ImmutableTestCase test =
            ImmutableTestCase.builder()
                .name(def.getAnnotation(Parameters.DataDriven.class).value())
                .specification(specification)
                .binding(Optional.of(specifications.getLeft()))
                // TODO fchovich IS IT NECESSARY?
                .isDataDriven(true)
                .build();

        runners.add(new TestMethodRunner(getTestClass().getJavaClass(), test, this.testBinder));
      }
    }

    return runners;
  }

  private static Pair<Parameters.ScenarioBinding, List<Specification>> retrieveDataDrivenScenarios(
      final FrameworkMethod method) {
    final String testClassPackage = method.getDeclaringClass().getPackage().getName();
    final Reflections reflections = new Reflections(testClassPackage);
    final Set<Class<? extends DataDrivenScenario>> dataDrivenScenarios =
        reflections.getSubTypesOf(DataDrivenScenario.class);
    final Method bindingMethod =
        dataDrivenScenarios.stream()
            .map(Class::getDeclaredMethods)
            .flatMap(Arrays::stream)
            .filter(m -> m.isAnnotationPresent(Parameters.ScenarioBinding.class))
            .filter(
                m ->
                    m.getAnnotation(Parameters.ScenarioBinding.class)
                        .testClass()
                        .equals(method.getDeclaringClass()))
            .filter(
                m ->
                    m.getAnnotation(Parameters.ScenarioBinding.class)
                        .testMethod()
                        .equalsIgnoreCase(method.getName()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(""));
    try {
      bindingMethod.setAccessible(true);
      return Pair.of(
          bindingMethod.getAnnotation(Parameters.ScenarioBinding.class),
          (List<Specification>) bindingMethod.invoke(null));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("", e);
    }
  }

  private static List<Specification> retrieveScenarios(final FrameworkField field) {
    // TODO fchovich CREATE SUGARY INTERFACES
    if (!SpecificationReference.class.isAssignableFrom(field.getType())) {
      throw new IllegalArgumentException(
          String.format(
              "Test case definition '%s' of the wrong type '%s'.",
              field.getName(), field.getType()));
    }
    try {
      field.getField().setAccessible(true);
      final SpecificationReference references = (SpecificationReference) field.get(null);
      return references.getSpecificationReferences();
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
