package com.kidsoncoffee.paramtests;

import java.util.Collections;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public abstract class ScenarioBlock implements ScenarioDefiner {

  private final Scenario scenario;

  public ScenarioBlock(Scenario scenario) {
    this.scenario = scenario;
  }

  public List<Scenario> getScenarios() {
    return Collections.singletonList(this.scenario);
  }
}
