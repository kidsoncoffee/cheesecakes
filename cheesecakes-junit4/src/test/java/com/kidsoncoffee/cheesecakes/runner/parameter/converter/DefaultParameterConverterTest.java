package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.ImmutableConvertible;
import com.kidsoncoffee.cheesecakes.ImmutableSchema;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.Scenario;
import com.kidsoncoffee.cheesecakes.runner.CheesecakesException;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static java.lang.Byte.MIN_VALUE;

/**
 * Unit tests for {@link DefaultParameterConverters}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class DefaultParameterConverterTest {

  /** The string representation of an enum. */
  private static final String TO_STRING_REPRESENTATION = "TO STRING REPRESENTATION";

  @Test
  public void stringConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<String> converter;
    final String converted;

    given:
    converter = DefaultParameterConverters.STRING.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("A")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo("A");
  }

  @Test
  public void byteConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Byte> converter;
    final Byte converted;

    given:
    converter = DefaultParameterConverters.BYTE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value(Byte.toString(MIN_VALUE))
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(MIN_VALUE);
  }

  @Test
  public void primitiveByteConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Byte> converter;
    final byte converted;

    given:
    converter = DefaultParameterConverters.BYTE_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value(Byte.toString(MIN_VALUE))
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(MIN_VALUE);
  }

  @Test
  public void shortConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Short> converter;
    final Short converted;

    given:
    converter = DefaultParameterConverters.SHORT.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(new Short("123"));
  }

  @Test
  public void primitiveShortConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Short> converter;
    final short converted;

    given:
    converter = DefaultParameterConverters.SHORT_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(new Short("123"));
  }

  @Test
  public void integerConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Integer> converter;
    final Integer converted;

    given:
    converter = DefaultParameterConverters.INTEGER.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123);
  }

  @Test
  public void primitiveIntegerConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Integer> converter;
    final int converted;

    given:
    converter = DefaultParameterConverters.INTEGER_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123);
  }

  @Test
  public void longConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Long> converter;
    final Long converted;

    given:
    converter = DefaultParameterConverters.LONG.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123L);
  }

  @Test
  public void primitiveLongConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Long> converter;
    final long converted;

    given:
    converter = DefaultParameterConverters.LONG_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123L);
  }

  @Test
  public void floatConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Float> converter;
    final Float converted;

    given:
    converter = DefaultParameterConverters.FLOAT.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123.45")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123.45F);
  }

  @Test
  public void primitiveFloatConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Float> converter;
    final float converted;

    given:
    converter = DefaultParameterConverters.FLOAT_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123.45")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123.45F);
  }

  @Test
  public void doubleConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Double> converter;
    final Double converted;

    given:
    converter = DefaultParameterConverters.DOUBLE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123.45")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123.45D);
  }

  @Test
  public void primitiveDoubleConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Double> converter;
    final double converted;

    given:
    converter = DefaultParameterConverters.DOUBLE_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("123.45")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(123.45D);
  }

  @Test
  public void booleanConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Boolean> converter;
    final Boolean converted;

    given:
    converter = DefaultParameterConverters.BOOLEAN.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("true")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(new Boolean("true"));
  }

  @Test
  public void primitiveBooleanConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Boolean> converter;
    final boolean converted;

    given:
    converter = DefaultParameterConverters.BOOLEAN_PRIMITIVE.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("false")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(false);
  }

  @Test
  public void classConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Class> converter;
    final Class converted;

    given:
    converter = DefaultParameterConverters.CLASS.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("java.lang.String")
            .schema(anySchema())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(String.class);
  }

  @Test
  public void classConverterException() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Class> converter;
    final AbstractThrowableAssert<?, ? extends Throwable> expectedException;

    given:
    converter = DefaultParameterConverters.CLASS.getConverter();

    parameter =
        ImmutableConvertible.convertableParameter()
            .method(anyMethod())
            .value("java.lang.Stringer")
            .schema(anySchema())
            .build();

    when:
    expectedException = Assertions.assertThatThrownBy(() -> converter.convert(parameter));

    then:
    expectedException
        .isInstanceOf(CheesecakesException.class)
        .hasMessage("Error converting String 'java.lang.Stringer' to Class.");
  }

  @Test
  public void simpleEnumConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Enum> converter;
    final Enum converted;

    given:
    converter = DefaultParameterConverters.ENUM.getConverter();

    // TODO fchovich CREATE MOO
    parameter =
        ImmutableConvertible.convertableParameter()
            .schema(
                ImmutableSchema.schema()
                    .name("name")
                    .step(Scenario.StepType.REQUISITE)
                    .overallOrder(0)
                    .type(SimpleEnum.class)
                    .build())
            .value("A")
            .method(anyMethod())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(SimpleEnum.A);
  }

  @Test
  public void toStringEnumConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Enum> converter;
    final Enum converted;

    given:
    converter = DefaultParameterConverters.ENUM.getConverter();

    // TODO fchovich CREATE MOO
    parameter =
        ImmutableConvertible.convertableParameter()
            .schema(
                ImmutableSchema.schema()
                    .name(TO_STRING_REPRESENTATION)
                    .step(Scenario.StepType.REQUISITE)
                    .overallOrder(0)
                    .type(SimpleEnum.class)
                    .build())
            .value("A")
            .method(anyMethod())
            .build();

    when:
    converted = converter.convert(parameter);

    then:
    Assertions.assertThat(converted).isEqualTo(SimpleEnum.A);
  }

  @Test
  public void enumExceptionConverter() {
    final Parameter.Convertible parameter;
    final Parameter.RegistrableConverter<Enum> converter;
    final AbstractThrowableAssert<?, ? extends Throwable> exception;

    given:
    converter = DefaultParameterConverters.ENUM.getConverter();

    // TODO fchovich CREATE MOO
    parameter =
        ImmutableConvertible.convertableParameter()
            .schema(
                ImmutableSchema.schema()
                    .name("")
                    .step(Scenario.StepType.REQUISITE)
                    .overallOrder(0)
                    .type(SimpleEnum.class)
                    .build())
            .value("INVALID VALUE")
            .method(anyMethod())
            .build();

    when:
    exception = Assertions.assertThatThrownBy(() -> converter.convert(parameter));

    then:
    exception
        .isInstanceOf(CheesecakesException.class)
        .hasMessage(
            "Error converting Enum constant 'INVALID VALUE' to 'class com.kidsoncoffee.cheesecakes.runner.parameter.converter.DefaultParameterConverterTest$SimpleEnum'.");
  }

  enum SimpleEnum {
    A
  }

  enum ToStringEnum {
    A(TO_STRING_REPRESENTATION);

    private final String name;

    ToStringEnum(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  /**
   * Returns a method with the given name from {@link ParameterConverterMethodsProvider}.
   *
   * @param methodName The name of the method to retrieve.
   * @return The method that matches the name.
   */
  private static Method retrieveMethod(final String methodName) {
    return Arrays.stream(ParameterConverterMethodsProvider.class.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "The setup for this test is incorrect. Method not found."));
  }

  // TODO fchovich CREATE BETTER OBJECTS AND REMOVE THIS
  private static Method anyMethod() {
    return retrieveMethod("methodWithoutParameters");
  }

  private static Parameter.Schema anySchema() {
    return ImmutableSchema.schema()
        .name("")
        .step(Scenario.StepType.REQUISITE)
        .type(String.class)
        .overallOrder(0)
        .build();
  }
}
