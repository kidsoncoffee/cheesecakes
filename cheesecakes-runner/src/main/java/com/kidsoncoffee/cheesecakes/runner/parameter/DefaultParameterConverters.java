package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.CheesecakesException;

import static com.kidsoncoffee.cheesecakes.ImmutableRegistrableConverter.of;

/**
 * Provides all default {@link Parameter.RegistrableConverter}s.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public enum DefaultParameterConverters {

  /** String converter. */
  STRING(of(String.class, s -> s)),

  /** Byte converter. */
  BYTE(of(Byte.class, Byte::parseByte)),

  /** Primitive byte converter. */
  BYTE_PRIMITIVE(of(byte.class, Byte::parseByte)),

  /** Short converter */
  SHORT(of(Short.class, Short::parseShort)),

  /** Primitive short converter. */
  SHORT_PRIMITIVE(of(short.class, Short::parseShort)),

  /** Integer converter. */
  INTEGER(of(Integer.class, Integer::parseInt)),

  /** Primitive integer converter. */
  INTEGER_PRIMITIVE(of(int.class, Integer::parseInt)),

  /** Long converter. */
  LONG(of(Long.class, Long::parseLong)),

  /** Primitive long converter. */
  LONG_PRIMITIVE(of(long.class, Long::parseLong)),

  /** Float converter. */
  FLOAT(of(Float.class, Float::parseFloat)),

  /** Primitive float converter. */
  FLOAT_PRIMITIVE(of(float.class, Float::parseFloat)),

  /** Double converter. */
  DOUBLE(of(Double.class, Double::parseDouble)),

  /** Primitive double converter. */
  DOUBLE_PRIMITIVE(of(double.class, Double::parseDouble)),

  /** Boolean converter. */
  BOOLEAN(of(Boolean.class, Boolean::parseBoolean)),

  /** Primitive boolean converter. */
  BOOLEAN_CONVERTER(of(boolean.class, Boolean::parseBoolean)),

  /** Class converter. */
  CLASS(
      of(
          Class.class,
          className -> {
            try {
              return Class.forName(className);
            } catch (ClassNotFoundException e) {
              throw new CheesecakesException(
                  String.format("Error converting String '%s' to Class.", className), e);
            }
          }));

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
   * Returns the underlying parameter converter.
   *
   * @return The parameter converter.
   */
  public Parameter.RegistrableConverter getConverter() {
    return converter;
  }
}
