package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.example.DataTableExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.ExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.parameter.ScenarioParametersConverter;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
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
 * @author fernando.chovich
 * @since 1.0
 */
public class Cheesecakes extends Suite {

  private final ScenarioParametersConverter scenarioParametersConverter;

  private final ExamplesLoader dataTableExamplesLoader;

  private final ExamplesLoader fieldExamplesLoader;

  private final List<Runner> runners;

  public Cheesecakes(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());

    // TODO fchovich USE GUAVA
    this.scenarioParametersConverter = new ScenarioParametersConverter();
    this.dataTableExamplesLoader = new DataTableExamplesLoader();
    this.fieldExamplesLoader = new FieldExamplesLoader();

    this.runners = createTestCaseRunners();
  }

  private List<Runner> createTestCaseRunners() throws InitializationError {
    final Map<String, List<Example.Builder>> examplesByScenario =
        getExamples(this.getTestClass(), this.dataTableExamplesLoader, this.fieldExamplesLoader);

    final List<Runner> scenarioRunners = new ArrayList<>();
    for (final String scenario : examplesByScenario.keySet()) {
      final List<Example.Builder> examples = examplesByScenario.get(scenario);
      final List<ExampleRunner> exampleRunners = new ArrayList<>();
      for (Example.Builder example : examples) {
        exampleRunners.add(
            new ExampleRunner(
                this.getTestClass().getJavaClass(),
                example.getScenarioMethodName(),
                example,
                this.scenarioParametersConverter));
      }
      scenarioRunners.add(new ScenarioRunner(this.getTestClass().getJavaClass(), scenario, exampleRunners));
    }

    /*final List<Runner> runners = new ArrayList<>();
    for (final Example.Builder example : examplesByScenario) {
      runners.add(
          new ExampleRunner(
              this.getTestClass().getJavaClass(),
              example.getScenarioMethodName(),
              example,
              this.scenarioParametersConverter));
    }*/

    return Collections.unmodifiableList(scenarioRunners);
  }

  private static Map<String, List<Example.Builder>> getExamples(
      final TestClass featureClass, final ExamplesLoader... examplesLoaders) {
    return Arrays.stream(examplesLoaders)
        .map(examplesLoader -> examplesLoader.load(featureClass.getJavaClass()))
        .flatMap(Collection::stream)
        .collect(Collectors.groupingBy(Example.Builder::getScenarioMethodName));
  }

  @Override
  protected List<Runner> getChildren() {
    return this.runners;
  }
}
