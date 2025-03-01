package com.rubyproducti9n.unofficialmech;

import android.content.Context;

import androidx.annotation.NonNull;

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

    private static final String[] MODELS = {
            "gemini-2.0-flash-001",
            "gemini-1.5-flash",
            "gemini-1.5-flash-8b-001",};
    private static String selectedModel;
    private String prompt;
    private static String response;

    //Model last updated: 01 Mar 2025

    public Gemini(int modelIndex, String prompt, GeminiCallback callback) {
        this.prompt = prompt;
        this.selectedModel = chooseModel(modelIndex);
        // Call initiate with a callback to store the response
        initiate(modelIndex, prompt, new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                response = result; // Store response when available
                if (callback != null) {
                    callback.onSuccess(result); // Pass response to caller
                }
            }

            @Override
            public void onFailure(String error) {
                response = "Error: " + error; // Handle error response
                if (callback != null) {
                    callback.onFailure(error);
                }
            }
        });

    }

    // Method to choose a model based on an integer value
    private String chooseModel(int index) {
        if (index >= 0 && index < MODELS.length) {
            return MODELS[index];
        } else {
            return MODELS[0]; // Default to gemini-pro if index is invalid
        }
    }
//    private String chooseModel(int index) {
//        String[] models = {
//                "gemini-2.0-flash-001",
//                "gemini-1.5-flash",
//                "gemini-1.5-flash-8b-001",};
//        return models[Math.max(0, Math.min(index, models.length - 1))]; // Ensure valid index
//    }

    // Function to be triggered upon object creation
    private void initiate() {
        System.out.println("Initiating Gemini with model: " + selectedModel);
        System.out.println("Prompt: " + prompt);
        // TODO: Add API call logic here
    }

    // Getter for the selected model
    public String getSelectedModel() {
        return selectedModel;
    }
    // Getter for response
    public String getResponse() {
        return response;
    }
    //Text Generation Model
    public interface GeminiCallback {
        void onSuccess(String result);
        void onFailure(String error);
    }
    public static void initiate(int modelIndex, String prompt, GeminiCallback callback) {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "text/plain";

        GenerationConfig generationConfig = configBuilder.build();

        // Array of Gemini models
        String[] models = {
                "gemini-2.0-flash-001",
                "gemini-1.5-flash",
                "gemini-1.5-flash-8b-001",};
        String selectedModel = models[Math.max(0, Math.min(modelIndex, models.length - 1))]; // Ensure valid index

        GenerativeModel gm = new GenerativeModel(
                selectedModel,
                BuildConfig.apiKey,
                generationConfig
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (callback != null) {
                    callback.onSuccess(resultText); // Pass result to callback
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
                if (callback != null) {
                    callback.onFailure(t.getMessage());
                }
            }
        }, executor);
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
    public static void processPrompt(int model, String prompt) {
        if (containsDisallowedKeywords(prompt)) {
            // In an Android app, you might use Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            // For this example, we'll simply print the message to the console.
            System.out.println("Input contains disallowed content. Please modify your prompt.");
        } else {
            //initiate(model, prompt);
            System.out.println("Prompt is acceptable. Proceeding to send it to the Gemini model...");
            // Call your Gemini model processing code here.
        }
    }
}
