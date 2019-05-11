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

  public void invalidModifierConstructor(
      @Parameter.Conversion(InvalidModifierConstructorConverter.class)
          final String notPublicConverter,
      @Parameter.Conversion(InvalidParametersConstructorConverter.class)
          final String parameterizedConstructor) {}

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

  public static class InvalidModifierConstructorConverter extends DummyConverter {
    private InvalidModifierConstructorConverter() {
      // this should be public
    }
  }

  public static class InvalidParametersConstructorConverter extends DummyConverter {
    public InvalidParametersConstructorConverter(final String parameter) {
      // this should not have any parameters
    }
  }

  public static class ExplodingConstructorConverter extends DummyConverter {
    public ExplodingConstructorConverter() {
      throw new UnsupportedOperationException("KA BO OM !");
    }
  }
}
