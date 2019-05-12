package com.kidsoncoffee.cheesecakes.processor.aggregator.group;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterGrouping {
  public ProcessingGroup group(final List<Element> annotatedParameters) {

    final ProcessingGroup group = new ProcessingGroup();
    for (final Element parameter : annotatedParameters) {
      final Element scenario = parameter.getEnclosingElement();
      final Element feature = scenario.getEnclosingElement();

      group.addParameter(feature, scenario, parameter);
    }
    return group;
  }
}
