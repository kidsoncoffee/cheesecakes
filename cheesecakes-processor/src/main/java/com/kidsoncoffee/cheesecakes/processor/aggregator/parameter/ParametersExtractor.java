package com.kidsoncoffee.cheesecakes.processor.aggregator.parameter;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.Scenario;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableParameterToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ParameterToGenerate;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ParametersExtractor {
  // TODO fchovich should we separate annotations used for generation from annotations used only by
  // the runner?
  public List<ParameterToGenerate> extract(final List<Element> parameters) {
    return IntStream.range(0, parameters.size())
        .mapToObj(i -> createParameterToGenerate(parameters, i))
        .collect(Collectors.toList());
  }

  private static ParameterToGenerate createParameterToGenerate(List<Element> parameters, int i) {
    final Element parameter = parameters.get(i);
    return ImmutableParameterToGenerate.builder()
        .name(parameter.getSimpleName().toString())
        .type(parameter.asType())
        .stepType(retrieveStepType(parameter))
        .overallOrder(i)
        .build();
  }

  private static Scenario.StepType retrieveStepType(Element parameter) {
    return parameter.getAnnotation(Parameter.Requisite.class) != null
        ? Scenario.StepType.REQUISITE
        : Scenario.StepType.EXPECTATION;
  }
}
