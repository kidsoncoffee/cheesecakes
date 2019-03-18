package com.kidsoncoffee.paramtests.generator;

import com.kidsoncoffee.paramtests.TestCaseParameters;
import com.kidsoncoffee.paramtests.TestCaseParametersBlock;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public void init(ProcessingEnvironment processingEnvironment) {
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

    final Map<Element, List<Element>> elementsByClass = new HashMap<>();

    for (final Element parameter : parameters) {
      final Element testClass = parameter.getEnclosingElement().getEnclosingElement();

      elementsByClass.putIfAbsent(testClass, new ArrayList<>());
      elementsByClass.get(testClass).add(parameter);
    }

    return elementsByClass.entrySet().stream()
        .map(
            entry ->
                ImmutableParameterizedTestsDefinition.builder()
                    .testClassName(entry.getKey().getSimpleName().toString())
                    .testClassPackage(this.elementUtils.getPackageOf(entry.getKey()).toString())
                    .requisites(extract(Requisites.class, entry))
                    .expectations(extract(Expectations.class, entry))
                    .build())
        .collect(Collectors.toList());
  }

  private Map<String, TypeMirror> extract(
      final Class<? extends Annotation> annotation, final Map.Entry<Element, List<Element>> entry) {
    return entry.getValue().stream()
        .filter(e -> e.getAnnotation(annotation) != null)
        .collect(Collectors.toMap(i -> i.getSimpleName().toString(), Element::asType));
  }

  @Override
  public boolean generate(final ParameterizedTestsDefinition definition) {
    final TypeName parametersType = TypeName.get(TestCaseParameters.class);
    final TypeName parametersBlockType = TypeName.get(TestCaseParametersBlock.class);

    final ClassName className =
        ClassName.get(
            definition.getTestClassPackage(),
            String.format("%sParameters", definition.getTestClassName()));

    final ClassName requisitesClassName = ClassName.get(className.simpleName(), "Requisites");
    final ClassName expectationsClassName = ClassName.get(className.simpleName(), "Expectations");

    final FieldSpec requisitesField =
        FieldSpec.builder(requisitesClassName, "requisites")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    final FieldSpec expectationsField =
        FieldSpec.builder(expectationsClassName, "expectations")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();

    final MethodSpec constructor =
        MethodSpec.constructorBuilder()
            .addStatement("this.$N = new $T(this)", requisitesField, requisitesClassName)
            .addStatement("this.$N = new $T(this)", expectationsField, expectationsClassName)
            .build();

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

    final TypeSpec.Builder requisitesInnerClass =
        createParameterBlock(
            className, requisitesClassName, definition.getRequisites(), parametersBlockType);

    final TypeSpec.Builder expectationsInnerClass =
        createParameterBlock(
            className, expectationsClassName, definition.getExpectations(), parametersBlockType);

    requisitesInnerClass.addMethod(
        MethodSpec.methodBuilder("then")
            .addStatement("return $T.this.$N", className, expectationsField)
            .returns(expectationsClassName)
            .build());

    final MethodSpec givenMethod =
        MethodSpec.methodBuilder("given")
            .addModifiers(Modifier.STATIC)
            .returns(requisitesClassName)
            .addStatement("return new $T().$N", className, requisitesField)
            .build();

    final TypeSpec typeSpec =
        TypeSpec.classBuilder(className)
            .addSuperinterface(parametersType)
            .addFields(asList(requisitesField, expectationsField))
            .addMethods(asList(constructor, requisitesGetter, expectationsGetter, givenMethod))
            .addTypes(asList(requisitesInnerClass.build(), expectationsInnerClass.build()))
            .build();

    try {
      JavaFile.builder(definition.getTestClassPackage(), typeSpec).build().writeTo(this.filer);
      return true;
    } catch (IOException e) {
      /*throw new UncheckedIOException(
      String.format(
          "Unable to write Parameters class for '%s.%s'.",
          definition.getTestClassPackage(), definition.getTestClassName()),
      e);*/
      return false;
    }
  }

  private static TypeSpec.Builder createParameterBlock(
      final ClassName parametersClassName,
      final ClassName blockName,
      final Map<String, TypeMirror> parameters,
      final TypeName parametersBlockType) {
    final List<FieldSpec> fields = new ArrayList<>();
    final List<MethodSpec> methods = new ArrayList<>();

    parameters.entrySet().stream()
        .map(r -> createBuilder(blockName, r.getValue(), r.getKey()))
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
        MethodSpec.methodBuilder(String.format("get%s", WordUtils.capitalize(name)))
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return this.$N", field)
            .returns(typeName)
            .build();

    return Pair.of(field, asList(setter, getter));
  }
}
