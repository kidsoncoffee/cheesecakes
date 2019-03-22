package com.kidsoncoffee.paramtests;

import com.google.auto.service.AutoService;
import com.kidsoncoffee.paramtests.generator.ParameterClassGenerator;
import com.kidsoncoffee.paramtests.generator.ParameterizedTestClassDefinition;
import com.kidsoncoffee.paramtests.generator.ParameterizedTestsClassGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class BDDParametersProcessor extends AbstractProcessor {

  private final List<ParameterizedTestsClassGenerator> generators = new ArrayList<>();

  // TODO fchovich USE THIS!
  private Messager messager;

  @Override
  public void init(final ProcessingEnvironment processingEnv) {
    this.messager = processingEnv.getMessager();
    // TODO fchovich MAKE THIS A RUNTIME DEPENDENCY
    this.generators.add(new ParameterClassGenerator());
    this.generators.forEach(g -> g.init(processingEnv));
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    // TODO fchovich ADD LOGGER. IF NO GENERATORS
    return this.generators.stream()
        .map(ParameterizedTestsClassGenerator::getAnnotationsToProcess)
        .flatMap(Collection::stream)
        .map(c -> ((Class) c)) // TODO fchovich DON'T UNDERSTAND THIS
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    final AtomicBoolean generated = new AtomicBoolean(false);
    for (final ParameterizedTestsClassGenerator<? extends ParameterizedTestClassDefinition>
        generator : this.generators) {
      final List<Element> elements =
          generator.getAnnotationsToProcess().stream()
              .map(roundEnv::getElementsAnnotatedWith)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());

      if (elements.isEmpty()) {
        continue;
      }

      generated.compareAndSet(false, callGenerator(generator, elements).get());
    }

    return generated.get();
  }

  private static <
          V extends ParameterizedTestClassDefinition, T extends ParameterizedTestsClassGenerator<V>>
      AtomicBoolean callGenerator(final T generator, final List<Element> elements) {
    final AtomicBoolean generated = new AtomicBoolean(false);
    for (final V definition : generator.createDefinitions(elements)) {
      final boolean defGen = generator.generate(definition);
      generated.compareAndSet(false, defGen);
    }
    return generated;
  }
}
