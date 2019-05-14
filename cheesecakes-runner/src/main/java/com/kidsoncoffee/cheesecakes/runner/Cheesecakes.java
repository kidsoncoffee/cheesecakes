package com.kidsoncoffee.cheesecakes.runner;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunner;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.validator.TestClassValidator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class Cheesecakes extends Suite {

  @Inject private ScenarioRunnerCreator scenarioRunnerCreator;

  @Inject private Set<TestClassValidator> validators;

  private final List<Runner> runners;

  public Cheesecakes(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());

    Guice.createInjector(new CheesecakesRunnerModule()).injectMembers(this);

    this.validateTestClass();

    this.runners = this.createRunner().stream().map(Runner.class::cast).collect(toList());
  }

  private final void validateTestClass() throws InitializationError {
    final List<Throwable> errors =
        this.validators.stream()
            .map(validator -> validator.validateTestClass(this.getTestClass()))
            .flatMap(Collection::stream)
            .map(Throwable.class::cast)
            .collect(toList());
    if (!errors.isEmpty()) {
      throw new InitializationError(errors);
    }
  }

  private List<ScenarioRunner> createRunner() throws InitializationError {
    return this.scenarioRunnerCreator.create(this.getTestClass());
  }

  @Override
  protected List<Runner> getChildren() {
    return this.runners;
  }
}
