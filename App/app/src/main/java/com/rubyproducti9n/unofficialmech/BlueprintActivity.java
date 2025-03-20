package com.rubyproducti9n.unofficialmech;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class BlueprintActivity extends BaseActivity {

    com.google.ai.client.generativeai.type.Content content;
    List<ItemBlueprint> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blueprint);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        articles.add(new ItemBlueprint("This is Headline", "Summary", "(Link goes here)"));

        initializeGemini(articles);
        BlueprintAdapter adapter = new BlueprintAdapter(articles, this);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(BlueprintActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    public void initializeGemini(List<ItemBlueprint> item){
        String defaultPrompt = "Generate a list of at least 25 news articles about recent trends in the mechanical industry and other important news globally. Each article should be listed as a numbered point and should include:\n" +
                "\n" +
                "The title of the article.\n" +
                "A concise summary (at least 2-3 sentences) highlighting the key information from the article.\n" +
                "The URL link to the article.\n" +
                "Ensure that every article is provided with its complete details, without any skipped or incomplete entries. The format should be as follows:\n" +
                "\n" +
                "Title of the first article\n" +
                "\n" +
                "Summary of the first article in 2-3 sentences explaining the key points and takeaways from the article.\n" +
                "https://www.example.com/article-1\n" +
                "Title of the second article\n" +
                "\n" +
                "Summary of the second article in 2-3 sentences explaining the key points and takeaways from the article.\n" +
                "https://www.example.com/article-2\n" +
                "Title of the third article\n" +
                "\n" +
                "Summary of the third article in 2-3 sentences explaining the key points and takeaways from the article.\n" +
                "https://www.example.com/article-3\n" +
                "// Continue this format for all remaining articles until the total count reaches 25.";
//        progressIndicator.setIndeterminate(true);


        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        configBuilder.maxOutputTokens = 1000;
        configBuilder.stopSequences = Collections.singletonList("red");


//        String apiKey = BuildConfig.MY_API_KEY;

        GenerationConfig generationConfig = configBuilder.build();
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDwswACZZRz2015E4yTNmcDn-8GlXyBovk");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        content = new com.google.ai.client.generativeai.type.Content.Builder()
                .addText(defaultPrompt)
                .build();

        Executor executor = Executors.newFixedThreadPool(15);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(com.google.ai.client.generativeai.type.GenerateContentResponse result) {
                String resultText = result.getText();
                System.out.println(resultText);
                articles = parseText(resultText);
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                BlueprintAdapter adapter = new BlueprintAdapter(articles, BlueprintActivity.this);
                GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(BlueprintActivity.this, 1);
                runOnUiThread(() -> recyclerView.setLayoutManager(layoutManager));
                runOnUiThread(() -> recyclerView.setAdapter(adapter));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Looper.prepare();
                Log.d("===Reason===", "R:- " + t);
            }
        }, executor);

    }

    public static List<ItemBlueprint> parseNewsArticles(String jsonText) throws JSONException {
        List<ItemBlueprint> articles = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonText);
        JSONArray articlesArray = jsonObject.getJSONArray("articles");

        for (int i = 0; i < articlesArray.length(); i++) {
            JSONObject articleObject = articlesArray.getJSONObject(i);
            String title = articleObject.getString("title");

            String summary = articleObject.getString("summary");
            String link = articleObject.getString("link");

            ItemBlueprint article = new ItemBlueprint(title, summary, link);
            articles.add(article);
        }

        return articles;
    }

    private void loadNewsArticles() {
        
//        NewsApiClient newsApiClient = new NewsApiClient("YOUR_API_KEY");
//
//// /v2/everything
//        newsApiClient.getEverything(
//                new EverythingRequest.Builder()
//                        .q("trump")
//                        .build(),
//                new NewsApiClient.ArticlesResponseCallback() {
//                    @Override
//                    public void onSuccess(ArticleResponse response) {
//                        System.out.println(response.getArticles().get(0).getTitle());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        System.out.println(throwable.getMessage());
//                    }
//                }
//        );

    }

    public static List<ItemBlueprint> parseText(String text) {
        List<ItemBlueprint> articles = new ArrayList<>();
        String[] lines = text.split("\n");

        for (String line : lines) {
            String[] parts = line.split(": ");
            if (parts.length == 2) {
                String title = parts[0];
                String summaryAndLink = parts[1];

                String[] summaryLinkParts = summaryAndLink.split(" ");
                String summary = summaryLinkParts[0];
                String link = summaryLinkParts[1];

                ItemBlueprint article = new ItemBlueprint(title, summary, link);
                articles.add(article);
            }
        }

        return articles;
    }
}