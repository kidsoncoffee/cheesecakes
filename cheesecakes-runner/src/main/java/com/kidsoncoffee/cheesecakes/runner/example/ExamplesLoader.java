package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface ExamplesLoader {
  List<Example.Builder> load(final Class featureClass);
}
