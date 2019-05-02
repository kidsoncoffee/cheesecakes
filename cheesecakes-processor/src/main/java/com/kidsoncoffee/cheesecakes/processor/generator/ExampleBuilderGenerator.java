package com.kidsoncoffee.cheesecakes.processor.generator;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.processor.domain.Feature;
import com.kidsoncoffee.cheesecakes.processor.domain.Parameter;
import com.kidsoncoffee.cheesecakes.processor.domain.Scenario;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleBuilderGenerator {

  private static final TypeName SPECIFICATION_TYPE = TypeName.get(Example.Builder.class);

  private static final TypeName SPECIFICATION_BLOCK_TYPE =
      TypeName.get(com.kidsoncoffee.cheesecakes.Scenario.StepBlock.class);

  private final Filer filer;

  public ExampleBuilderGenerator(final Filer filer) {
    this.filer = filer;
  }

  private static TypeSpec createScenarioParameterBuilder(
      final ClassName scenariosType,
      final Feature feature,
      final Scenario definition,
      final ClassName generatedSchema) {
    // DEFINE NAMES

    final String scenarioName = WordUtils.capitalize(definition.getTestMethod());
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
            .addStatement(
                "super($L.$L.class, $S, asList($T.values()))",
                feature.getTestClassPackage(),
                feature.getTestClassName(),
                definition.getTestMethod(),
                generatedSchema)
            .addStatement("this.$N = new $T(this)", requisitesField, requisitesClassName)
            .addStatement("this.$N = new $T(this)", expectationsField, expectationsClassName)
            .build();

    // CREATE METHODS

    final MethodSpec givenMethod =
        MethodSpec.methodBuilder("given")
            .addModifiers(Modifier.STATIC)
            .returns(requisitesClassName)
            .addStatement("return new $T().$N", scenarioClassName, requisitesField)
            .build();

    // CREATE INNER CLASSES

    final TypeSpec requisitesType =
        createRequisitesType(
            requisitesClassName,
            scenarioClassName,
            expectationsClassName,
            expectationsField,
            definition.getRequisites());

    final TypeSpec expectationsType =
        createExpectationsType(
            scenarioClassName, expectationsClassName, definition.getExpectations());

    // CREATE TYPE

    return TypeSpec.classBuilder(scenarioName)
        .superclass(SPECIFICATION_TYPE)
        .addModifiers(Modifier.STATIC)
        .addFields(asList(requisitesField, expectationsField))
        .addMethods(asList(constructor, givenMethod))
        .addTypes(asList(requisitesType, expectationsType))
        .build();
  }

  private static TypeSpec createRequisitesType(
      final ClassName blockClassName,
      final ClassName scenarioClassName,
      final ClassName expectationsClassName,
      final FieldSpec expectationsField,
      final List<Parameter> parameters) {
    final TypeSpec.Builder requisites =
        createScenarioBlockType(scenarioClassName, blockClassName, parameters);

    requisites.addMethod(
        MethodSpec.methodBuilder("then")
            .addStatement("return $T.this.$N", scenarioClassName, expectationsField)
            .returns(expectationsClassName)
            .build());

    return requisites.build();
  }

  private static TypeSpec createExpectationsType(
      final ClassName scenarioType, final ClassName stepType, final List<Parameter> parameters) {
    final TypeSpec.Builder requisites = createScenarioBlockType(scenarioType, stepType, parameters);
    return requisites.build();
  }

  private static TypeSpec.Builder createScenarioBlockType(
      final ClassName scenarioType, final ClassName stepType, final List<Parameter> parameters) {
    final List<FieldSpec> fields = new ArrayList<>();

    final List<MethodSpec> parameterSetters =
        parameters.stream()
            .map(
                parameter ->
                    createParameterSetter(
                        scenarioType, stepType, parameter.getType(), parameter.getName()))
            .collect(Collectors.toList());

    final ParameterSpec constructorParameter =
        ParameterSpec.builder(SPECIFICATION_TYPE, "testCase").addModifiers(Modifier.FINAL).build();

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addParameter(constructorParameter)
            .addStatement("super($N)", constructorParameter)
            .build();

    return TypeSpec.classBuilder(stepType)
        .superclass(SPECIFICATION_BLOCK_TYPE)
        .addFields(fields)
        .addMethods(parameterSetters)
        .addMethod(constructor);
  }

  private static MethodSpec createParameterSetter(
      final ClassName specificationClass,
      final ClassName blockName,
      final TypeMirror type,
      final String name) {

    final TypeName typeName = TypeName.get(type);

    final ParameterSpec parameterSpec =
        ParameterSpec.builder(typeName, name).addModifiers(Modifier.FINAL).build();

    final MethodSpec setter =
        MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(parameterSpec)
            .addStatement("$T.this.setValue($S, $N)", specificationClass, name, name)
            .addStatement("return this")
            .returns(blockName)
            .build();

    return setter;
  }

  public void generate(final Feature feature, final Map<Scenario, ClassName> generatedSchemas) {
    final ClassName featureClassName =
        ClassName.get(
            feature.getTestClassPackage(),
            format("%s_%s", feature.getTestClassName(), Example.Builder.class.getSimpleName()));

    final List<TypeSpec> innerClasses = new ArrayList<>();
    for (final Scenario scenario : feature.getScenarios()) {
      final TypeSpec specificationClass =
          createScenarioParameterBuilder(
              featureClassName, feature, scenario, generatedSchemas.get(scenario));
      innerClasses.add(specificationClass);
    }

    final TypeSpec featureClass =
        TypeSpec.classBuilder(featureClassName)
            .addSuperinterface(TypeName.get(com.kidsoncoffee.cheesecakes.Feature.class))
            .addTypes(innerClasses)
            .build();

    try {
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(feature.getTestClassPackage(), featureClass)
          .addStaticImport(Arrays.class, "asList")
          .build()
          .writeTo(this.filer);
    } catch (IOException e) {
      throw new UncheckedIOException(format("Error generating '%s'.", Feature.class), e);
    }
  }
}
