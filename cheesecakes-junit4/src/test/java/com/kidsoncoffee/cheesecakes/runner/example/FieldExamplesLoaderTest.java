package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.example.field.ExampleFieldsProvider;
import com.kidsoncoffee.cheesecakes.runner.example.field.InitializationErrorExampleFieldsProvider;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link FieldExamplesLoader}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class FieldExamplesLoaderTest {

  /** The unit under test. */
  private FieldExamplesLoader loader;

  /** Setup the unit under test. */
  @Before
  public void setup() {
    this.loader = new FieldExamplesLoader();
  }

  /**
   * Checks than an empty list of examples is returned if there are no example fields in the target
   * feature class.
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
    Assertions.assertThat(examples).as("No example fields in the target class.").isEmpty();
  }

  /**
   * Checks that a list of examples is returned if there are example fields matching the target
   * feature class. Is expected to skip any invalid example fields.
   */
  @Test
  public void examples() {
    final Class featureClass;
    final List<Example.Builder> examples;

    given:
    featureClass = ExampleFieldsProvider.class;

    when:
    examples = this.loader.load(featureClass);

    then:
    Assertions.assertThat(examples).as("The target class has one valid example field.").hasSize(1);
  }

  /**
   * Checks that an empty list is returned if there are fields that throws initialization errors,
   * even if with valid example fields.
   */
  @Test
  public void initializationErrorExample() {
    final Class featureClass;
    final List<Example.Builder> examples;

    given:
    featureClass = InitializationErrorExampleFieldsProvider.class;

    when:
    examples = this.loader.load(featureClass);

    then:
    Assertions.assertThat(examples).as("The target class has one invalid example field.").isEmpty();
  }
}
