package com.kidsoncoffee.paramtests;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.kidsoncoffee.paramtests.generator.ParameterizedTestsGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class BDDParametersProcessor extends AbstractProcessor {

  private List<ParameterizedTestsGenerator> generators;

  @Override
  public void init(final ProcessingEnvironment processingEnv) {
    this.generators = Lists.newArrayList(ServiceLoader.load(ParameterizedTestsGenerator.class));
    this.generators.forEach(g -> g.init(processingEnv));
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return this.generators.stream()
        .map(ParameterizedTestsGenerator::getAnnotationsToProcess)
        .flatMap(Collection::stream)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

    for (final ParameterizedTestsGenerator generator : this.generators) {
      final List<Element> elements =
          generator.getAnnotationsToProcess().stream()
              .map(roundEnv::getElementsAnnotatedWith)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());

      generator.createDefinitions(elements).forEach(generator::generate);
    }

    return false;
  }
}
