package com.kidsoncoffee.paramtests.scenario;

import com.kidsoncoffee.cheesecakes.Feature;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
import com.kidsoncoffee.cheesecakes.SpecificationBlock;
import com.kidsoncoffee.cheesecakes.SpecificationParameter;
import com.kidsoncoffee.cheesecakes.SpecificationStepType;
import com.kidsoncoffee.paramtests.scenario.domain.ScenarioBlockDefinition;
import com.kidsoncoffee.paramtests.scenario.domain.ScenarioDefinition;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class SpecificationClassGenerator {

  private static final TypeName SPECIFICATION_TYPE = TypeName.get(Specification.class);

  private static final TypeName SCENARIO_BLOCK_TYPE = TypeName.get(SpecificationBlock.class);

  private static final TypeName PARAMETER_TYPE = TypeName.get(SpecificationParameter.class);

  private final Filer filer;

  public SpecificationClassGenerator(Filer filer) {
    this.filer = filer;
  }

  public void generate(
      final String testClassPackage,
      final String testClassName,
      final List<ScenarioDefinition> scenarios) {
    final ClassName featureClassName =
        ClassName.get(testClassPackage, format("%sParameters", testClassName));

    final List<TypeSpec> innerClasses = new ArrayList<>();
    for (ScenarioDefinition scenario : scenarios) {
      final TypeSpec schemaEnum = createSchemaEnum(featureClassName, scenario);
      final TypeSpec specificationClass =
          createSpecificationClass(featureClassName, scenario, schemaEnum);
      innerClasses.add(schemaEnum);
      innerClasses.add(specificationClass);
    }

    final TypeSpec featureClass =
        TypeSpec.classBuilder(featureClassName)
            .addSuperinterface(TypeName.get(Feature.class))
            .addTypes(innerClasses)
            .build();

    try {
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(testClassPackage, featureClass)
          .addStaticImport(Arrays.class, "asList")
          .build()
          .writeTo(this.filer);
    } catch (IOException e) {
      throw new UncheckedIOException(format("Error generating '%s'.", Feature.class), e);
    }
  }

  private static TypeSpec createSchemaEnum(
      final ClassName featureClassName, final ScenarioDefinition def) {
    final String specificationName = WordUtils.capitalize(def.getTestMethodName());
    final ClassName schemaClassName =
        ClassName.get(featureClassName.simpleName(), format("%sSchema", specificationName));

    final ParameterSpec nameParameter =
        ParameterSpec.builder(String.class, "name", Modifier.FINAL).build();
    final ParameterSpec typeParameter =
        ParameterSpec.builder(Class.class, "type", Modifier.FINAL).build();
    final ParameterSpec stepParameter =
        ParameterSpec.builder(SpecificationStepType.class, "step", Modifier.FINAL).build();

    final FieldSpec nameField = FieldSpec.builder(String.class, "name", Modifier.FINAL).build();
    final FieldSpec typeField = FieldSpec.builder(Class.class, "type", Modifier.FINAL).build();
    final FieldSpec stepField =
        FieldSpec.builder(SpecificationStepType.class, "step", Modifier.FINAL).build();

    final TypeSpec.Builder enumBuilder =
        TypeSpec.enumBuilder(schemaClassName)
            .addSuperinterface(PARAMETER_TYPE)
            .addFields(asList(nameField, typeField, stepField))
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addParameters(asList(nameParameter, typeParameter, stepParameter))
                    .addStatement("this.$N = $N", nameField, nameParameter)
                    .addStatement("this.$N = $N", typeField, typeParameter)
                    .addStatement("this.$N = $N", stepField, stepParameter)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getName")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", nameField)
                    .returns(String.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getType")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", typeField)
                    .returns(Class.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getStep")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", stepField)
                    .returns(SpecificationStepType.class)
                    .build());

    def.getRequisites()
        .forEach(
            r -> createParameterSchemaConstant(enumBuilder, r, SpecificationStepType.REQUISITE));
    def.getExpectations()
        .forEach(
            r -> createParameterSchemaConstant(enumBuilder, r, SpecificationStepType.EXPECTATION));
    return enumBuilder.build();
  }

  private static TypeSpec.Builder createParameterSchemaConstant(
      TypeSpec.Builder enumBuilder, ScenarioBlockDefinition r, SpecificationStepType stepType) {
    return enumBuilder.addEnumConstant(
        r.getParameterName().toUpperCase(),
        TypeSpec.anonymousClassBuilder(
                "$S, $T.class, $T.$L",
                r.getParameterName(),
                r.getParameterType(),
                SpecificationStepType.class,
                stepType.name())
            .build());
  }

  private static TypeSpec createSpecificationClass(
      final ClassName scenariosType, final ScenarioDefinition definition, TypeSpec schemaEnum) {
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
            .addStatement("super(asList($N.values()))", schemaEnum)
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

    // CREATE ANNOTATIONS
    final AnnotationSpec bindingAnnotation =
        AnnotationSpec.builder(Parameters.ScenarioBinding.class)
            .addMember("testClass", "$N.class", definition.getTestClassName())
            .addMember("testMethod", "$S", definition.getTestMethodName())
            .build();

    // CREATE TYPE

    return TypeSpec.classBuilder(scenarioName)
        .superclass(SPECIFICATION_TYPE)
        .addAnnotation(bindingAnnotation)
        .addModifiers(Modifier.STATIC)
        .addFields(asList(requisitesField, expectationsField))
        .addMethods(asList(constructor, givenMethod))
        .addTypes(asList(requisitesType, expectationsType))
        .build();
  }

  private static MethodSpec createParameterNamesGetter(ScenarioDefinition definition) {
    final ParameterizedTypeName pairType =
        ParameterizedTypeName.get(Pair.class, Class.class, String.class);
    final TypeName listType = ParameterizedTypeName.get(ClassName.get(List.class), pairType);

    final MethodSpec.Builder getter =
        MethodSpec.methodBuilder("getParameterNames")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(listType)
            .addStatement("final $T<$T> names = new $T<>()", List.class, pairType, ArrayList.class);

    Stream.concat(definition.getRequisites().stream(), definition.getExpectations().stream())
        .sorted((o1, o2) -> o1.getOverallOrder())
        .forEach(
            d ->
                getter.addStatement(
                    "names.add($T.of($T.class,$S))",
                    Pair.class,
                    d.getAnnotationType(),
                    d.getParameterName()));

    return getter.addStatement("return names").build();
  }

  private static TypeSpec createRequisitesType(
      final ClassName blockClassName,
      final ClassName scenarioClassName,
      final ClassName expectationsClassName,
      final FieldSpec expectationsField,
      final List<ScenarioBlockDefinition> parameters) {
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
      final ClassName scenarioType,
      final ClassName stepType,
      final List<ScenarioBlockDefinition> parameters) {
    final TypeSpec.Builder requisites = createScenarioBlockType(scenarioType, stepType, parameters);
    return requisites.build();
  }

  private static TypeSpec.Builder createScenarioBlockType(
      final ClassName scenarioType,
      final ClassName stepType,
      final List<ScenarioBlockDefinition> parameters) {
    final List<FieldSpec> fields = new ArrayList<>();

    final List<MethodSpec> parameterSetters =
        parameters.stream()
            .map(
                parameter ->
                    createParameterSetter(
                        scenarioType,
                        stepType,
                        parameter.getParameterType(),
                        parameter.getParameterName()))
            .collect(Collectors.toList());

    final ParameterSpec constructorParameter =
        ParameterSpec.builder(SPECIFICATION_TYPE, "testCase").addModifiers(Modifier.FINAL).build();

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addParameter(constructorParameter)
            .addStatement("super($N)", constructorParameter)
            .build();

    return TypeSpec.classBuilder(stepType)
        .superclass(SCENARIO_BLOCK_TYPE)
        .addFields(fields)
        .addMethods(parameterSetters)
        .addMethod(constructor);
  }

  private static MethodSpec createParameterSetter(
      ClassName specificationClass, ClassName blockName, final TypeMirror type, final String name) {

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
}
