package com.example.scamarmor;

import java.util.HashMap;
import java.util.Map;

public class Lemmatizer {

    private static final Map<String, String> lemmaMap;

    static {
        // Example mapping for English lemmatization
        lemmaMap = new HashMap<>();
        lemmaMap.put("running", "run");
        lemmaMap.put("ran", "run");
        // Add more mappings as needed
    }

    public static String lemmatize(String word) {
        // Return lemma if exists, otherwise return original word
        return lemmaMap.getOrDefault(word, word);
    }
}
