package com.kidsoncoffee.paramtests.generator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kidsoncoffee.paramtests.TestCaseParameters;
import com.kidsoncoffee.paramtests.TestCaseParametersBlock;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
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
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterClassGenerator implements ParameterizedTestsGenerator {

  private Filer filer;
  private Elements elementUtils;

  @Override
  public void init(final ProcessingEnvironment processingEnvironment) {
    this.filer = processingEnvironment.getFiler();
    this.elementUtils = processingEnvironment.getElementUtils();
  }

  @Override
  public List<Class<? extends Annotation>> getAnnotationsToProcess() {
    return asList(Requisites.class, Expectations.class);
  }

  @Override
  public List<ParameterizedTestsDefinition> createDefinitions(final List<Element> elements) {
    final List<Element> parameters =
        elements.stream()
            .filter(e -> e.getKind().equals(ElementKind.PARAMETER))
            .collect(Collectors.toList());

    // TODO fchovich VALIDATE THAT ALL PARAMETERS ARE ANNOTATED

    if (parameters.isEmpty()) {
      return Collections.emptyList();
    }

    final Table<Element, Element, List<Element>> groupedElements = groupElements(parameters);
    return groupedElements.cellSet().stream()
        .map(
            cell ->
                ImmutableParameterizedTestsDefinition.builder()
                    .testClassName(cell.getRowKey().getSimpleName().toString())
                    .testClassPackage(this.elementUtils.getPackageOf(cell.getRowKey()).toString())
                    .testMethodName(cell.getColumnKey().getSimpleName().toString())
                    .requisites(extract(Requisites.class, cell.getValue()))
                    .expectations(extract(Expectations.class, cell.getValue()))
                    .build())
        .collect(Collectors.toList());
  }

  private static Table<Element, Element, List<Element>> groupElements(List<Element> parameters) {
    final Table<Element, Element, List<Element>> groupedElements = HashBasedTable.create();

    for (final Element parameter : parameters) {
      final Element methodElement = parameter.getEnclosingElement();
      final Element classElement = methodElement.getEnclosingElement();

      if (!groupedElements.contains(classElement, methodElement)) {
        groupedElements.put(classElement, methodElement, new ArrayList<>());
      }
      groupedElements.get(classElement, methodElement).add(parameter);
    }

    return groupedElements;
  }

  private static List<ParameterizedTestsBlockDefinition> extract(
      final Class<? extends Annotation> annotation, final List<Element> elements) {
    final List<ParameterizedTestsBlockDefinition> blockDefinitions = new ArrayList<>();
    for (int i = 0; i < elements.size(); i++) {
      final Element element = elements.get(i);
      if (element.getAnnotation(annotation) == null) {
        continue;
      }
      blockDefinitions.add(
          ImmutableParameterizedTestsBlockDefinition.builder()
              .name(element.getSimpleName().toString())
              .type(element.asType())
              .overallOrder(i)
              .build());
    }
    return blockDefinitions;
  }

  @Override
  public boolean generate(final ParameterizedTestsDefinition def) {
    final TypeName parametersType = TypeName.get(TestCaseParameters.class);
    final TypeName parametersBlockType = TypeName.get(TestCaseParametersBlock.class);

    final ClassName classClassName =
        ClassName.get(
            def.getTestClassPackage(), String.format("%sParameters", def.getTestClassName()));

    final ClassName methodClassName =
        ClassName.get(classClassName.simpleName(), WordUtils.capitalize(def.getTestMethodName()));

    final ClassName requisitesClassName =
        ClassName.get(classClassName.simpleName(), methodClassName.simpleName(), "Requisites");
    final ClassName expectationsClassName =
        ClassName.get(classClassName.simpleName(), methodClassName.simpleName(), "Expectations");

    final FieldSpec requisitesField =
        FieldSpec.builder(requisitesClassName, "requisites")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    final FieldSpec expectationsField =
        FieldSpec.builder(expectationsClassName, "expectations")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();

    final ParameterizedTypeName injectionablesType =
        ParameterizedTypeName.get(List.class, Supplier.class);
    final FieldSpec injectionablesField =
        FieldSpec.builder(injectionablesType, "injectionables")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();

    final MethodSpec.Builder constructor =
        MethodSpec.constructorBuilder()
            .addStatement("this.$N = new $T(this)", requisitesField, requisitesClassName)
            .addStatement("this.$N = new $T(this)", expectationsField, expectationsClassName)
            .addStatement("this.$N = new $T<>()", injectionablesField, ArrayList.class);

    def.getRequisites()
        .forEach(
            d ->
                constructor.addStatement(
                    "this.$N.add(this.$N::$N)",
                    injectionablesField,
                    requisitesField,
                    formatGetterMethodName(d.getName())));

    def.getExpectations()
        .forEach(
            d ->
                constructor.addStatement(
                    "this.$N.add(this.$N::$N)",
                    injectionablesField,
                    expectationsField,
                    formatGetterMethodName(d.getName())));

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

    final MethodSpec injectionablesGetter =
        MethodSpec.methodBuilder("getInjectionables")
            .addStatement("return this.$N", injectionablesField)
            .addModifiers(Modifier.PUBLIC)
            .returns(injectionablesType)
            .build();

    final TypeSpec.Builder requisitesInnerClass =
        createParameterBlock(requisitesClassName, def.getRequisites(), parametersBlockType);

    final TypeSpec.Builder expectationsInnerClass =
        createParameterBlock(expectationsClassName, def.getExpectations(), parametersBlockType);

    requisitesInnerClass.addMethod(
        MethodSpec.methodBuilder("then")
            .addStatement("return $T.this.$N", methodClassName, expectationsField)
            .returns(expectationsClassName)
            .build());

    final MethodSpec givenMethod =
        MethodSpec.methodBuilder("given")
            .addModifiers(Modifier.STATIC)
            .returns(requisitesClassName)
            .addStatement("return new $T().$N", methodClassName, requisitesField)
            .build();

    final TypeSpec methodClass =
        TypeSpec.classBuilder(methodClassName)
            .addModifiers(Modifier.STATIC)
            .addSuperinterface(parametersType)
            .addFields(asList(requisitesField, expectationsField, injectionablesField))
            .addMethods(
                asList(
                    constructor.build(),
                    requisitesGetter,
                    expectationsGetter,
                    injectionablesGetter,
                    givenMethod))
            .addTypes(asList(requisitesInnerClass.build(), expectationsInnerClass.build()))
            .build();

    final TypeSpec classClass =
        TypeSpec.classBuilder(classClassName).addTypes(asList(methodClass)).build();

    try {
      JavaFile.builder(def.getTestClassPackage(), classClass).build().writeTo(this.filer);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private static TypeSpec.Builder createParameterBlock(
      final ClassName blockName,
      final List<ParameterizedTestsBlockDefinition> parameters,
      final TypeName parametersBlockType) {
    final List<FieldSpec> fields = new ArrayList<>();
    final List<MethodSpec> methods = new ArrayList<>();

    parameters.stream()
        .map(r -> createBuilder(blockName, r.getType(), r.getName()))
        .forEach(
            p -> {
              fields.add(p.getLeft());
              methods.addAll(p.getRight());
            });

    final FieldSpec testCaseField =
        FieldSpec.builder(TestCaseParameters.class, "testCase")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();

    fields.add(testCaseField);

    final ParameterSpec testCaseParameter =
        ParameterSpec.builder(TestCaseParameters.class, "testCase")
            .addModifiers(Modifier.FINAL)
            .build();

    methods.add(
        MethodSpec.constructorBuilder()
            .addParameter(testCaseParameter)
            .addStatement("this.$N = $N", testCaseField, testCaseParameter)
            .build());

    methods.add(
        MethodSpec.methodBuilder("getTestCase")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addStatement("return $N", testCaseField)
            .returns(ClassName.get(TestCaseParameters.class))
            .build());

    return TypeSpec.classBuilder(blockName)
        .addFields(fields)
        .addSuperinterface(parametersBlockType)
        .addMethods(methods);
  }

  private static Pair<FieldSpec, List<MethodSpec>> createBuilder(
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
        MethodSpec.methodBuilder(formatGetterMethodName(name))
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return this.$N", field)
            .returns(typeName)
            .build();

    return Pair.of(field, asList(setter, getter));
  }

  private static String formatGetterMethodName(final String name) {
    return String.format("get%s", WordUtils.capitalize(name));
  }
}
