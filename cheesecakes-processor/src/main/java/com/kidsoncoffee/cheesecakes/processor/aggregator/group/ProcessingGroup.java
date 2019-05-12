package com.kidsoncoffee.cheesecakes.processor.aggregator.group;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kidsoncoffee.cheesecakes.processor.aggregator.group.ImmutableFeatureElements.featureElements;
import static com.kidsoncoffee.cheesecakes.processor.aggregator.group.ImmutableScenarioElements.scenarioElements;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ProcessingGroup {
  private final Table<Element, Element, List<Element>> group = HashBasedTable.create();

  public void addParameter(final Element feature, final Element scenario, final Element parameter) {
    if (!this.group.contains(feature, scenario)) {
      this.group.put(feature, scenario, new ArrayList<>());
    }

    this.group.get(feature, scenario).add(parameter);
  }

  public List<FeatureElements> getFeatures() {
    return this.group.rowMap().entrySet().stream()
        .map(ProcessingGroup::convertToFeatureElements)
        .collect(Collectors.toList());
  }

  private static FeatureElements convertToFeatureElements(
      final Map.Entry<Element, Map<Element, List<Element>>> featureGroup) {
    return featureElements()
        .feature(featureGroup.getKey())
        .scenarios(
            featureGroup.getValue().entrySet().stream()
                .map(ProcessingGroup::convertToScenarioElements)
                .collect(Collectors.toList()))
        .build();
  }

  private static ScenarioElements convertToScenarioElements(
      final Map.Entry<Element, List<Element>> scenarioGroup) {
    return scenarioElements()
        .scenario(scenarioGroup.getKey())
        .parameters(scenarioGroup.getValue())
        .build();
  }
}
