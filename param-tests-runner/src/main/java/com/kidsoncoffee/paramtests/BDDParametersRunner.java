package com.kidsoncoffee.paramtests;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class BDDParametersRunner extends BlockJUnit4ClassRunner {

  /**
   * Constructs a new {@code ParentRunner} that will run {@code @TestClass}
   *
   * @param testClass The test class to run.
   */
  protected BDDParametersRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }
}
