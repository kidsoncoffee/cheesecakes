package com.kidsoncoffee.paramtests.generator;

import com.kidsoncoffee.paramtests.annotations.BDDParameters;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class BindingMethodClassGenerator implements ParameterizedTestsClassGenerator<BindingClass> {

  private Elements elementUtils;
  private Filer filer;

  @Override
  public void init(final ProcessingEnvironment processingEnvironment) {
    this.elementUtils = processingEnvironment.getElementUtils();
    this.filer = processingEnvironment.getFiler();
  }

  @Override
  public List<Class<? extends Annotation>> getAnnotationsToProcess() {
    return asList(BDDParameters.Requisites.class, BDDParameters.Expectations.class);
  }

  @Override
  public List<BindingClass> createDefinitions(final List<Element> elements) {
    final List<Element> parameters =
        elements.stream()
            .filter(e -> e.getKind().equals(ElementKind.PARAMETER))
            .collect(Collectors.toList());

    // TODO fchovich VALIDATE THAT ALL PARAMETERS ARE ANNOTATED

    if (parameters.isEmpty()) {
      return Collections.emptyList();
    }

    final Map<Element, List<Element>> groupedElements =
        parameters.stream()
            .map(Element::getEnclosingElement)
            .distinct()
            .collect(Collectors.groupingBy(Element::getEnclosingElement));

    return groupedElements.entrySet().stream()
        .map(
            entry ->
                ImmutableBindingClass.builder()
                    .testClassName(entry.getKey().getSimpleName().toString())
                    .testClassPackage(this.elementUtils.getPackageOf(entry.getKey()).toString())
                    .testMethodName(entry.getKey().getSimpleName().toString())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  public boolean generate(final BindingClass def) {

    final ClassName annotationClassName =
        ClassName.get(
            def.getTestClassPackage(),
            String.format("%sBindings", WordUtils.capitalize(def.getTestMethodName())));

    final TypeSpec annotationClass = TypeSpec.annotationBuilder(annotationClassName).build();
    try {
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(def.getTestClassPackage(), annotationClass).build().writeTo(this.filer);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
