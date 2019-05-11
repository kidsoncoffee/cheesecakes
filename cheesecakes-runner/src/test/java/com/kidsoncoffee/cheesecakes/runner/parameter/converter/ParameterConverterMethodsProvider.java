package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Parameter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.function.Function;

/**
 * Provider of methods for the {@link CustomConverterExtractorTest}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterConverterMethodsProvider {

  /** Method without parameters. */
  public void methodWithoutParameters() {}

  /**
   * Method with parameters but without converters.
   *
   * @param parameters Parameter without assigned converter.
   * @param without Parameter without assigned converter.
   * @param annotations Parameter without assigned converter.
   */
  public void methodWithoutConverters(
      final String parameters, final String without, final String annotations) {}

  /**
   * Method with some parameters annotated for conversion.
   *
   * @param parameterWithoutAnnotation Parameter with assigned converter.
   * @param parameterWithAnnotation Parameter without assigned converter.
   */
  public void methodWithConverter(
      final String parameterWithoutAnnotation,
      @Parameter.Conversion(DummyConverter.class) final String parameterWithAnnotation) {}

  /**
   * Method with annotated converters with invalid constructors.
   *
   * @param notPublicConverter Parameter with converter with not public constructor.
   * @param parameterizedConstructor Parameter with converter with converter with parameters.
   */
  public void invalidModifierConstructor(
      @Parameter.Conversion(InvalidModifierConstructorConverter.class)
          final String notPublicConverter,
      @Parameter.Conversion(InvalidParametersConstructorConverter.class)
          final String parameterizedConstructor) {}

  /**
   * Method with annotated converters which throws an exception in runtime.
   *
   * @param explodes Parameter with converter with constructor that throws an exception.
   */
  public void explodingConstructor(
      @Parameter.Conversion(ExplodingConstructorConverter.class) final String explodes) {}

  /** Dummy converter. */
  public static class DummyConverter implements Parameter.Converter<String> {
    @Override
    public Function<Parameter.ConvertableParameter, String> getConverter() {
      return Parameter.ConvertableParameter::getValue;
    }

    @Override
    public Class<String> getTargetType() {
      return String.class;
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
  }

  /** Converter with private constructor. */
  public static class InvalidModifierConstructorConverter extends DummyConverter {

    /** Private constructor. */
    private InvalidModifierConstructorConverter() {
      // this should be public
    }
  }

  /** Converter with constructor with parameters. */
  public static class InvalidParametersConstructorConverter extends DummyConverter {

    /**
     * Constructor with parameter.
     *
     * @param parameter The parameter.
     */
    public InvalidParametersConstructorConverter(final String parameter) {
      // this should not have any parameters
    }
  }

  /** Converter with constructor that throws an exception. */
  public static class ExplodingConstructorConverter extends DummyConverter {

    /** Constructor that throws an exception. */
    public ExplodingConstructorConverter() {
      throw new UnsupportedOperationException("KA BO OM !");
    }
  }
}
