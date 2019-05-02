package com.kidsoncoffee.cheesecakes.processor;

import com.kidsoncoffee.cheesecakes.processor.domain.Example;
import com.kidsoncoffee.cheesecakes.processor.domain.ImmutableExample;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class CommentParser {

  private final Elements elementUtils;

  public CommentParser(final Elements elementUtils) {
    this.elementUtils = elementUtils;
  }

  public List<Example> parse(final Element scenario) {
    final String doc = this.elementUtils.getDocComment(scenario);

    if (doc == null) {
      return Collections.emptyList();
    }

    final AtomicBoolean whereFound = new AtomicBoolean(false);
    final List<String> scenarioLines =
        stream(doc.split(System.lineSeparator()))
            .filter(
                docLine -> {
                  if (docLine.trim().equalsIgnoreCase("Examples:")) {
                    return whereFound.getAndSet(true);
                  }
                  return whereFound.get();
                })
            .filter(StringUtils::isNotBlank)
            .filter(docLine -> !docLine.trim().startsWith("---")) // TODO fchovich VALIDATE STATIC
            .filter(docLine -> !docLine.contains("<pre>"))
            .filter(docLine -> !docLine.contains("</pre>"))
            .collect(Collectors.toList());

    final List<String> header =
        stream(scenarioLines.get(0).split("\\|")).map(String::trim).collect(Collectors.toList());

    return scenarioLines.subList(1, scenarioLines.size()).stream()
        .map(s -> parseExample(s, header))
        .collect(Collectors.toList());
  }

  private Example parseExample(final String scenario, final List<String> header) {
    final String[] fields = scenario.split("\\|");
    final Map<String, String> values =
        IntStream.range(0, fields.length)
            .boxed()
            .collect(Collectors.toMap(header::get, i -> fields[i].trim()));
    return ImmutableExample.example().putAllValue(values).build();
  }
}
