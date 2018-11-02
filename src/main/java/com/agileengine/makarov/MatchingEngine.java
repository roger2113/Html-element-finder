package com.agileengine.makarov;

import org.jsoup.nodes.Element;

import java.util.*;
import java.util.logging.Logger;

public class MatchingEngine {

    private static final Logger log = Logger.getLogger(MatchingEngine.class.getName());


    private static final String ATTRIBUTE_KEY_TITLE = "title";
    private static final String ATTRIBUTE_KEY_TEXT = "text";
    private static final String ATTRIBUTE_KEY_HREF = "href";

    public Optional<Element> check(Map<String, String> originalData, List<Element> elements) {
        Optional<Candidate> winner = elements.stream()
                .map(element -> {
                    Candidate candidate = new Candidate(element);
                    matchTitle(originalData.get(ATTRIBUTE_KEY_TITLE), candidate);
                    matchText(originalData.get(ATTRIBUTE_KEY_TEXT), candidate);
                    matchHref(originalData.get(ATTRIBUTE_KEY_HREF), candidate);
                    return candidate;
                })
                .max(Comparator.comparing(Candidate::getScore));
        return winner.isPresent() ? Optional.of(winner.get().getElement()) : Optional.empty();
    }

    private static void matchTitle(String originalValue, Candidate candidate) {
        String candidateTitleValue = candidate.getElement().attr(ATTRIBUTE_KEY_TITLE);
        if(!candidateTitleValue.equals("") && candidateTitleValue.contains(originalValue)) {
            candidate.incrementScore();
        }
    }

    private static void matchText(String originalValue, Candidate candidate) {
        String[] originalWords = originalValue.split(" ");
        String[] candidateWords = candidate.getElement().attr(ATTRIBUTE_KEY_TEXT).split(" ");
        boolean coincided = Arrays.stream(candidateWords)
                .anyMatch(candidateWord -> Arrays.asList(originalWords).contains(candidateWord));
        if(coincided) candidate.incrementScore();

    }

    private static void matchHref(String originalValue, Candidate candidate) {
        String originalHref = originalValue.replace("#" , "");
        String candidateHrefValue = candidate.getElement().attr(ATTRIBUTE_KEY_HREF);
        if(candidateHrefValue.contains(originalHref)) candidate.incrementScore();
    }

    private class Candidate {
        private Element element;
        private int score;

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

        private void incrementScore() {
            this.score++;
        }
    }
}

