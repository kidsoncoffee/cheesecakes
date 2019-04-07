package com.kidsoncoffee.cheesecakes.processor;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.SpecificationStepType;
import com.kidsoncoffee.cheesecakes.processor.domain.Example;
import com.kidsoncoffee.cheesecakes.processor.domain.Feature;
import com.kidsoncoffee.cheesecakes.processor.domain.ImmutableFeature;
import com.kidsoncoffee.cheesecakes.processor.domain.ImmutableParameter;
import com.kidsoncoffee.cheesecakes.processor.domain.ImmutableScenario;
import com.kidsoncoffee.cheesecakes.processor.domain.Scenario;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class FeaturesAggregator {

  private final Elements elementUtils;

  private final CommentParser commentParser;

  public FeaturesAggregator(Elements elementUtils) {
    this.elementUtils = elementUtils;
    this.commentParser = new CommentParser(this.elementUtils);
  }

  public List<Feature> aggregate(final List<Element> elements) {
    final List<Element> annotatedParameters =
        elements.stream()
            .filter(
                e ->
                    isAnnotationPresent(e, Parameters.Requisites.class)
                        || isAnnotationPresent(e, Parameters.Expectations.class))
            .filter(e -> e.getKind().equals(ElementKind.PARAMETER))
            .collect(Collectors.toList());

    // TODO fchovich VALIDATE THAT ALL PARAMETERS ARE ANNOTATED

    if (annotatedParameters.isEmpty()) {
      return emptyList();
    }

    final Table<Element, Element, List<Element>> indexedElements =
        groupParameters(annotatedParameters);

    final List<Feature> features = new ArrayList<>();
    for (Map.Entry<Element, Map<Element, List<Element>>> featureEntry :
        indexedElements.rowMap().entrySet()) {
      final Element feature = featureEntry.getKey();

      final List<Scenario> scenarios = new ArrayList<>();
      for (Map.Entry<Element, List<Element>> scenarioEntry : featureEntry.getValue().entrySet()) {
        final Element scenario = scenarioEntry.getKey();
        final List<Element> parameters = scenarioEntry.getValue();

        scenarios.add(
            ImmutableScenario.builder()
                .testMethod(scenario.getSimpleName().toString())
                .parameters(extractParameters(parameters))
                .examples(extractExamples(this.commentParser, scenario))
                .build());
      }

      features.add(
          ImmutableFeature.builder()
              .testClassName(feature.getSimpleName().toString())
              .testClassPackage(this.elementUtils.getPackageOf(feature).getQualifiedName().toString())
              .scenarios(scenarios)
              .build());
    }
    return features;
  }

  private static List<ImmutableParameter> extractParameters(final List<Element> parameters) {
    return parameters.stream()
        .map(
            p ->
                ImmutableParameter.builder()
                    .name(p.getSimpleName().toString())
                    .type(p.asType())
                    .stepType(
                        p.getAnnotation(Parameters.Requisites.class) != null
                            ? SpecificationStepType.REQUISITE
                            : SpecificationStepType.EXPECTATION)
                    .build())
        .collect(Collectors.toList());
  }

  private static List<Example> extractExamples(
      final CommentParser commentParser, final Element scenario) {
    if (isAnnotationPresent(scenario, Parameters.DataDriven.class)) {
      return commentParser.parse(scenario);
    }
    return emptyList();
  }

  private static boolean isAnnotationPresent(
      final Element e, final Class<? extends Annotation> annotation) {
    return e.getAnnotation(annotation) != null;
  }

  private static Table<Element, Element, List<Element>> groupParameters(
      List<Element> annotatedParameters) {
    final Table<Element, Element, List<Element>> groupedElements = HashBasedTable.create();

    for (final Element parameter : annotatedParameters) {
      final Element methodElement = parameter.getEnclosingElement();
      final Element classElement = methodElement.getEnclosingElement();

      if (!groupedElements.contains(classElement, methodElement)) {
        groupedElements.put(classElement, methodElement, new ArrayList<>());
      }

      groupedElements.get(classElement, methodElement).add(parameter);
    }
    return groupedElements;
  }
}
