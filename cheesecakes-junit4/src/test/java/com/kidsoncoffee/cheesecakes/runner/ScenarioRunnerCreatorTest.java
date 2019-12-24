package com.kidsoncoffee.cheesecakes.runner;

import com.google.common.collect.Sets;
import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunner;
import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunnerAssert;
import com.kidsoncoffee.cheesecakes.runner.example.ExamplesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ScenarioRunnerCreator}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioRunnerCreatorTest {

  /** The name for a particular scenario. */
  private static final String SCENARIO_A = "scenarioA";

  /** The name for a particular scenario. */
  private static final String SCENARIO_B = "scenarioB";

  /** The first loader dependency. */
  private ExamplesLoader firstLoader;

  /** The second loader dependency. */
  private ExamplesLoader secondLoader;

  /** The unit under test. */
  private ScenarioRunnerCreator creator;

  /** Setup the unit under test and its dependencies. */
  @Before
  public void setup() {
    this.firstLoader = mock(ExamplesLoader.class, "firstLoader");
    this.secondLoader = mock(ExamplesLoader.class, "secondLoader");
    this.creator = new ScenarioRunnerCreator(Sets.newHashSet(this.firstLoader, this.secondLoader));
  }

  /** Verify that all mocks interactions were accounted for. */
  @After
  public void verifyMocks() {
    verifyNoMoreInteractions(this.firstLoader, this.secondLoader);
  }

  /**
   * Checks that an empty list of runners is returned if no examples are loaded by {@link
   * ExamplesLoader}s.
   *
   * @throws InitializationError If any error occurs while creating runners.
   */
  @Test
  public void noExamples() throws InitializationError {
    final List<ScenarioRunner> runners;

    orchestrate:
    when(this.firstLoader.load(ScenarioRunnerCreatorTest.class)).thenReturn(emptyList());
    when(this.secondLoader.load(ScenarioRunnerCreatorTest.class)).thenReturn(emptyList());

    when:
    runners = this.creator.create(new TestClass(ScenarioRunnerCreatorTest.class));

    then:
    assertThat(runners)
        .as("Each example for different scenarios should be transformed into runners.")
        .isEmpty();

    verification:
    verify(this.firstLoader, times(1)).load(ScenarioRunnerCreatorTest.class);
    verify(this.secondLoader, times(1)).load(ScenarioRunnerCreatorTest.class);
  }

  /**
   * Checks that the {@link ScenarioRunnerCreator} is able to put {@link Example.Builder} with the
   * same scenario name under one {@link ScenarioRunner}.
   *
   * @throws InitializationError If any error occurs while creating runners.
   */
  @Test
  public void sameScenarioExamples() throws InitializationError {
    final TestClass testClass;
    final Example.Builder example;

    final List<ScenarioRunner> runners;

    given:
    example = new Example.Builder(null, SCENARIO_A, null);
    testClass = new TestClass(ScenarioRunnerCreatorTest.class);

    orchestrate:
    when(this.firstLoader.load(ScenarioRunnerCreatorTest.class)).thenReturn(singletonList(example));
    when(this.secondLoader.load(ScenarioRunnerCreatorTest.class))
        .thenReturn(singletonList(example));

    when:
    runners = this.creator.create(testClass);

    then:
    assertThat(runners)
        .as("Only one scenario should be created since both example have the same names.")
        .hasSize(1);

    ScenarioRunnerAssert.assertThat(runners.get(0))
        .as("The name of the scenario should be the loaded one")
        .hasName(SCENARIO_A)
        .as("Both examples loaded are related to the same scenario method.")
        .hasExampleRunners(2)
        .as("The examples loaded should both be part of the same scenario.")
        .containsExamplesExactly(example, example)
        .as("The test class should be the informed.")
        .hasTestClass(testClass);

    verification:
    verify(this.firstLoader, times(1)).load(ScenarioRunnerCreatorTest.class);
    verify(this.secondLoader, times(1)).load(ScenarioRunnerCreatorTest.class);
  }

  /**
   * Checks that the {@link ScenarioRunnerCreator} is able to separate {@link ScenarioRunner}s
   * according to different scenario method names from {@link Example.Builder}s.
   *
   * @throws InitializationError If any error occurs while creating runners.
   */
  @Test
  public void differentScenarioExamples() throws InitializationError {
    final TestClass testClass;
    final Example.Builder exampleA, exampleB;

    final List<ScenarioRunner> runners;

    given:
    testClass = new TestClass(ScenarioRunnerCreatorTest.class);
    exampleA = new Example.Builder(null, SCENARIO_A, null);
    exampleB = new Example.Builder(null, SCENARIO_B, null);

    orchestrate:
    when(this.firstLoader.load(ScenarioRunnerCreatorTest.class))
        .thenReturn(singletonList(exampleA));
    when(this.secondLoader.load(ScenarioRunnerCreatorTest.class))
        .thenReturn(singletonList(exampleB));

    when:
    runners = this.creator.create(testClass);

    then:
    assertThat(runners)
        .as("Both examples have different scenario method names, so two runners should be created.")
        .hasSize(2);

    ScenarioRunnerAssert.assertThat(runners.get(0))
        .as("The name of the scenario should be the loaded one")
        .hasName(SCENARIO_A)
        .as("Only one example is related to the same scenario method name.")
        .hasExampleRunners(1)
        .as("The examples loaded should both be part of the same scenario.")
        .containsExamplesExactly(exampleA)
        .as("The test class should be the informed.")
        .hasTestClass(testClass);

    ScenarioRunnerAssert.assertThat(runners.get(1))
        .as("The name of the scenario should be the loaded one")
        .hasName(SCENARIO_B)
        .as("Only one example is related to the same scenario method name.")
        .hasExampleRunners(1)
        .as("The examples loaded should both be part of the same scenario.")
        .containsExamplesExactly(exampleB)
        .as("The test class should be the informed.")
        .hasTestClass(testClass);

    verification:
    verify(this.firstLoader, times(1)).load(ScenarioRunnerCreatorTest.class);
    verify(this.secondLoader, times(1)).load(ScenarioRunnerCreatorTest.class);
  }
}
