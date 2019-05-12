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
@SuppressWarnings("PMD.UnusedPrivateMethod")
public class ExampleFieldsProvider {

  /** Invalid example field (Missing static modifier). */
  private final Example.Builder fieldNotStatic =
      new Example.Builder(Integer.class, "EXAMPLE_NOT_STATIC", emptyList());

  /** This is not an example field, so should be ignored by the loader. */
  private static final String fieldNotString = "IGNORE ME PLEASE";

  /** Valid example field for {@link FieldExamplesLoaderTest} (target feature class). */
  private static final Example.Builder fieldForTargetClass =
      new Example.Builder(ExampleFieldsProvider.class, "EXAMPLE_TARGET_CLASS", emptyList());

  /** Valid example field for {@link FieldExamplesLoader} (target feature class). */
  private static final Example.Builder fieldForDifferentClass =
      new Example.Builder(FieldExamplesLoader.class, "EXAMPLE_DIFFERENT_CLASS", emptyList());
}
