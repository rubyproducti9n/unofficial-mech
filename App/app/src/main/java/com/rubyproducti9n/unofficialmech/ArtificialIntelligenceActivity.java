package com.rubyproducti9n.unofficialmech;

import static android.app.Activity.RESULT_OK;
import static android.view.View.VISIBLE;
import static com.rubyproducti9n.unofficialmech.Callbacks.getAdValue;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.context;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.loadBannerAd;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.LottieAnimationView;
import com.google.ai.client.generativeai.GenerativeModel;
//import com.google.ai.client.generativeai.internal.api.GenerateContentRequest;
//import com.google.ai.client.generativeai.internal.api.GenerateContentResponse;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class ArtificialIntelligenceActivity extends BottomSheetProfileEdit {

    GemmaModel gemmaModel;

    //OpenAI (Removed)
    private static final String API_KEY = System.getenv("OPENAI_API_KEY"); // Replace with your API Key
    private static final String GPT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DALL_E_URL = "https://api.openai.com/v1/images/generations";
    private static final MediaType JSON =MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    View view;

    TextView txt;

    String outputTxt;
    LinearProgressIndicator progressIndicator;
    private ActivityResultLauncher<Intent> startActivityForResult;
    TextInputEditText promptEditTxt;
    MaterialButton btn, model_info;
    Uri rawSelectedImageUri;
    com.google.ai.client.generativeai.type.Content content;
    Bitmap bitmap;
    private static InterstitialAd mInterstitialAd;

    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.activity_artificial_intelligence, container, false);

        // Delay execution to ensure the view is attached
        view.post(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if (dialog != null) {
                FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                    behavior.setHideable(true);
                    behavior.setFitToContents(true);
                }
            }

            // Fix scrolling issue
            ScrollView scrollView = view.findViewById(R.id.scroll_view);
            if (scrollView != null) {
                scrollView.setOnTouchListener((v, event) -> {
                    v.getParent().requestDisallowInterceptTouchEvent(false); // Prevent BottomSheet from intercepting
                    return false;
                });
            }
        });
        //new Gemini(requireContext(), "Heyy!!!");

        //initializeGeminiUpdated("Heyy");


        Chip chip = view.findViewById(R.id.latest);
        SharedPreferences modelPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int model = modelPref.getInt("selected_model", 0);
        if (model == 0){
            chip.setVisibility(VISIBLE);
        }else{
            chip.setVisibility(View.GONE);
        }

        //TODO: Google Gemma Model in Test (Alpha-Beta-01)
//        try {
//            gemmaModel = new GemmaModel(requireContext());
//
//            // Example question
//            String question = "What is the capital of India?";
//
//            // Get model response
//            String answer = gemmaModel.runInference(question);
//
//            // Log and display answer
//            Log.d("Gemma Output", answer);
//            new MaterialAlertDialogBuilder(requireContext())
//                    .setMessage(answer)
//                    .show();
//            Toast.makeText(requireContext(), answer, Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }

//        String response = generateTextResponse("Hello, How are you?");
//        Log.d("OpenAI - ChatGPT", response);
//        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();

        ImageView bgImg = view.findViewById(R.id.blurBg);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        Window window = requireActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.DynamicBlack));

        LottieAnimationView l = view.findViewById(R.id.lottie);
        l.setAnimation(R.raw.anim_gemini_nm);
        l.loop(true);
        l.playAnimation();


        MaterialCardView ad_container = view.findViewById(R.id.ad_container);
        getAdValue(ad_container, new Callbacks.AdValue() {
            @Override
            public void onAdValue(boolean value) {
                if (value){
                    AdView adView = view.findViewById(R.id.adView);
                    MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                            if (initializationStatus != null){
                                Log.d("Admob: ", "Ad received!");
                                loadBannerAd(adView);
                                AdRequest adRequest = new AdRequest.Builder().build();
                                adView.loadAd(adRequest);
                            }else{
                                Log.d("Admob: ", "Error receiving ads!");
                            }
                        }
                    });
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);

                    InterstitialAd.load(requireContext(), "ca-app-pub-5180621516690353/8609265521", new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.d("Admob says: ", "Ad not loaded");
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            mInterstitialAd = interstitialAd;
                        }
                    });

                }else{
                    ad_container.setVisibility(View.GONE);
                }
            }
        });

        progressIndicator = view.findViewById(R.id.progress_bar);
        ImageView img = view.findViewById(R.id.img);
        txt = view.findViewById(R.id.textView);
        btn = view.findViewById(R.id.btn);
        promptEditTxt = view.findViewById(R.id.prompt);
        model_info = view.findViewById(R.id.model_info);
        MaterialButton img_selector = view.findViewById(R.id.img_btn);

        img_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        model_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://deepmind.google/technologies/gemini/#introduction"));
                startActivity(intent);
            }
        });

        progressIndicator.setIndeterminate(false);
        progressIndicator.setProgress(0);
        txt.setTextSize(20);
        txt.setTextColor(getResources().getColor(R.color.DynamicWhite));


//        if (getArguments() !=null){
//            String receivedCmd = getArguments().getString("cmd");
//            initializeGemini(receivedCmd, txt);
//            //Toast.makeText(requireContext(), receivedCmd, Toast.LENGTH_SHORT).show();
//        }

        promptEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Objects.requireNonNull(promptEditTxt.getText()).toString().isEmpty()){
                    btn.setEnabled(false);
                    ProjectToolkit.setButtonDisabledAnim(btn);
                }else{
                    if (verifyLimit()==0){
                        btn.setEnabled(true);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setLimit();
                            }
                        });
                    }else{
                        btn.setEnabled(true);
                        ProjectToolkit.setButtonEnabledAnim(btn);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mInterstitialAd!=null){
                                    mInterstitialAd.show(requireActivity());
                                }else{
                                    Toast.makeText(requireContext(), "Processing...", Toast.LENGTH_SHORT).show();
                                }

                                progressIndicator.setVisibility(VISIBLE);
                                ProjectToolkit.pulseAmin(1, txt);
                                txt.setText(" ");
                                txt.setAlpha(0.5f);
                                setLimit();
                                initiateModel(promptEditTxt.getText().toString(), txt);
                                promptEditTxt.setText("");
                                //IntelligentProcessingHub.googleBarcodeScanner(requireContext());
                            }
                        });
                    }

                }
            }
        });

        if (verifyLogin()){
            String[] urls = {"https://www.livemint.com/lm-img/img/2024/02/23/1600x900/GEMINI-AI-6_1708690357523_1708690365165.jpg",
                    "https://www.androidauthority.com/wp-content/uploads/2023/12/Gemini-Google-Logo.jpg",
            };
            Random random = new Random();
            int randomIndex = random.nextInt(urls.length);
            String randomUrl = urls[randomIndex];
            Picasso.get().load(randomUrl).into(img);
        }else{
            warningDialog(verifyLimit());
            Picasso.get().load("https://i0.wp.com/www.gyaaninfinity.com/wp-content/uploads/2023/12/gogle-gemini-ai.webp").into(img);
        }


//        IntelligentProcessingHub.textRecognization(this, Uri.parse("https://rubyproducti9n.github.io/mech/img/movify.png"), new IntelligentProcessingHub.OnTextRecognizedListener() {
//            @Override
//            public void OnTextRecognized(String text) {
//                if (text != null) {
//                    txt.setText(text);
//                }
//            }
//        });

//        SharedPreferences darkPref = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean isDakrModeEnabled = darkPref.getBoolean("DarkMode", false);
//        DeveloperActivity.applyTheme(isDakrModeEnabled);

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            rawSelectedImageUri = data.getData();
                            initializeGeminiVision(Objects.requireNonNull(promptEditTxt.getText()).toString(), rawSelectedImageUri, txt);
                            String selectedImageUri = rawSelectedImageUri.toString();
                        }else{
                            rawSelectedImageUri = null;
                            initializeGemini(Objects.requireNonNull(promptEditTxt.getText()).toString(), txt);
                        }
                    }
                }
        );




        return view;

    }

    private boolean verifyLogin(){
        String role = preferences.getString("auth_userole", null);
            if (role!=null){
                return true;
            }else{
                return false;
            }

    }

    private void warningDialog(int credits){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Warning!")
                .setCancelable(false)
                .setMessage("You have " + credits + " credit to use Gemini Pro for free. To enjoy unlimited Gemini Pro and Gemini Flash please create a account or login")
                .setNegativeButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (credits==0){
                            dialog.dismiss();
                            initiateLimitDialog();
                        }else {
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();
    }

    private void initiateLimitDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Exhausted!")
                .setCancelable(false)
                .setMessage("You have exhausted your one-time limit with no account logged-in")
                .setPositiveButton("Re-new", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(requireContext());
                        SharedPreferences.Editor e = p.edit();
                        e.putInt("limit", 3);
                        e.apply();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requireActivity().finish();
                    }
                });
        builder.show();
    }

    private int verifyLimit(){
        return preferences.getInt("limit", 3);
    }

    private void setLimit(){
        int limit = preferences.getInt("limit", 3);
        SharedPreferences.Editor editor = preferences.edit();

        if (limit > 0) {
            limit--;
        }else{
            initiateLimitDialog();
//            btn.setEnabled(false);
        }
        editor.putInt("limit", limit);
        editor.apply();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult.launch(intent);
    }

    private void initializeGeminiVision(String prompt, Uri imgUri, TextView txt){
        progressIndicator.setIndeterminate(true);
        txt.setTextSize(14);
        txt.setTextColor(getResources().getColor(R.color.matte_White));
        try {
        InputStream inputStream = requireActivity().getContentResolver().openInputStream(imgUri);
        bitmap = BitmapFactory.decodeStream(inputStream);

        if (inputStream!=null){
            inputStream.close();
        }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        configBuilder.maxOutputTokens = 1000;
        configBuilder.stopSequences = Collections.singletonList("red");

        GenerationConfig generationConfig = configBuilder.build();
        GenerativeModel gm = new GenerativeModel(String.valueOf(R.string.gem_vision), BuildConfig.apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        content = new com.google.ai.client.generativeai.type.Content.Builder()
                .addText(prompt)
                .addImage(bitmap)
                .addImage(bitmap)
                .build();

        Executor executor = Executors.newFixedThreadPool(15);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(com.google.ai.client.generativeai.type.GenerateContentResponse result) {
                String resultText = result.getText();
                requireActivity().runOnUiThread(() -> txt.setAlpha(1.0f));
                requireActivity().runOnUiThread(() -> txt.setText(resultText));
                requireActivity().runOnUiThread(() -> ProjectToolkit.fadeIn(txt));
                requireActivity().runOnUiThread(() -> ProjectToolkit.fadeOut(progressIndicator));
                requireActivity().runOnUiThread(() -> promptEditTxt.setText(""));
                requireActivity().runOnUiThread(() -> btn.setEnabled(false));
                requireActivity().runOnUiThread(() -> ProjectToolkit.pulseAmin(0, txt));
                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Looper.prepare();
                requireActivity().runOnUiThread(() -> txt.setText("Oops! something went wrong, please try again later."));
                Log.d("===Reason===", "R:- " + t);
                System.out.println("====Reason:- " + t);
            }
        }, executor);
    }

    public void initializeGemini(String prompt, TextView txt){
        progressIndicator.setIndeterminate(true);
        txt.setTextSize(14);
        txt.setTextColor(getResources().getColor(R.color.matte_White));

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        configBuilder.maxOutputTokens = 1000;
        configBuilder.stopSequences = Collections.singletonList("red");


//        String apiKey = BuildConfig.MY_API_KEY;

        GenerationConfig generationConfig = configBuilder.build();
        GenerativeModel gm = new GenerativeModel(String.valueOf(R.string.gem_text), BuildConfig.apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        content = new com.google.ai.client.generativeai.type.Content.Builder()
                .addText(prompt)
                .build();

        Executor executor = Executors.newFixedThreadPool(15);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(com.google.ai.client.generativeai.type.GenerateContentResponse result) {
                String resultText = result.getText();
                requireActivity().runOnUiThread(() -> txt.setAlpha(1.0f));
                requireActivity().runOnUiThread(() -> txt.setText(resultText));
                requireActivity().runOnUiThread(() -> ProjectToolkit.fadeIn(txt));
                requireActivity().runOnUiThread(() -> ProjectToolkit.fadeOut(progressIndicator));
                requireActivity().runOnUiThread(() -> promptEditTxt.setText(""));
                requireActivity().runOnUiThread(() -> btn.setEnabled(false));
                requireActivity().runOnUiThread(() -> ProjectToolkit.pulseAmin(0, txt));
                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Looper.prepare();
                requireActivity().runOnUiThread(() -> txt.setText("Oops! something went wrong, please try again later."));
                requireActivity().runOnUiThread(() -> ProjectToolkit.fadeOut(progressIndicator));
                Log.d("===Reason===", "R:- " + t);
            }
        }, executor);

    }

    public void initializeGeminiUpdated(String prompt) {
//        progressIndicator.setIndeterminate(true);
//        txt.setTextSize(14);
//        txt.setTextColor(getResources().getColor(R.color.matte_White));

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
//        configBuilder.responseMimeType = "application/json";  //For JSON formatted response
        configBuilder.responseMimeType = "text/plain";

        GenerationConfig generationConfig = configBuilder.build();

// Specify a Gemini model appropriate for your use case
        GenerativeModel gm =
                new GenerativeModel(
                        /* modelName */ "gemini-1.5-flash",
                        // Access your API key as a Build Configuration variable (see "Set up your API key"
                        // above)
                        /* apiKey */ BuildConfig.apiKey,
                        /* generationConfig */ generationConfig);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content =
                new Content.Builder()
                        .addText(prompt)
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
                        new Thread(() -> {
                            Looper.prepare();
                            new Handler(Looper.getMainLooper()).post(() -> {
                                txt.setText(resultText);
                            });
                            Looper.loop();
                        }).start();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                },
                executor);
    }



    private void initiateModel(String prompt, TextView txt){
        //Gemini res = new Gemini(0, prompt);
                Gemini.initiate(0, prompt, new Gemini.GeminiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                txt.setTextSize(14);
                                txt.setText(result); // Update UI with response
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                txt.setTextSize(12);
                                txt.setText("Error: " + error);
                            }
                        });
                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gemmaModel != null) {
//            gemmaModel.close();
        }
    }
}