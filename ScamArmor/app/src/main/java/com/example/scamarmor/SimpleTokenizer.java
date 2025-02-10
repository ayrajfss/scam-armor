package com.example.scamarmor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTokenizer {

    private Map<String, Integer> wordIndex;
    private int numWords;
    private String oovToken;

    public SimpleTokenizer(int numWords, String oovToken) {
        this.numWords = numWords;
        this.oovToken = oovToken;
        wordIndex = new HashMap<>();
    }

    public void fitOnTexts(List<String> texts) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String text : texts) {
            String[] words = text.split("\\s+");
            for (String word : words) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCounts.entrySet());
        sortedWords.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int index = 1;
        for (Map.Entry<String, Integer> entry : sortedWords) {
            if (index >= numWords) break;
            wordIndex.put(entry.getKey(), index);
            index++;
        }

        wordIndex.put(oovToken, numWords - 1);
    }

    public int[] textsToSequences(String text) {
        String[] words = text.split("\\s+");
        int[] sequences = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            sequences[i] = wordIndex.getOrDefault(words[i], wordIndex.get(oovToken));
        }
        return sequences;
    }

    public int[] padSequences(int[] sequences, int maxLength) {
        int[] padded = new int[maxLength];
        int len = Math.min(sequences.length, maxLength);
        System.arraycopy(sequences, 0, padded, 0, len);
        return padded;
    }
}
