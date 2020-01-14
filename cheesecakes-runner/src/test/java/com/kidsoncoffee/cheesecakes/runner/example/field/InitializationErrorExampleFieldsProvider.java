package com.kidsoncoffee.cheesecakes.runner.example.field;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.ExplodingExampleBuilder;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoaderTest;

import static java.util.Collections.emptyList;

/**
 * Provides example fields for {@link FieldExamplesLoaderTest}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class InitializationErrorExampleFieldsProvider {

  /** Invalid example field. This will throw an exception at the top. */
  private static final Example.Builder fieldException =
      new ExplodingExampleBuilder(
          InitializationErrorExampleFieldsProvider.class, "EXAMPLE_EXCEPTION", emptyList());

  /** Valid example field for {@link InitializationErrorExampleFieldsProvider} (target feature class). */
  private static final Example.Builder fieldForTargetClass =
      new Example.Builder(
          InitializationErrorExampleFieldsProvider.class.getSimpleName(), "EXAMPLE_TARGET_CLASS", emptyList());
}
