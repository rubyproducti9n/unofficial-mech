package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavigationAIActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private TextView commandResultTextView;
    private SpeechRecognizer speechRecognizer;
    TextInputEditText promptEditTxt;
    MaterialButton btn, micBtn;
    private ArrayList<Interaction> interactionHistory;
    private Map<String, String> activityNavigationMap;
    private Map<String, Class<?>> activityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_aiactivity);

        ImageView bgImg1 = findViewById(R.id.bgImg1);
        ImageView bgImg2 = findViewById(R.id.bgImg2);
        animateVertically(this, bgImg1, 0, 2000);
        animateVertically(this, bgImg2, 0, 9000);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        commandResultTextView = findViewById(R.id.commandResultTextView);
        interactionHistory = new ArrayList<>();

        promptEditTxt = findViewById(R.id.prompt);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commandResultTextView.setText("");
                onSubmitClick();
            }
        });

        micBtn = findViewById(R.id.mic_btn);
        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
                } else {
                    Toast.makeText(NavigationAIActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
                    initializeSpeechRecognizer();
                }
            }
        });

        activityMap = new HashMap<>();
        activityNavigationMap = new HashMap<>();
        activityNavigationMap.put("settings", "Home > Tap on Menu (top left corner) > Settings");
        activityNavigationMap.put("profile", "Home > Tap on Profile icon");

        initializeActivityMap();
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (speechRecognizer != null) {
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String command = matches.get(0);
                        processCommand(command);
                        commandResultTextView.setText("Command: " + command);
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }

                // Implement other methods as needed
            });
        }
    }

    public void onSubmitClick() {
        String userCommand = promptEditTxt.getText().toString();
        if (!userCommand.isEmpty()) {
            interactionHistory.clear();
            String output = processCommand(userCommand);
            addToHistory(new Interaction(userCommand, output));
            updateConversationUI();
            displayStepByStepInstructions(output);
            promptEditTxt.getText().clear();
        }
    }

    private void updateConversationUI() {
        StringBuilder conversationBuilder = new StringBuilder();
        for (Interaction interaction : interactionHistory) {
            conversationBuilder.append("User: ").append(interaction.getUserInput()).append("\n");
            conversationBuilder.append("Bot: ").append(interaction.getResponse()).append("\n");
        }
        commandResultTextView.setText(conversationBuilder.toString());
    }

    private String processCommand(String command) {
        if (containsHelpRequest(command)) {
            return provideStepByStepInstructions(command);
        } else if (command.toLowerCase().contains("open")) {
            return processOpenCommand(command);
        } else if (command.toLowerCase().contains("navigate")) {
            return processNavigateCommand(command);
        } else if (command.toLowerCase().contains("search")) {
            return processSearchCommand(command);
        } else if (command.toLowerCase().contains("help")) {
            return processHelpCommand(command);
        } else {
            return "Unrecognized command";
        }
    }

    private String processOpenCommand(String command) {
        // Extract the target activity from the user command
        String targetActivity = extractActivityName(command);

        // Check if the activity exists in the map
        if (activityMap.containsKey(targetActivity)) {
            // Redirect to the specified activity
            navigateToActivity(targetActivity);
            return "Opening " + targetActivity + "...";
        } else {
            // Try to find the closest matching activity
            String closestMatch = findClosestMatchingActivity(targetActivity);

            if (closestMatch != null) {
                // Redirect to the closest matching activity
                navigateToActivity(closestMatch);
                return "Opening " + closestMatch + "...";
            } else {
                return "Sorry, I don't have specific steps for opening " + targetActivity;
            }
        }
    }

    private String processNavigateCommand(String command) {
        handleRedirection(command);
        return "Please wait...";
    }

    private String processSearchCommand(String command) {
        return provideStepByStepInstructions(command);
    }

    private String processHelpCommand(String command) {
        String targetItem = extractTargetItem(command);
        return "Here are some steps to help with " + targetItem + ": [Step 1], [Step 2], ...";
    }

    private String extractTargetItem(String userCommand) {
        return userCommand.toLowerCase().replace("search", "").replace("help", "").trim();
    }

    private boolean containsHelpRequest(String userCommand) {
        return userCommand.toLowerCase().contains("help")
                || userCommand.toLowerCase().contains("unable to find")
                || userCommand.toLowerCase().contains("find");
    }


    private void navigateToActivity(String targetActivity) {
        Class<?> activityClass = getActivityClass(targetActivity);
        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }
    }

    private String findClosestMatchingActivity(String targetActivity) {
        for (String activity : activityMap.keySet()) {
            if (activity.toLowerCase().contains(targetActivity.toLowerCase())) {
                return activity;
            }
        }
        return null;
    }

    private void initializeActivityMap() {
        activityMap.put("about", AboutActivity.class);
        activityMap.put("profile", AccountInfo.class);
        activityMap.put("ai tools", AIToolsActivity.class);
        activityMap.put("album", AlbumActivity.class);
        activityMap.put("appstore", AppStoreActivity.class);
        activityMap.put("Artificial Intelligence", ArtificialIntelligenceActivity.class);
        activityMap.put("attendancerecorder", AttendanceRecorder.class);
        activityMap.put("attendancesheet", AttendanceSheetActivity.class);
        activityMap.put("attendancetracker", AttendenceTrackerActivity.class);
        activityMap.put("beta", BetaActivity.class);
        activityMap.put("createinternshipnotification", CreateInternshipNotification.class);
        activityMap.put("createnotice", CreateNotice.class);
        activityMap.put("createpost", CreatePost.class);
        activityMap.put("cretaeproject", CreateProject.class);
        activityMap.put("home", MainActivity.class);
        activityMap.put("password", PasswordActivity.class);
        activityMap.put("plugins", PluginsActivity.class);
        activityMap.put("privacypolicy", PrivacyPolicyActivity.class);
        activityMap.put("project", ProjectActivity.class);
        activityMap.put("settings", SettingsActivity.class);
    }

    private Class<?> getActivityClass(String targetActivity) {
        Map<String, Class<?>> activityClassMap = new HashMap<>();
        activityClassMap.put("about", AboutActivity.class);
        activityClassMap.put("profile", AccountInfo.class);
        activityClassMap.put("ai tools", AIToolsActivity.class);
        activityClassMap.put("album", AlbumActivity.class);
        activityClassMap.put("appstore", AppStoreActivity.class);
        activityClassMap.put("Artificial Intelligence", ArtificialIntelligenceActivity.class);
        activityClassMap.put("attendancerecorder", AttendanceRecorder.class);
        activityClassMap.put("attendancesheet", AttendanceSheetActivity.class);
        activityClassMap.put("attendancetracker", AttendenceTrackerActivity.class);
        activityClassMap.put("beta", BetaActivity.class);
        activityClassMap.put("createinternshipnotification", CreateInternshipNotification.class);
        activityClassMap.put("createnotice", CreateNotice.class);
        activityClassMap.put("createpost", CreatePost.class);
        activityClassMap.put("cretaeproject", CreateProject.class);
        activityClassMap.put("home", MainActivity.class);
        activityClassMap.put("password", PasswordActivity.class);
        activityClassMap.put("plugins", PluginsActivity.class);
        activityClassMap.put("privacypolicy", PrivacyPolicyActivity.class);
        activityClassMap.put("project", ProjectActivity.class);
        activityClassMap.put("settings", SettingsActivity.class);
        return activityClassMap.get(targetActivity);
    }

    private String provideStepByStepInstructions(String userCommand) {
        String activityName = extractActivityName(userCommand);
        if (activityNavigationMap.containsKey(activityName)) {
            return "Steps: " + activityNavigationMap.get(activityName);
        } else {
            return "Sorry, I don't have specific steps for navigating to " + activityName;
        }
    }

    private void handleRedirection(String userCommand) {
        String targetActivity = extractActivityName(userCommand);
        if (activityMap.containsKey(targetActivity)) {
            navigateToActivity(targetActivity);
        }
    }

    private String extractActivityName(String userCommand) {
        String[] activityKeywords = {
                "about", "profile", "ai tools", "album", "appstore",
                "artificial intelligence", "attendance recorder", "attendancesheet",
                "attendance tracker", "beta", "create internship notification",
                "create notice", "create post", "create project", "home",
                "password", "plugins", "privacy policy", "project", "settings"
        };
        for (String keyword : activityKeywords) {
            if (userCommand.toLowerCase().contains(keyword)) {
                return keyword;
            }
        }
        return "";
    }

    private void displayStepByStepInstructions(String instructions) {
        commandResultTextView.setText(instructions);
    }

    // ... (remaining methods)

    private void addToHistory(Interaction interaction) {
        interactionHistory.add(interaction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private static class Interaction {
        private final String userInput;
        private final String response;

        public Interaction(String userInput, String response) {
            this.userInput = userInput;
            this.response = response;
        }

        public String getUserInput() {
            return userInput;
        }

        public String getResponse() {
            return response;
        }
    }
}
