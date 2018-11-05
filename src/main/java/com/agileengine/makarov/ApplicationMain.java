package com.agileengine.makarov;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ApplicationMain {

    private static final String TARGET_ELEMENT_ID = "make-everything-ok-button";

    public static void main(String[] args) throws IOException, InvalidArgumentsException {
        validateArgs(args);

        String originalFilePath = args[0];
        String diffFilePath = args[1];
        String targetElementId = args.length == 3 ? args[2] : TARGET_ELEMENT_ID;
        log.info("Searching element by id '{}'", targetElementId);

        Element targetElement = extractElement(originalFilePath, targetElementId);
        log.info("Original element found:{}", targetElement);

        Elements elements = extractAllElements(diffFilePath);
        Optional<Element> result = new FinderService().find(targetElement, elements);

        if (result.isPresent()) {
            List<String> path = result.get().parents().stream().map(Element::nodeName).collect(toList());
            Collections.reverse(path);
            log.info("Element path :" + String.join(">", path));
        } else {
            log.info("No elements matching found");
        }
    }

    private static Element extractElement(String filePath, String targetElementId) throws IOException, InvalidArgumentsException {
        Document document = getJSoupDocument(filePath);
        Element element = document.getElementById(targetElementId);
        if(element == null) {
            throw new InvalidArgumentsException(String.format("Target element with id '%s' not found", targetElementId));
        }
        return element;

    }

    private static Elements extractAllElements(String filePath) throws IOException, InvalidArgumentsException {
        Document document = getJSoupDocument(filePath);
        Elements elements = document.getAllElements();
        if(elements.isEmpty()) {
            throw new InvalidArgumentsException("Diff file does not contain any HTML element");
        }
        log.info("{} elements found in diff file", elements.size());
        return elements;

    }

    private static Document getJSoupDocument(String filePath) throws IOException {
        return Jsoup.parse(new File(filePath), "UTF-8");
    }

    private static void validateArgs(String[] args) throws InvalidArgumentsException {
        if (args.length < 2 || args.length > 3) {
            throw new InvalidArgumentsException("Invalid arguments quantity");
        }

        if(args[0].length() < 2 || args[1].length() < 2) {
            throw new InvalidArgumentsException("Invalid file path(s)");
        }
    }

}
