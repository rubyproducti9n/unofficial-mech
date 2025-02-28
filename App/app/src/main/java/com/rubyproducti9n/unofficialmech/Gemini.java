package com.rubyproducti9n.unofficialmech;

import android.content.Context;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Gemini {

    public Gemini(Context context, String prompt) {
        processPrompt(context, prompt);
    }

    public static void initiate(Context context, String prompt) {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";

        GenerationConfig generationConfig = configBuilder.build();

// Specify a Gemini model appropriate for your use case
        GenerativeModel gm =
                new GenerativeModel(
                        /* modelName */ "gemini-1.5-flash",
                        // Access your API key as a Build Configuration variable (see "Set up your API key"
                        // above)
                        /* apiKey */ "AIzaSyDwswACZZRz2015E4yTNmcDn-8GlXyBovk",
                        /* generationConfig */ generationConfig);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content =
                new Content.Builder()
                        .addText(
                                "Heyy!!")
                        .build();

// For illustrative purposes only. You should use an executor that fits your needs.
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        System.out.println(resultText);
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Gemini")
                                .setMessage(resultText)
                                .show();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                },
                executor);
    }

    private static final List<String> DISALLOWED_KEYWORDS = Arrays.asList(
            "hate", "violence", "explicitContent", "illegal", "terrorism"
            // Add more keywords as needed...
    );

    // Check if the prompt contains any disallowed keywords.
    public static boolean containsDisallowedKeywords(String prompt) {
        String lowerPrompt = prompt.toLowerCase();
        for (String keyword : DISALLOWED_KEYWORDS) {
            if (lowerPrompt.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Process the prompt: show a toast (or a message) if disallowed keywords are found,
    // otherwise proceed to send the prompt to the Gemini model.
    public static void processPrompt(Context context, String prompt) {
        if (containsDisallowedKeywords(prompt)) {
            // In an Android app, you might use Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            // For this example, we'll simply print the message to the console.
            System.out.println("Input contains disallowed content. Please modify your prompt.");
        } else {
            initiate(context, prompt);
            System.out.println("Prompt is acceptable. Proceeding to send it to the Gemini model...");
            // Call your Gemini model processing code here.
        }
    }
}
