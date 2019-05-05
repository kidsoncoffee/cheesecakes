package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.example.method.ExampleSource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ClassExamplesLoader}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ClassExamplesLoaderTest {

  /** The unit under test. */
  private ClassExamplesLoader loader;

  /** Setup the unit under test. */
  @Before
  public void setup() {
    this.loader = new ClassExamplesLoader();
  }

  /**
   * Checks that an empty list is returned if there are no example suppliers in the package of the
   * target feature class matching the target feature class.
   */
  @Test
  public void noExamples() {
    final Class<String> featureClass;
    final List<Example.Builder> examples;

    given:
    featureClass = String.class;

    when:
    examples = this.loader.load(featureClass);

    then:
    assertThat(examples)
        .as("No examples sources in the String package should not create examples.")
        .isEmpty();
  }

  /**
   * Checks that a list of examples is returned for the target feature class. Uses {@link
   * ExampleSource} as the supplier of example.
   */
  @Test
  public void suppliedExamplesForTheSameTargetClass() {
    final Class<ClassExamplesLoaderTest> featureClass;
    final List<Example.Builder> examples;

    given:
    featureClass = ClassExamplesLoaderTest.class;

    when:
    examples = this.loader.load(featureClass);

    then:
    assertThat(examples)
        .as("The class has one valid supplier for the target test class.")
        .hasSize(1);

    assertThat(examples.get(0).getFeatureClass())
        .as("The feature class is the target test class.")
        .isEqualTo(featureClass);

    assertThat(examples.get(0).getScenarioMethodName())
        .as("The scenario method name is the one supplied for the target test class.")
        .isEqualTo(ExampleSource.EXAMPLE_SUPPLIER_STATIC_TEST_CLASS);
  }

  /**
   * Checks that a list of examples is returned for the target feature class.Uses {@link
   * ExampleSource} as the supplier of example.
   */
  @Test
  public void suppliedExamplesForTheSameDifferentTargetClass() {
    final Class<ClassExamplesLoader> featureClass;
    final List<Example.Builder> examples;

    given:
    featureClass = ClassExamplesLoader.class;

    when:
    examples = this.loader.load(featureClass);

    then:
    assertThat(examples)
        .as("The class has one valid supplier for the target test class.")
        .hasSize(1);

    assertThat(examples.get(0).getFeatureClass())
        .as("The feature class is the target test class.")
        .isEqualTo(featureClass);

    assertThat(examples.get(0).getScenarioMethodName())
        .as("The scenario method name is the one supplied for the target test class.")
        .isEqualTo(ExampleSource.EXAMPLE_SUPPLIER_STATIC_MAIN_CLASS);
  }
}
