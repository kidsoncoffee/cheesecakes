package com.kidsoncoffee.cheesecakes.processor.domain;

import org.immutables.value.Value;

import java.util.Map;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
@Value.Style(builder = "example")
public interface  Example {
  Map<String, String> getValue();
}
