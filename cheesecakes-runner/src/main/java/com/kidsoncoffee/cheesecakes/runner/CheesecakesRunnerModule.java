package com.kidsoncoffee.cheesecakes.runner;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.kidsoncoffee.cheesecakes.runner.example.ClassExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.ExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.FieldExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.validator.AllParametersAreAnnotatedValidator;
import org.junit.validator.TestClassValidator;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class CheesecakesRunnerModule extends AbstractModule {

  @Override
  protected void configure() {
    super.configure();

    final Multibinder<ExamplesLoader> examplesLoaderMultibinder =
        Multibinder.newSetBinder(binder(), ExamplesLoader.class);
    examplesLoaderMultibinder.addBinding().to(FieldExamplesLoader.class);
    examplesLoaderMultibinder.addBinding().to(ClassExamplesLoader.class);

    final Multibinder<TestClassValidator> testClassValidatorMultibinder =
        Multibinder.newSetBinder(binder(), TestClassValidator.class);
    testClassValidatorMultibinder.addBinding().to(AllParametersAreAnnotatedValidator.class);
  }
}
