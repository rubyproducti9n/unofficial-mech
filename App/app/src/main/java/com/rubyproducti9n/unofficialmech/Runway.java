package com.rubyproducti9n.unofficialmech;

import android.util.Log;
import android.widget.ImageView;

import com.google.common.net.MediaType;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Runway {

    // Example endpoint – update as needed based on Runway ML documentation.
    private static final String API_ENDPOINT = "https://api.runwayml.com/v1/predictions";
    // Replace with your Runway ML API key if authentication is required.
    private static final String API_KEY = "key_7633b7900388c67542e443bcfd33e280eb3d025c902e66d7941914c2f26dd8cb3767e0f62e3bd5dac54ba36a7510ff1bf84aced8232d8644fc3d758592163ec9";
    MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");


    public Runway(String prompt, ImageView img){
        generateImage(prompt, img);
    }

    private void generateImage(String prompt, ImageView img) {
        OkHttpClient client = new OkHttpClient();

        // Build the JSON payload. Adjust keys/values based on the actual API spec.
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", prompt);
            jsonBody.put("model", "stable-diffusion"); // Example model; adjust per API documentation.
            // Add additional parameters as needed.
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody body = RequestBody.create(jsonBody.toString().getBytes(StandardCharsets.UTF_8));


        Request.Builder requestBuilder = new Request.Builder()
                .url(API_ENDPOINT)
                .post(body)
                .addHeader("Content-Type", "application/json");

        // Add the Authorization header if an API key is required.
        if (!API_KEY.equals("YOUR_API_KEY")) {
            requestBuilder.addHeader("Authorization", "Bearer " + API_KEY);
        }

        Request request = requestBuilder.build();

        // Execute the request asynchronously.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network errors or request failures.
                Log.e("ImageGen", "Request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Process the response – typically you may get a JSON containing the image URL or image data.
                    final String responseData = response.body().string();
                    Log.d("ImageGen", "Response: " + responseData);
                    Picasso.get().load(responseData).into(img);
                    // Remember to update your UI on the main thread if needed.
                } else {
                    Log.e("ImageGen", "Request not successful: " + response.code());
                }
            }
        });
    }


}
