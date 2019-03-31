package com.kidsoncoffee.cheesecakes.processor.datadriven;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.FeatureDefinition;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.ImmutableFeatureDefinition;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.ImmutableScenarioDefinition;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.ImmutableScenarioExampleDefinition;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.ScenarioDefinition;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class DataDrivenSpecificationsProcessor extends AbstractProcessor {

  // TODO fchovich ADD LOGGER FOR EXECUTION

  private Filer filer;
  private Elements elementUtils;

  private DataDrivenScenariosClassGenerator generator;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.filer = processingEnv.getFiler();
    this.elementUtils = processingEnv.getElementUtils();
    this.generator = new DataDrivenScenariosClassGenerator(processingEnv.getFiler());
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Sets.newHashSet(Parameters.DataDriven.class.getCanonicalName());
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
    final List<? extends Element> elements =
        annotations.stream()
            .map(env::getElementsAnnotatedWith)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    if (elements.isEmpty()) {
      return false;
    }

    final List<FeatureDefinition> features = groupElements(this.elementUtils, elements);
    features.forEach(feature -> this.generator.generate(feature));

    return false;
  }

  private static List<FeatureDefinition> groupElements(
      final Elements elementUtils, final List<? extends Element> elements) {

    final Map<Element, ? extends List<? extends Element>> indexedByTestClass =
        elements.stream().collect(Collectors.groupingBy(Element::getEnclosingElement));

    return indexedByTestClass.entrySet().stream()
        .map(
            entry -> {
              final List<ScenarioDefinition> scenarios =
                  entry.getValue().stream()
                      .map(scenario -> createScenarioDefinition(elementUtils, scenario))
                      .collect(Collectors.toList());

              return ImmutableFeatureDefinition.builder()
                  .testClassPackage(elementUtils.getPackageOf(entry.getKey()).toString())
                  .testClassName(entry.getKey().getSimpleName().toString())
                  .addAllScenarios(scenarios)
                  .build();
            })
        .collect(Collectors.toList());
  }

  private static ImmutableScenarioDefinition createScenarioDefinition(
      final Elements elementUtils, final Element scenario) {
    final String doc = elementUtils.getDocComment(scenario);

    final AtomicBoolean whereFound = new AtomicBoolean(false);
    final List<String> scenarioLines =
        Arrays.stream(doc.split(System.lineSeparator()))
            .filter(
                docLine -> {
                  if (docLine.trim().equalsIgnoreCase("WHERE:")) {
                    return whereFound.getAndSet(true);
                  }
                  return whereFound.get();
                })
            .filter(StringUtils::isNotBlank)
            .filter(
                docLine ->
                    !docLine.trim().startsWith("---")) // TODO fchovich VALIDATE --- MANDATORY
            // AT
            // LEAST
            .collect(Collectors.toList());

    final List<String> header =
        Arrays.stream(scenarioLines.get(0).split("\\|"))
            .map(String::trim)
            .collect(Collectors.toList());

    final List<ImmutableScenarioExampleDefinition> examples =
        scenarioLines.subList(1, scenarioLines.size()).stream()
            .map(s -> parseScenarioLine(s, header))
            .map(
                parameters ->
                    ImmutableScenarioExampleDefinition.builder().parameters(parameters).build())
            .collect(Collectors.toList());

    return ImmutableScenarioDefinition.builder()
        .testMethodName(((Element) scenario).getSimpleName().toString())
        .addAllExamples(examples)
        .build();
  }

  private static Map<String, String> parseScenarioLine(
      final String scenario, final List<String> header) {
    final String[] fields = scenario.split("\\|");
    return IntStream.range(0, fields.length)
        .boxed()
        .collect(Collectors.toMap(header::get, i -> fields[i].trim()));
  }
}
