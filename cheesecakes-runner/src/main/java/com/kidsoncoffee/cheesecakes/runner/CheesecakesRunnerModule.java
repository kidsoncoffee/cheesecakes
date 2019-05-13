package com.kidsoncoffee.cheesecakes.runner;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.kidsoncoffee.cheesecakes.runner.validator.AllParametersAreAnnotatedValidator;
import org.junit.validator.TestClassValidator;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class CheesecakesRunnerModule extends AbstractModule {

  @Override
  protected void configure() {
    super.configure();

    final Multibinder<TestClassValidator> testClassValidators =
        newSetBinder(binder(), TestClassValidator.class);
    testClassValidators.addBinding().to(AllParametersAreAnnotatedValidator.class);
  }
}
