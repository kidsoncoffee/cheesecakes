package com.kidsoncoffee.paramtests.generator;

import com.google.auto.service.AutoService;
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
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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
@AutoService(ParameterizedTestsGenerator.class)
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
  public void generate(final ParameterizedTestsDefinition definition) {
    final TypeName parametersType = TypeName.get(TestCaseParameters.class);
    final TypeName parametersBlockType = TypeName.get(TestCaseParametersBlock.class);

    final ClassName className =
        ClassName.get(
            definition.getTestClassPackage(),
            String.format("%sParameters", definition.getTestClassName()));

    final ClassName requisitesClassName = ClassName.get(className.simpleName(), "Requisites");
    final ClassName expectationsClassName = ClassName.get(className.simpleName(), "Expectations");

    final FieldSpec requisitesField = FieldSpec.builder(requisitesClassName, "requisites").build();
    final FieldSpec expectationsField =
        FieldSpec.builder(expectationsClassName, "expectations").build();

    final TypeSpec.Builder requisitesInnerClass =
        createParameterBlock(
            requisitesClassName, definition.getRequisites(), parametersType, parametersBlockType);

    final TypeSpec.Builder expectationsInnerClass =
        createParameterBlock(
            expectationsClassName,
            definition.getExpectations(),
            parametersType,
            parametersBlockType);

    requisitesInnerClass.addMethod(
        MethodSpec.methodBuilder("then")
            .addStatement("return this.$N", expectationsField)
            .returns(expectationsClassName)
            .build());

    final MethodSpec givenMethod =
        MethodSpec.methodBuilder("given")
            .addModifiers(Modifier.STATIC)
            .returns(parametersBlockType)
            .addStatement("return this.$N", requisitesField)
            .build();

    final TypeSpec typeSpec =
        TypeSpec.classBuilder(className)
            .addSuperinterface(parametersType)
            .addFields(asList(requisitesField, expectationsField))
            .addMethods(asList(givenMethod))
            .addTypes(asList(requisitesInnerClass.build(), expectationsInnerClass.build()))
            .build();

    try {
      JavaFile.builder(definition.getTestClassPackage(), typeSpec).build().writeTo(this.filer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static TypeSpec.Builder createParameterBlock(
      final ClassName blockName,
      final Map<String, TypeMirror> parameters,
      final TypeName parametersType,
      final TypeName parametersBlockType) {
    final List<FieldSpec> fields = new ArrayList<>();
    final List<MethodSpec> methods = new ArrayList<>();

    parameters.entrySet().stream()
        .map(r -> createBuilder(parametersType, r.getValue(), r.getKey()))
        .forEach(
            p -> {
              fields.add(p.getLeft());
              methods.addAll(p.getRight());
            });

    return TypeSpec.classBuilder(blockName)
        .addSuperinterface(parametersBlockType)
        .addFields(fields)
        .addMethods(methods);
  }

  private static Pair<FieldSpec, List<MethodSpec>> createBuilder(
          final TypeName parameterType, final TypeMirror type, final String name) {

    final TypeName typeName = TypeName.get(type);

    final FieldSpec field =
        FieldSpec.builder(typeName, name).addModifiers(Modifier.PRIVATE, Modifier.FINAL).build();

    final ParameterSpec parameterSpec =
        ParameterSpec.builder(typeName, name).addModifiers(Modifier.FINAL).build();

    final MethodSpec setter =
        MethodSpec.methodBuilder(name)
            .addParameter(parameterSpec)
            .addStatement("this.$N = $N", field, parameterSpec)
            .addStatement("return this")
            .returns(parameterType)
            .build();

    final MethodSpec getter =
        MethodSpec.methodBuilder(String.format("get%s", WordUtils.capitalize(name)))
            .addStatement("return this.$N", field)
            .returns(parameterType)
            .build();

    return Pair.of(field, asList(setter, getter));
  }

  /*static class SingleTestCaseTestDefinitionExampleTestParameters implements TestCase {

  static final Requisites given() {
      return new SingleTestCaseTestDefinitionExampleTestParameters().requisites;
  }

  final Requisites requisites;
  final Expectations expectations;

  public SingleTestCaseTestDefinitionExampleTestParameters() {
      this.requisites = new Requisites(this);
      this.expectations = new Expectations(this);
  }

  class Requisites implements TestCase {
      private final TestCase testCase;

      private String name;

      private String surname;

      public Requisites(TestCase testCase) {
          this.testCase = testCase;
      }

      public String getName() {
          return name;
      }

      public Requisites name(String name) {
          this.name = name;
          return this;
      }

      public String getSurname() {
          return surname;
      }

      public Requisites surname(String surname) {
          this.surname = surname;
          return this;
      }

      Expectations then() {
          return expectations;
      }

      @Override
      public TestCase get() {
          return this.testCase;
      }
  }

  class Expectations implements TestCase {
      private final TestCase testCase;
      */
  /*
          private String fullName;

          public Expectations(TestCase testCase) {
              this.testCase = testCase;
          }

          public String getFullName() {
              return fullName;
          }

          public Expectations fullname(String fullName) {
              this.fullName = fullName;
              return this;
          }

          @Override
          public TestCase get() {
              return this.testCase;
          }
      }

      @Override
      public TestCase get() {
          return this;
      }
  }*/
}
