package com.kidsoncoffee.paramtests.scenario;

import com.google.auto.service.AutoService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
import com.kidsoncoffee.cheesecakes.SpecificationBlock;
import com.kidsoncoffee.paramtests.scenario.domain.ImmutableScenarioBlockDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ImmutableScenarioDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ScenarioBlockDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ScenarioDefinition;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class ScenarioParametersProcessor extends AbstractProcessor {

  private static final TypeName SCENARIO_TYPE = TypeName.get(Specification.class);

  private static final TypeName SCENARIO_BLOCK_TYPE = TypeName.get(SpecificationBlock.class);

  private Elements elementUtils;
  private SpecificationClassGenerator generator;
  private Types typeUtils;

  @Override
  public synchronized void init(final ProcessingEnvironment env) {
    super.init(env);

    this.elementUtils = env.getElementUtils();
    this.typeUtils = env.getTypeUtils();

    this.generator = new SpecificationClassGenerator(env.getFiler());
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Parameters.Requisites.class, Parameters.Expectations.class)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
    final List<Element> elements =
        annotations.stream()
            .map(env::getElementsAnnotatedWith)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    if (elements.isEmpty()) {
      return false;
    }

    groupElements(this.typeUtils, this.elementUtils, elements)
        .forEach((key, value) -> this.generator.generate(key.getLeft(), key.getRight(), value));

    return true;
  }

  // TODO fchovich STUPID DATA STRUCTURES
  private static Map<Pair<String, String>, List<ScenarioDefinition>> groupElements(
      final Types typeUtils, final Elements elementUtils, final List<Element> elements) {
    final List<Element> parameters =
        elements.stream()
            .filter(e -> e.getKind().equals(ElementKind.PARAMETER))
            .collect(Collectors.toList());

    // TODO fchovich VALIDATE THAT ALL PARAMETERS ARE ANNOTATED

    if (parameters.isEmpty()) {
      return Collections.emptyMap();
    }

    final Table<Element, Element, List<Element>> groupedElements = HashBasedTable.create();

    for (final Element parameter : parameters) {
      final Element methodElement = parameter.getEnclosingElement();
      final Element classElement = methodElement.getEnclosingElement();

      if (!groupedElements.contains(classElement, methodElement)) {
        groupedElements.put(classElement, methodElement, new ArrayList<>());
      }

      groupedElements.get(classElement, methodElement).add(parameter);
    }

    return groupedElements.cellSet().stream()
        .map(
            entry ->
                ImmutableScenarioDefinition.builder()
                    .testClassName(entry.getRowKey().getSimpleName().toString())
                    .testClassPackage(elementUtils.getPackageOf(entry.getRowKey()).toString())
                    .testMethodName(entry.getColumnKey().getSimpleName().toString())
                    .requisites(extract(typeUtils, Parameters.Requisites.class, entry.getValue()))
                    .expectations(
                        extract(typeUtils, Parameters.Expectations.class, entry.getValue()))
                    .build())
        .collect(
            Collectors.groupingBy(
                def -> Pair.of(def.getTestClassPackage(), def.getTestClassName())));
  }

  private static List<ScenarioBlockDefinition> extract(
      final Types typeUtils,
      final Class<? extends Annotation> annotation,
      final List<Element> elements) {
    return IntStream.range(0, elements.size())
        .filter(i -> elements.get(i).getAnnotation(annotation) != null)
        .mapToObj(
            i ->
                ImmutableScenarioBlockDefinition.builder()
                    .parameterName(elements.get(i).getSimpleName().toString())
                    .parameterType(elements.get(i).asType())
                    .annotationType(annotation)
                    .overallOrder(i)
                    .build())
        .collect(Collectors.toList());
  }
}
