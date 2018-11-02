package com.agileengine.makarov;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ApplicationMain {

    private static final Logger log = Logger.getLogger(ApplicationMain.class.getName());
    private static final String BUTTON_ID = "make-everything-ok-button";


    public static void main(String[] args) throws IOException, InvalidArgumentsException {
        if (args.length < 2 || args.length > 3) {
            throw new InvalidArgumentsException("Error - invalid arguments quantity");
        }

        Map<String, String> originalAttributes = extractOriginalButtonAttributes(args[0]);
        Document diffPage = getJSoupDocument(args[1]);

        //Task example shows us, that all searched elements are in <a> tag
        Elements references = diffPage.getElementsByTag("a");

        //And Have "btn" class
        List<Element> buttons = references.stream()
                .filter(el -> el.attr("class").startsWith("btn"))
                .collect(toList());

        //we filtered elements by all exactly defined params
        //and now we need to find maximum data match with original element
        //let's create checking chain and Element with maximum score will be the answer
        MatchingEngine matchingEngine = new MatchingEngine();
        Optional<Element> winner = matchingEngine.check(originalAttributes, buttons);
        if (winner.isPresent()) {
            List<String> path = winner.get().parents().stream().map(Element::nodeName).collect(toList());
            Collections.reverse(path);
            log.info("Answer :" + String.join(">", path));
        } else {
            log.info("No elements matching found");
        }

    }

    private static Map<String, String> extractOriginalButtonAttributes(String originalFilePath) throws IOException {
        Document document = getJSoupDocument(originalFilePath);

        Element element = document.getElementById(BUTTON_ID);
        Map<String, String> attributes = element.attributes().asList().stream()
                .collect(toMap(Attribute::getKey, Attribute::getValue));

        attributes.put("text", element.text());
        return attributes;
    }

    private static Document getJSoupDocument(String filePath) throws IOException {
        return Jsoup.parse(new File(filePath), "UTF-8");
    }
}
