package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;

import java.util.List;

/**
 * Loads examples from feature {@link Class}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public interface ExamplesLoader {

  /**
   * Loads example from the feature class.
   *
   * @param featureClass The feature class to load the example from.
   * @return A list of examples.
   */
  List<Example.Builder> load(final Class featureClass);
}
