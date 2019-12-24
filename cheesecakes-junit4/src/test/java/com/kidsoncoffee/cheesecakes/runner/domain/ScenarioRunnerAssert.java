package com.kidsoncoffee.cheesecakes.runner.domain;

import com.kidsoncoffee.cheesecakes.Example;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.Iterables;
import org.assertj.core.internal.Objects;
import org.junit.runners.model.TestClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a custom {@link AbstractAssert} for {@link ScenarioRunner}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioRunnerAssert extends AbstractAssert<ScenarioRunnerAssert, ScenarioRunner> {

  /**
   * Initializes assertion instance for the {@link ScenarioRunner}.
   *
   * @param runner The {@link ScenarioRunner} to start assertion.
   * @return The assertion instance.
   */
  public static ScenarioRunnerAssert assertThat(final ScenarioRunner runner) {
    return new ScenarioRunnerAssert(runner);
  }

  /** The assertion helper for iterables. */
  private final Iterables iterables = Iterables.instance();

  /** The assertion helper for objects. */
  private final Objects objects = Objects.instance();

  /**
   * Constructs a {@link ScenarioRunnerAssert} with the {@link ScenarioRunner}.
   *
   * @param scenarioRunner The {@link ScenarioRunner} to assert.
   */
  private ScenarioRunnerAssert(final ScenarioRunner scenarioRunner) {
    super(scenarioRunner, ScenarioRunnerAssert.class);
  }

  /**
   * Asserts that a {@link ScenarioRunner} has the expected number of {@link ExampleRunner}s.
   *
   * @param size The expected number of {@link ExampleRunner}s.
   * @return The assertion instance for chaining.
   */
  public ScenarioRunnerAssert hasExampleRunners(final int size) {
    this.isNotNull();

    this.iterables.assertHasSize(this.info, this.actual.getChildren(), size);

    return this;
  }

  /**
   * Asserts that a {@link ScenarioRunner} has the {@link Example.Builder} inside of its {@link
   * ExampleRunner}s in the exact order.
   *
   * @param examples The expected examples.
   * @return The assertion instance for chaining.
   */
  public ScenarioRunnerAssert containsExamplesExactly(final Example.Builder... examples) {
    this.isNotNull();

    final List<Example.Builder> actualExamples =
        this.actual.getChildren().stream()
            .map(ExampleRunner::getExample)
            .collect(Collectors.toList());

    this.iterables.assertContainsExactly(this.info, actualExamples, examples);

    return this;
  }

  /**
   * Asserts that a {@link ScenarioRunner} has the expected name.
   *
   * @param name The expected name.
   * @return The assertion instance for chaining.
   */
  public ScenarioRunnerAssert hasName(final String name) {
    this.isNotNull();

    this.objects.assertEqual(this.info, this.actual.getName(), name);

    return this;
  }

  /**
   * Asserts that a {@link ScenarioRunner} has the expected {@link TestClass}.
   *
   * @param testClass The expected test class.
   * @return The assertion instance for chaining.
   */
  public ScenarioRunnerAssert hasTestClass(final TestClass testClass) {
    this.isNotNull();

    this.objects.assertEqual(this.info, this.actual.getTestClass(), testClass);

    return this;
  }
}
