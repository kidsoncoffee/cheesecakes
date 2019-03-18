package com.kidsoncoffee.paramtests;

import com.google.auto.service.AutoService;
import com.kidsoncoffee.paramtests.generator.ParameterClassGenerator;
import com.kidsoncoffee.paramtests.generator.ParameterizedTestsGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class BDDParametersProcessor extends AbstractProcessor {

  private List<ParameterizedTestsGenerator> generators;

  private Messager messager;

  @Override
  public void init(final ProcessingEnvironment processingEnv) {
    this.messager = processingEnv.getMessager();
    // TODO fchovich MAKE THIS A RUNTIME DEPENDENCY
    this.generators = asList(new ParameterClassGenerator());
    this.generators.forEach(g -> g.init(processingEnv));
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    // TODO fchovich ADD LOGGER. IF NO GENERATORS
    return this.generators.stream()
        .map(ParameterizedTestsGenerator::getAnnotationsToProcess)
        .flatMap(Collection::stream)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    final AtomicBoolean generated = new AtomicBoolean(false);
    for (final ParameterizedTestsGenerator generator : this.generators) {
      final List<Element> elements =
          generator.getAnnotationsToProcess().stream()
              .map(roundEnv::getElementsAnnotatedWith)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());

      generated.compareAndSet(
          !generated.get(),
          generator.createDefinitions(elements).stream()
              .map(generator::generate)
              .filter(b -> b)
              .findAny()
              .orElse(false));
    }

    return generated.get();
  }
}
