package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.CheesecakesException;

import java.util.Arrays;
import java.util.function.Function;

import static com.kidsoncoffee.cheesecakes.ImmutableRegistrableConverter.of;

/**
 * Provides all default {@link Parameter.RegistrableConverter}s.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public enum DefaultParameterConverters {

  /** String converter. */
  STRING(of(String.class, Parameter.Convertible::getValue)),

  /** Byte converter. */
  BYTE(Byte.class, Byte::parseByte),

  /** Primitive byte converter. */
  BYTE_PRIMITIVE(byte.class, Byte::parseByte),

  /** Short converter */
  SHORT(Short.class, Short::parseShort),

  /** Primitive short converter. */
  SHORT_PRIMITIVE(short.class, Short::parseShort),

  /** Integer converter. */
  INTEGER(Integer.class, Integer::parseInt),

  /** Primitive integer converter. */
  INTEGER_PRIMITIVE(int.class, Integer::parseInt),

  /** Long converter. */
  LONG(Long.class, Long::parseLong),

  /** Primitive long converter. */
  LONG_PRIMITIVE(long.class, Long::parseLong),

  /** Float converter. */
  FLOAT(Float.class, Float::parseFloat),

  /** Primitive float converter. */
  FLOAT_PRIMITIVE(float.class, Float::parseFloat),

  /** Double converter. */
  DOUBLE(Double.class, Double::parseDouble),

  /** Primitive double converter. */
  DOUBLE_PRIMITIVE(double.class, Double::parseDouble),

  /** Boolean converter. */
  BOOLEAN(Boolean.class, Boolean::parseBoolean),

  /** Primitive boolean converter. */
  BOOLEAN_PRIMITIVE(boolean.class, Boolean::parseBoolean),

  /** Class converter. */
  CLASS(
      Class.class,
      className -> {
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException e) {
          throw new CheesecakesException(
              String.format("Error converting String '%s' to Class.", className), e);
        }
      }),

  /** Enum constant converter. */
  ENUM(
      of(
          Enum.class,
          convertableParameter ->
              (Enum)
                  Arrays.stream(convertableParameter.getSchema().getType().getEnumConstants())
                      .filter(
                          constant ->
                              constant.toString().equalsIgnoreCase(convertableParameter.getValue()))
                      .findFirst()
                      .orElseThrow(
                          () ->
                              new CheesecakesException(
                                  String.format(
                                      "Error converting Enum constant '%s' to '%s'.",
                                      convertableParameter.getValue(),
                                      convertableParameter.getSchema().getType())))));

  /** The converter. */
  private final Parameter.RegistrableConverter converter;

  /**
   * Constructs a default parameter converter with a underlying {@link Parameter.Converter}.
   *
   * @param converter The underlying parameter converter.
   */
  DefaultParameterConverters(final Parameter.RegistrableConverter converter) {
    this.converter = converter;
  }

  /**
   * Constructs a default parameter converter with a simple string to object conversion. If more
   * information needed, i.e. Schema, use the constructor that receives the pre-build constructor.
   *
   * @param clazz The target class.
   * @param converter The conversion function from String to target.
   * @param <R> The type that matches the target.
   */
  <R> DefaultParameterConverters(final Class<R> clazz, final Function<String, R> converter) {
    this(of(clazz, t -> converter.apply(t.getValue())));
  }

  /**
   * Returns the underlying parameter converter.
   *
   * @return The parameter converter.
   */
  public Parameter.RegistrableConverter getConverter() {
    return converter;
  }
}
