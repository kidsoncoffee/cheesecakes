package com.kidsoncoffee.paramtests;

import java.util.Collections;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface Scenario extends ScenarioDefiner {
  default List<Scenario> getScenarios() {
    return Collections.singletonList(this);
  }
}
