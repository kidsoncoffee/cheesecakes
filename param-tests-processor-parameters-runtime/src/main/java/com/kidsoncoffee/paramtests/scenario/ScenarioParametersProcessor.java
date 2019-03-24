package com.kidsoncoffee.paramtests.scenario;

import com.google.auto.service.AutoService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kidsoncoffee.paramtests.Scenario;
import com.kidsoncoffee.paramtests.ScenarioBlock;
import com.kidsoncoffee.paramtests.Scenarios;
import com.kidsoncoffee.paramtests.annotations.Parameters;
import com.kidsoncoffee.paramtests.scenario.domain.ImmutableScenarioBlockDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ImmutableScenarioDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ScenarioBlockDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ScenarioDefinition;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class ScenarioParametersProcessor extends AbstractProcessor {

  private static final TypeName SCENARIO_TYPE = TypeName.get(Scenario.class);

  private static final TypeName SCENARIO_BLOCK_TYPE = TypeName.get(ScenarioBlock.class);

  private Filer filer;
  private Elements elementUtils;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.filer = processingEnv.getFiler();
    this.elementUtils = processingEnv.getElementUtils();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Parameters.Requisites.class, Parameters.Expectations.class)
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    final List<Element> elements =
        annotations.stream()
            .map(roundEnv::getElementsAnnotatedWith)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    if (elements.isEmpty()) {
      return false;
    }

    groupElements(this.elementUtils, elements)
        .forEach((key, value) -> generate(this.filer, key.getLeft(), key.getRight(), value));

    return true;
  }

  private static Map<Pair<String, String>, List<ScenarioDefinition>> groupElements(
      final Elements elementUtils, final List<Element> elements) {
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
                    .requisites(extract(Parameters.Requisites.class, entry.getValue()))
                    .expectations(extract(Parameters.Expectations.class, entry.getValue()))
                    .build())
        .collect(
            Collectors.groupingBy(
                def -> Pair.of(def.getTestClassPackage(), def.getTestClassName())));
  }

  private static List<ScenarioBlockDefinition> extract(
      final Class<? extends Annotation> annotation, final List<Element> elements) {
    return elements.stream()
        .filter(element -> element.getAnnotation(annotation) != null)
        .map(
            element ->
                ImmutableScenarioBlockDefinition.builder()
                    .parameterName(element.getSimpleName().toString())
                    .parameterType(element.asType())
                    .build())
        .collect(Collectors.toList());
  }

  private static void generate(
      final Filer filer,
      final String testClassPackage,
      final String testClassName,
      final List<ScenarioDefinition> scenarios) {
    final String scenariosClassName = String.format("%sScenarios", testClassName);
    final ClassName scenariosType = ClassName.get(testClassPackage, scenariosClassName);

    final List<TypeSpec> scenariosTypes =
        scenarios.stream()
            .map(definition -> createScenarioClass(scenariosType, definition))
            .collect(Collectors.toList());

    final TypeSpec scenariosClass =
        TypeSpec.classBuilder(scenariosType)
            .addSuperinterface(TypeName.get(Scenarios.class))
            .addTypes(scenariosTypes)
            .build();

    try {
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(testClassPackage, scenariosClass)
          .addStaticImport(Arrays.class, "asList")
          .build()
          .writeTo(filer);
    } catch (IOException e) {
      throw new UncheckedIOException(String.format("Error generating '%s'.", Scenarios.class), e);
    }
  }

  private static TypeSpec createScenarioClass(
      final ClassName scenariosType, final ScenarioDefinition definition) {
    // DEFINE NAMES

    final String scenarioName = WordUtils.capitalize(definition.getTestMethodName());
    final ClassName scenarioClassName = ClassName.get(scenariosType.simpleName(), scenarioName);
    final ClassName requisitesClassName =
        ClassName.get(scenariosType.simpleName(), scenarioClassName.simpleName(), "Requisites");
    final ClassName expectationsClassName =
        ClassName.get(scenariosType.simpleName(), scenarioClassName.simpleName(), "Expectations");

    // CREATE FIELDS

    final FieldSpec requisitesField =
        FieldSpec.builder(requisitesClassName, "requisites")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();

    final FieldSpec expectationsField =
        FieldSpec.builder(expectationsClassName, "expectations")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();

    // CREATE CONSTRUCTOR

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addStatement("this.$N = new $T(this)", requisitesField, requisitesClassName)
            .addStatement("this.$N = new $T(this)", expectationsField, expectationsClassName)
            .build();

    // CREATE METHODS

    final MethodSpec requisitesGetter =
        MethodSpec.methodBuilder("getRequisites")
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return this.$N", requisitesField)
            .returns(requisitesField.type)
            .build();

    final MethodSpec expectationsGetter =
        MethodSpec.methodBuilder("getExpectations")
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return this.$N", expectationsField)
            .returns(expectationsField.type)
            .build();

    final MethodSpec givenMethod =
        MethodSpec.methodBuilder("given")
            .addModifiers(Modifier.STATIC)
            .returns(requisitesClassName)
            .addStatement("return new $T().$N", scenarioClassName, requisitesField)
            .build();

    // CREATE INNER FIELDS

    final TypeSpec requisitesType =
        createRequisitesType(
            requisitesClassName,
            scenarioClassName,
            expectationsClassName,
            expectationsField,
            definition.getRequisites());

    final TypeSpec expectationsType =
        createExpectationsType(expectationsClassName, definition.getExpectations());

    // CREATE TYPE

    return TypeSpec.classBuilder(scenarioName)
        .addSuperinterface(Scenario.class)
        .addModifiers(Modifier.STATIC)
        .addFields(asList(requisitesField, expectationsField))
        .addMethods(asList(constructor, requisitesGetter, expectationsGetter, givenMethod))
        .addTypes(asList(requisitesType, expectationsType))
        .build();
  }

  private static TypeSpec createRequisitesType(
      final ClassName blockClassName,
      final ClassName scenarioClassName,
      final ClassName expectationsClassName,
      final FieldSpec expectationsField,
      final List<ScenarioBlockDefinition> parameters) {
    final TypeSpec.Builder requisites = createScenarioBlockType(blockClassName, parameters);

    requisites.addMethod(
        MethodSpec.methodBuilder("then")
            .addStatement("return $T.this.$N", scenarioClassName, expectationsField)
            .returns(expectationsClassName)
            .build());

    return requisites.build();
  }

  private static TypeSpec createExpectationsType(
      final ClassName type, final List<ScenarioBlockDefinition> parameters) {
    final TypeSpec.Builder requisites = createScenarioBlockType(type, parameters);
    return requisites.build();
  }

  private static TypeSpec.Builder createScenarioBlockType(
      final ClassName type, final List<ScenarioBlockDefinition> parameters) {
    final List<FieldSpec> fields = new ArrayList<>();
    final List<MethodSpec> methods = new ArrayList<>();

    parameters.stream()
        .map(r -> createBuilderPattern(type, r.getParameterType(), r.getParameterName()))
        .forEach(
            p -> {
              fields.add(p.getLeft());
              methods.addAll(p.getRight());
            });

    final ParameterSpec constructorParameter =
        ParameterSpec.builder(SCENARIO_TYPE, "testCase").addModifiers(Modifier.FINAL).build();

    methods.add(
        MethodSpec.constructorBuilder()
            .addParameter(constructorParameter)
            .addStatement("super($N)", constructorParameter)
            .build());

    return TypeSpec.classBuilder(type)
        .superclass(SCENARIO_BLOCK_TYPE)
        .addFields(fields)
        .addMethods(methods);
  }

  private static Pair<FieldSpec, List<MethodSpec>> createBuilderPattern(
      ClassName blockName, final TypeMirror type, final String name) {

    final TypeName typeName = TypeName.get(type);

    final FieldSpec field =
        FieldSpec.builder(typeName, name).addModifiers(Modifier.PRIVATE).build();

    final ParameterSpec parameterSpec =
        ParameterSpec.builder(typeName, name).addModifiers(Modifier.FINAL).build();

    final MethodSpec setter =
        MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(parameterSpec)
            .addStatement("this.$N = $N", field, parameterSpec)
            .addStatement("return this")
            .returns(blockName)
            .build();

    final MethodSpec getter =
        MethodSpec.methodBuilder(String.format("get%s", WordUtils.capitalize(name)))
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return this.$N", field)
            .returns(typeName)
            .build();

    return Pair.of(field, asList(setter, getter));
  }
}
