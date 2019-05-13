package com.kidsoncoffee.cheesecakes.processor.aggregator;

import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.FeatureToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableFeatureToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableScenarioToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ParameterToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ScenarioToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.example.ExamplesExtractor;
import com.kidsoncoffee.cheesecakes.processor.aggregator.group.FeatureElements;
import com.kidsoncoffee.cheesecakes.processor.aggregator.group.ParameterGrouping;
import com.kidsoncoffee.cheesecakes.processor.aggregator.group.ProcessingGroup;
import com.kidsoncoffee.cheesecakes.processor.aggregator.group.ScenarioElements;
import com.kidsoncoffee.cheesecakes.processor.aggregator.parameter.ParametersExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class FeaturesAggregator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeaturesAggregator.class);

  private final Elements elementUtils;

  private final ExamplesExtractor examplesExtractor;

  private final ParametersExtractor parametersExtractor;

  private final ParameterGrouping parameterGrouping;

  @Inject
  public FeaturesAggregator(
      final Elements elementUtils,
      final ParameterGrouping parameterGrouping,
      final ExamplesExtractor examplesExtractor,
      ParametersExtractor parametersExtractor) {
    this.elementUtils = elementUtils;
    this.parameterGrouping = parameterGrouping;
    this.examplesExtractor = examplesExtractor;
    this.parametersExtractor = parametersExtractor;
  }

  public List<FeatureToGenerate> aggregate(final List<Element> elements) {
    final List<Element> annotatedParameters =
        elements.stream()
            .filter(e -> e.getKind().equals(ElementKind.PARAMETER))
            .collect(Collectors.toList());

    if (annotatedParameters.isEmpty()) {
      LOGGER.warn("No parameters were found in any test.");
      return emptyList();
    }

    final ProcessingGroup groups = this.parameterGrouping.group(annotatedParameters);
    return groups.getFeatures().stream()
        .map(this::createFeatureToGenerate)
        .collect(Collectors.toList());
  }

  private FeatureToGenerate createFeatureToGenerate(FeatureElements featureElements) {
    final Element feature = featureElements.getFeature();
    return ImmutableFeatureToGenerate.builder()
        .testClassName(feature.getSimpleName().toString())
        .testClassPackage(this.elementUtils.getPackageOf(feature).getQualifiedName().toString())
        .scenarios(createScenariosToGenerate(featureElements))
        .build();
  }

  private List<ScenarioToGenerate> createScenariosToGenerate(FeatureElements feature) {
    return feature.getScenarios().stream()
        .map(this::createScenarioToGenerate)
        .collect(Collectors.toList());
  }

  private ScenarioToGenerate createScenarioToGenerate(ScenarioElements scenarioElements) {
    final List<ParameterToGenerate> parameter =
        this.parametersExtractor.extract(scenarioElements.getParameters());
    final Element scenario = scenarioElements.getScenario();
    return ImmutableScenarioToGenerate.builder()
        .testMethod(scenario.getSimpleName().toString())
        .parameters(parameter)
        .examples(this.examplesExtractor.extract(parameter, scenario))
        .build();
  }
}
