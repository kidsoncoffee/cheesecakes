package com.kidsoncoffee.cheesecakes.processor.aggregator.group;

import org.immutables.value.Value;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
@Value.Style(builder = "scenarioElements")
public interface ScenarioElements {
  Element getScenario();

  List<Element> getParameters();
}
