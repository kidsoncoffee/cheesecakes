package com.kidsoncoffee.cheesecakes.processor;

import com.google.auto.service.AutoService;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.processor.domain.Feature;
import com.kidsoncoffee.cheesecakes.processor.domain.Scenario;
import com.kidsoncoffee.cheesecakes.processor.generator.DataDrivenScenariosGenerator;
import com.kidsoncoffee.cheesecakes.processor.generator.ParameterBuilderGenerator;
import com.kidsoncoffee.cheesecakes.processor.generator.SchemaGenerator;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
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

  private FeaturesAggregator featuresAggregator;

  private SchemaGenerator schemaGenerator;
  private ParameterBuilderGenerator parameterBuilderGenerator;
  private DataDrivenScenariosGenerator dataDrivenScenariosGenerator;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.featuresAggregator = new FeaturesAggregator(processingEnv.getElementUtils());
    this.schemaGenerator = new SchemaGenerator(processingEnv.getFiler());
    this.parameterBuilderGenerator = new ParameterBuilderGenerator(processingEnv.getFiler());
    this.dataDrivenScenariosGenerator = new DataDrivenScenariosGenerator(processingEnv.getFiler());
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Parameters.Requisites.class, Parameters.Expectations.class)
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

    final List<Feature> features = this.featuresAggregator.aggregate(elements);

    for (final Feature feature : features) {
      final Map<Scenario, ClassName> generatedSchemas = this.schemaGenerator.generate(feature);
      this.parameterBuilderGenerator.generate(feature, generatedSchemas);
      this.dataDrivenScenariosGenerator.generate(feature, generatedSchemas);
    }

    return true;
  }
}
