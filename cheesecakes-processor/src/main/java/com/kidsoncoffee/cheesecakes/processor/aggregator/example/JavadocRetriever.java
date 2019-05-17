package com.kidsoncoffee.cheesecakes.processor.aggregator.example;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class JavadocRetriever {

  private final Elements elementUtils;

  @Inject
  public JavadocRetriever(Elements elementUtils) {
    this.elementUtils = elementUtils;
  }

  public List<String> retrieve(final Element scenarioMethod) {
    final String doc = this.elementUtils.getDocComment(scenarioMethod);

    if (doc == null) {
      return Collections.emptyList();
    }

    final AtomicBoolean whereFound = new AtomicBoolean(false);
    return stream(doc.split(System.lineSeparator()))
        .filter(
            docLine -> {
              if (docLine.trim().equalsIgnoreCase("Examples:")) {
                return whereFound.getAndSet(true);
              }
              return whereFound.get();
            })
        .filter(StringUtils::isNotBlank)
        .filter(docLine -> !docLine.contains("<pre>"))
        .filter(docLine -> !docLine.contains("</pre>"))
        .filter(docLine -> !docLine.trim().startsWith("@param"))
        .filter(docLine -> !docLine.trim().startsWith("@return"))
        .filter(docLine -> !docLine.trim().startsWith("@deprecated"))
        .filter(docLine -> !docLine.trim().startsWith("@exception"))
        .filter(docLine -> !docLine.trim().startsWith("@throws"))
        .filter(docLine -> !docLine.trim().startsWith("@see"))
        .filter(docLine -> !docLine.trim().startsWith("@since"))
        .collect(Collectors.toList());
  }
}
