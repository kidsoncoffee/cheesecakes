package com.kidsoncoffee.cheesecakes.processor.aggregator;

import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.FeatureToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableFeatureToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableParameterToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableScenarioToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ParameterToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.example.ExamplesExtractor;
import com.kidsoncoffee.cheesecakes.processor.aggregator.group.ParameterGrouping;
import com.kidsoncoffee.cheesecakes.processor.aggregator.group.ProcessingGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class FeaturesAggregator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeaturesAggregator.class);

  private final Elements elementUtils;

  private ExamplesExtractor examplesExtractor;

  private ParameterGrouping parameterGrouping;

  @Inject
  public FeaturesAggregator(
      final Elements elementUtils,
      final ParameterGrouping parameterGrouping,
      final ExamplesExtractor examplesExtractor) {
    this.elementUtils = elementUtils;
    this.parameterGrouping = parameterGrouping;
    this.examplesExtractor = examplesExtractor;
  }

  public List<FeatureToGenerate> aggregate(final List<Element> elements) {
    final List<Element> annotatedParameters =
        elements.stream()
            .filter(e -> e.getKind().equals(ElementKind.PARAMETER))
            .collect(Collectors.toList());

    // TODO fchovich VALIDATE THAT ALL PARAMETERS ARE ANNOTATED

    if (annotatedParameters.isEmpty()) {
      LOGGER.warn("No parameters were found in any test.");
      return emptyList();
    }

    final ProcessingGroup groups = this.parameterGrouping.group(annotatedParameters);

    return groups.getFeatures().stream()
        .map(
            feature ->
                ImmutableFeatureToGenerate.builder()
                    .testClassName(feature.getFeature().getSimpleName().toString())
                    .testClassPackage(
                        this.elementUtils
                            .getPackageOf(feature.getFeature())
                            .getQualifiedName()
                            .toString())
                    .scenarios(
                        feature.getScenarios().stream()
                            .map(
                                scenario -> {
                                  final List<ParameterToGenerate> parameter =
                                      extractParameters(scenario.getParameters());
                                  return ImmutableScenarioToGenerate.builder()
                                      .testMethod(scenario.getScenario().getSimpleName().toString())
                                      .parameters(parameter)
                                      .examples(
                                          this.examplesExtractor.extract(
                                              parameter, scenario.getScenario()))
                                      .build();
                                })
                            .collect(Collectors.toList()))
                    .build())
        .collect(Collectors.toList());
  }

  // TODO fchovich should we separate annotations used for generation from annotations used only by
  // the runner.
  private static List<ParameterToGenerate> extractParameters(final List<Element> parameters) {
    return IntStream.range(0, parameters.size())
        .mapToObj(
            i -> {
              final Element parameter = parameters.get(i);
              return ImmutableParameterToGenerate.builder()
                  .name(parameter.getSimpleName().toString())
                  .type(parameter.asType())
                  .stepType(
                      parameter.getAnnotation(Parameter.Requisite.class) != null
                          ? com.kidsoncoffee.cheesecakes.Scenario.StepType.REQUISITE
                          : com.kidsoncoffee.cheesecakes.Scenario.StepType.EXPECTATION)
                  .overallOrder(i)
                  .build();
            })
        .collect(Collectors.toList());
  }
}
