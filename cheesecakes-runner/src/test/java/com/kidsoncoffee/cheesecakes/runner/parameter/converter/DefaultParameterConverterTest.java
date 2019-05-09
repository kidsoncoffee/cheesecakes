package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.CheesecakesException;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static java.lang.Byte.MIN_VALUE;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class DefaultParameterConverterTest {

  @Test
  public void stringConverter() {
    final Parameter.RegistrableConverter<String> converter;
    final String converted;

    given:
    converter = DefaultParameterConverters.STRING.getConverter();

    when:
    converted = converter.convert("A");

    then:
    Assertions.assertThat(converted).isEqualTo("A");
  }

  @Test
  public void byteConverter() {
    final Parameter.RegistrableConverter<Byte> converter;
    final Byte converted;

    given:
    converter = DefaultParameterConverters.BYTE.getConverter();

    when:
    converted = converter.convert(Byte.toString(MIN_VALUE));

    then:
    Assertions.assertThat(converted).isEqualTo(MIN_VALUE);
  }

  @Test
  public void primitiveByteConverter() {
    final Parameter.RegistrableConverter<Byte> converter;
    final byte converted;

    given:
    converter = DefaultParameterConverters.BYTE_PRIMITIVE.getConverter();

    when:
    converted = converter.convert(Byte.toString(MIN_VALUE));

    then:
    Assertions.assertThat(converted).isEqualTo(MIN_VALUE);
  }

  @Test
  public void shortConverter() {
    final Parameter.RegistrableConverter<Short> converter;
    final Short converted;

    given:
    converter = DefaultParameterConverters.SHORT.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(new Short("123"));
  }

  @Test
  public void primitiveShortConverter() {
    final Parameter.RegistrableConverter<Short> converter;
    final short converted;

    given:
    converter = DefaultParameterConverters.SHORT_PRIMITIVE.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(new Short("123"));
  }

  @Test
  public void integerConverter() {
    final Parameter.RegistrableConverter<Integer> converter;
    final Integer converted;

    given:
    converter = DefaultParameterConverters.INTEGER.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123);
  }

  @Test
  public void primitiveIntegerConverter() {
    final Parameter.RegistrableConverter<Integer> converter;
    final int converted;

    given:
    converter = DefaultParameterConverters.INTEGER_PRIMITIVE.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123);
  }

  @Test
  public void longConverter() {
    final Parameter.RegistrableConverter<Long> converter;
    final Long converted;

    given:
    converter = DefaultParameterConverters.LONG.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123L);
  }

  @Test
  public void primitiveLongConverter() {
    final Parameter.RegistrableConverter<Long> converter;
    final long converted;

    given:
    converter = DefaultParameterConverters.LONG_PRIMITIVE.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123L);
  }

  @Test
  public void floatConverter() {
    final Parameter.RegistrableConverter<Float> converter;
    final Float converted;

    given:
    converter = DefaultParameterConverters.FLOAT.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123F);
  }

  @Test
  public void primitiveFloatConverter() {
    final Parameter.RegistrableConverter<Float> converter;
    final float converted;

    given:
    converter = DefaultParameterConverters.FLOAT_PRIMITIVE.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123F);
  }

  @Test
  public void doubleConverter() {
    final Parameter.RegistrableConverter<Double> converter;
    final Double converted;

    given:
    converter = DefaultParameterConverters.DOUBLE.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123D);
  }

  @Test
  public void primitiveDoubleConverter() {
    final Parameter.RegistrableConverter<Double> converter;
    final double converted;

    given:
    converter = DefaultParameterConverters.DOUBLE_PRIMITIVE.getConverter();

    when:
    converted = converter.convert("123");

    then:
    Assertions.assertThat(converted).isEqualTo(123D);
  }

  @Test
  public void booleanConverter() {
    final Parameter.RegistrableConverter<Boolean> converter;
    final Boolean converted;

    given:
    converter = DefaultParameterConverters.BOOLEAN.getConverter();

    when:
    converted = converter.convert("true");

    then:
    Assertions.assertThat(converted).isEqualTo(new Boolean("true"));
  }

  @Test
  public void primitiveBooleanConverter() {
    final Parameter.RegistrableConverter<Boolean> converter;
    final boolean converted;

    given:
    converter = DefaultParameterConverters.BOOLEAN_PRIMITIVE.getConverter();

    when:
    converted = converter.convert("false");

    then:
    Assertions.assertThat(converted).isEqualTo(false);
  }

  @Test
  public void classConverter() {
    final Parameter.RegistrableConverter<Class> converter;
    final Class converted;

    given:
    converter = DefaultParameterConverters.CLASS.getConverter();

    when:
    converted = converter.convert("java.lang.String");

    then:
    Assertions.assertThat(converted).isEqualTo(String.class);
  }

  @Test
  public void classConverterException() {
    final Parameter.RegistrableConverter<Class> converter;
    final AbstractThrowableAssert<?, ? extends Throwable> expectedException;

    given:
    converter = DefaultParameterConverters.CLASS.getConverter();

    when:
    expectedException =
        Assertions.assertThatThrownBy(() -> converter.convert("java.lang.Stringer"));

    then:
    expectedException
        .isInstanceOf(CheesecakesException.class)
        .hasMessage("Error converting String 'java.lang.Stringer' to Class.");
  }
}
