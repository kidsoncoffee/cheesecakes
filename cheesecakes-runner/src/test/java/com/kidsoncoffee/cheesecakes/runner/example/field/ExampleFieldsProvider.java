package com.kidsoncoffee.cheesecakes.runner.example.field;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoaderTest;

import static java.util.Collections.emptyList;

/**
 * Provides example fields for {@link FieldExamplesLoaderTest}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleFieldsProvider {

  /** Invalid example field (Missing static modifier). */
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private final Example.Builder fieldNotStatic =
      new Example.Builder(Integer.class.getName(), "EXAMPLE_NOT_STATIC", emptyList());

  /** This is not an example field, so should be ignored by the loader. */
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private static final String fieldNotString = "IGNORE ME PLEASE";

  /** Valid example field for {@link FieldExamplesLoaderTest} (target feature class). */
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private static final Example.Builder fieldForTargetClass =
      new Example.Builder(ExampleFieldsProvider.class.getName(), "EXAMPLE_TARGET_CLASS", emptyList());

  /** Valid example field for {@link FieldExamplesLoader} (target feature class). */
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private static final Example.Builder fieldForDifferentClass =
      new Example.Builder(FieldExamplesLoader.class.getName(), "EXAMPLE_DIFFERENT_CLASS", emptyList());
}
