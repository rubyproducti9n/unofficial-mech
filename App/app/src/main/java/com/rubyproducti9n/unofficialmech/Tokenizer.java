package com.rubyproducti9n.unofficialmech;

import android.content.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private static final String VOCAB_FILE = "tokenizer.txt"; // Update with actual tokenizer file
    private List<String> vocabList;

    public Tokenizer(Context context) throws IOException {
        vocabList = loadVocabulary(context);
    }

    private List<String> loadVocabulary(Context context) throws IOException {
        List<String> vocab = new ArrayList<>();
        try (java.io.InputStream is = context.getAssets().open(VOCAB_FILE);
             java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                vocab.add(line);
            }
        }
        return vocab;
    }

    public int[] encode(String text) {
        List<Integer> tokenIds = new ArrayList<>();
        for (String word : text.split(" ")) {
            int index = vocabList.indexOf(word);
            tokenIds.add(index >= 0 ? index : 0); // Use 0 for unknown tokens
        }
        return tokenIds.stream().mapToInt(i -> i).toArray();
    }

    public String decode(float[] output) {
        StringBuilder result = new StringBuilder();
        for (float token : output) {
            int index = (int) token;
            if (index > 0 && index < vocabList.size()) {
                result.append(vocabList.get(index)).append(" ");
            }
        }
        return result.toString().trim();
    }
}
