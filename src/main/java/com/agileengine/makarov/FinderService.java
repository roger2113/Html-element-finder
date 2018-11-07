package com.agileengine.makarov;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class FinderService {

    private final static String SPLIT_REGEX = "#|-|;|:|\\s+|\\.|\\(|\\)|\\{|\\}|\\[|\\]";

    /**
     * Entry point to finder service
     * each given element is compared with target element data and given its own score,
     * returns element with max score
     *
     * @param targetElement
     * @param elements
     */
    public Optional<Element> find(Element targetElement, Elements elements) {
        final Map<String, String> originalData = extractTargetElementData(targetElement);

        Optional<Candidate> candidate = elements.parallelStream()
                .map(element -> compare(originalData, element))
                .max(Comparator.comparingInt(Candidate::getScore));
        if (candidate.isPresent()) {
            Candidate winner = candidate.get();
            log.info("Highest score {} for element {}", winner.getScore(), winner.getElement());
            return Optional.of(winner.getElement());
        }
        return Optional.empty();
    }

    /**
     * Method to represent target element attributes as stringed map
     * element text value added as additional point for scoring
     *
     * @param element - target element
     */
    private Map<String, String> extractTargetElementData(Element element) {
        Map<String, String> attributes = element.attributes().asList().stream()
                .collect(toMap(Attribute::getKey, Attribute::getValue));
        attributes.put("text", element.text());
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Method to compare target element(TE) attributes with compared element(CE) attributes
     * if same attribute found in CE, TE attribute value split into words
     * and increments CE score for each word containing in CE attribute
     *
     * @param targetAttributes - target element attributes as stringed map
     * @param comparedElement
     */
    private Candidate compare(final Map<String, String> targetAttributes, Element comparedElement) {
        final Candidate candidate = new Candidate(comparedElement);

        targetAttributes.forEach((attributeKey, attributeValue) -> {
            String diffAttributeValue = comparedElement.attr(attributeKey);
            if (!diffAttributeValue.equals("")) {
                String[] originalAttributeContent = split(attributeValue);
                int score = getScore(originalAttributeContent, diffAttributeValue);
                candidate.incrementScore(score);
            }
        });
        return candidate;
    }

    /**
     * @param targetContent - word array from target element current attribute
     * @param diffContent - compared element current attribute value
     * @return
     */
    private int getScore(String[] targetContent, String diffContent) {
        int score = 0;
        for (String targetContentWord : targetContent) {
            if (diffContent.toLowerCase().contains(targetContentWord.toLowerCase()))
                score++;
        }
        return score;
    }

    private String[] split(String str) {
        return Arrays.stream(str.split(SPLIT_REGEX))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    private class Candidate {
        private final Element element;
        private Integer score;

        private Candidate(Element element) {
            this.element = element;
            this.score = 0;
        }

        private Element getElement() {
            return element;
        }

        private int getScore() {
            return score;
        }

        private void incrementScore(int score) {
            this.score += score;
        }
    }

}
