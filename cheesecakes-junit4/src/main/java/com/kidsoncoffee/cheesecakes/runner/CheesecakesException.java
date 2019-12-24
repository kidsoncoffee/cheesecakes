package com.kidsoncoffee.cheesecakes.runner;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class CheesecakesException extends RuntimeException {
  public CheesecakesException(final String message, final Exception cause) {
    super(message, cause);
  }

  public CheesecakesException(final String message) {
    super(message);
  }
}
