package com.kidsoncoffee.cheesecakes.processor;

import com.google.auto.service.AutoService;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.processor.aggregator.FeaturesAggregator;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.FeatureToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ScenarioToGenerate;
import com.kidsoncoffee.cheesecakes.processor.generator.DataTableExampleGenerator;
import com.kidsoncoffee.cheesecakes.processor.generator.ExampleBuilderGenerator;
import com.kidsoncoffee.cheesecakes.processor.generator.ScenarioParametersSchemasGenerator;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class CheesecakesProcessor extends AbstractProcessor {

  @Inject private Types typeUtils;
  @Inject private Elements elementUtils;

  @Inject private FeaturesAggregator featuresAggregator;

  @Inject private ScenarioParametersSchemasGenerator scenarioParametersSchemasGenerator;
  @Inject private ExampleBuilderGenerator exampleBuilderGenerator;
  @Inject private DataTableExampleGenerator dataTableExampleGenerator;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    Guice.createInjector(new CheesecakesProcessorModule(processingEnv)).injectMembers(this);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Parameter.Requisite.class, Parameter.Expectation.class)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final List<Element> elements =
        annotations.stream()
            .map(roundEnv::getElementsAnnotatedWith)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    if (elements.isEmpty()) {
      return false;
    }

    final List<FeatureToGenerate> features = this.featuresAggregator.aggregate(elements);

    for (final FeatureToGenerate feature : features) {
      final Map<ScenarioToGenerate, ClassName> generatedSchemas =
          this.scenarioParametersSchemasGenerator.generate(feature);
      this.exampleBuilderGenerator.generate(feature, generatedSchemas);
      this.dataTableExampleGenerator.generate(feature, generatedSchemas);
    }

    return true;
  }
}
