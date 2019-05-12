package com.kidsoncoffee.cheesecakes.runner;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.runner.domain.ScenarioRunner;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class Cheesecakes extends Suite {

  @Inject private ScenarioRunnerCreator scenarioRunnerCreator;

  private final List<Runner> runners;

  public Cheesecakes(final Class<?> klass) throws InitializationError {
    super(klass, Collections.emptyList());

    Guice.createInjector(new CheesecakesRunnerModule()).injectMembers(this);

    this.runners = this.createRunner().stream().map(Runner.class::cast).collect(toList());
  }

  private List<ScenarioRunner> createRunner() throws InitializationError {
    return this.scenarioRunnerCreator.create(this.getTestClass());
  }

  @Override
  protected List<Runner> getChildren() {
    return this.runners;
  }
}
