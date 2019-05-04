package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.domain.ExampleRunner;
import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunner;
import com.kidsoncoffee.cheesecakes.runner.example.ExamplesLoader;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creates all {@link ScenarioRunner}s from a test class.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioRunnerCreator {

  /** The example loaders. */
  private final ExamplesLoader[] exampleLoaders;

  /**
   * Constructs a {@link ScenarioRunnerCreator} with the parameters.
   *
   * @param exampleLoaders The example loaders.
   */
  public ScenarioRunnerCreator(final ExamplesLoader... exampleLoaders) {
    this.exampleLoaders = exampleLoaders;
  }

  /**
   * Returns all {@link ScenarioRunner}s for the {@link TestClass}. Loads examples from all
   * registered {@link ExamplesLoader} and then group by the scenario method name and create {@link
   * org.junit.runner.Runner}s following the hierarchy, in which a scenario may have one or more
   * examples.
   *
   * @param testClass The test class.
   * @return The {@link ScenarioRunner}s for the {@link TestClass} parameter.
   * @throws InitializationError If an error occurs while initializing the {@link ScenarioRunner} or
   *     {@link ExampleRunner}.
   */
  public List<ScenarioRunner> create(final TestClass testClass) throws InitializationError {
    final Map<String, List<Example.Builder>> examplesByScenario =
        Arrays.stream(this.exampleLoaders)
            .map(examplesLoader -> examplesLoader.load(testClass.getJavaClass()))
            .flatMap(Collection::stream)
            .collect(Collectors.groupingBy(Example.Builder::getScenarioMethodName));

    final List<ScenarioRunner> scenarioRunners = new ArrayList<>();

    for (final String scenario : examplesByScenario.keySet()) {
      final List<Example.Builder> examples = examplesByScenario.get(scenario);

      final List<ExampleRunner> exampleRunners = new ArrayList<>();
      for (Example.Builder example : examples) {
        exampleRunners.add(new ExampleRunner(testClass.getJavaClass(), example));
      }
      scenarioRunners.add(new ScenarioRunner(testClass.getJavaClass(), scenario, exampleRunners));
    }

    return Collections.unmodifiableList(scenarioRunners);
  }
}
