package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getServerUrl;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WebViewActivity extends BaseActivity {
    static LinearProgressIndicator progress;
    static MaterialToolbar toolbar;
    static MaterialCardView webErr;
    static WebView webView;
    ExtendedFloatingActionButton fab1;
    static String s = getServerUrl();
    public static String link;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private boolean userConsentGiven = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the consent from user at the beginning (this could be shown via a dialog)
        getUserConsent();

        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        String activity = intent.getStringExtra("activity");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.matte_black));

        FloatingActionButton fab = findViewById(R.id.desktopView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDesktopView();
            }
        });
        fab1 = findViewById(R.id.externalLink);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });

        toolbar = findViewById(R.id.toolbar);
        progress = findViewById(R.id.linearProgress);
        webView = findViewById(R.id.webView);
        webErr = findViewById(R.id.webErr);
        webErr.setVisibility(View.GONE);

        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new MyWebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload(link);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (activity != null && activity.endsWith("pdf")) {
            openInDocs(link);
        }

        if (link != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.setSubtitle("loading...");
            if (link.equals(s)) {
                fab1.setVisibility(View.GONE);
                //new CheckServerOnlineTask().execute(s);
            } else {
                fab1.setVisibility(View.VISIBLE);
                webView.loadUrl(link);
                webErr.setVisibility(View.GONE);
            }
        } else {
            //getFile("https://firebasestorage.googleapis.com/v0/unofficial-mech.appspot.com/o/ADHAR.pdf?alt=media&token=e42cf2fa-c306-46af-aed3-d09e435c9cb2");
        }

        // Load and autofill user credentials if available
        loadUserCredentials();
    }

    // Method to get user consent to save credentials
    private void getUserConsent() {
        // This could be a dialog asking for consent to save credentials
        new MaterialAlertDialogBuilder(this)
                .setTitle("Save Credentials?")
                .setMessage("Do you want to save your login details for future visits?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userConsentGiven = true;  // User consent given to save credentials
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show();
    }

    // Method to save user credentials
    private void saveUserCredentials(String username, String password) {
        if (userConsentGiven) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USERNAME_KEY, username);
            editor.putString(PASSWORD_KEY, password);
            editor.apply();
        }
    }

    // Method to load user credentials and autofill them
    private void loadUserCredentials() {
        String savedUsername = sharedPreferences.getString(USERNAME_KEY, null);
        String savedPassword = sharedPreferences.getString(PASSWORD_KEY, null);

        if (savedUsername != null && savedPassword != null) {
            // Code to autofill the username and password into the WebView login form
            // You will need to inject JavaScript into the WebView to fill the form fields
            webView.evaluateJavascript("document.getElementById('username').value = '" + savedUsername + "';", null);
            webView.evaluateJavascript("document.getElementById('password').value = '" + savedPassword + "';", null);
        }
    }

    private void reload(String url) {
        webView.loadUrl(url);
    }

    private void toggleDesktopView() {
        boolean isDesktopViewEnabled = webView.getSettings().getUserAgentString().contains("Mozilla");
        String userAgent;
        if (isDesktopViewEnabled) {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } else {
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";
        }
        webView.getSettings().setUserAgentString(userAgent);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ProjectToolkit.fadeOut(progress);

            String pageTitle = view.getTitle();
            if (pageTitle.startsWith("http://")) {
                toolbar.setSubtitle("WARNING! Unsafe site");
                toolbar.setNavigationIcon(R.drawable.round_warning_24);
                fab1.setVisibility(View.VISIBLE);
            } else {
                toolbar.setSubtitle(pageTitle);
            }
        }
    }

    private void getFile(String url) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String gotUrl = uri.toString();
                openInDocs(gotUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WebViewActivity.this, "Failed to load media", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openInDocs(String pdfUrl) {
        String encodedUrl = Uri.encode(pdfUrl, "UTF-8");
        String googleDocsViewerUrl = "https://docs.google.com/gview?embedded=true&url=" + encodedUrl;
        webView.loadUrl(googleDocsViewerUrl);
    }

    private void initiateRequest(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+ "mechanical.official73@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Unofficial Mech - Request for server");
        intent.putExtra(Intent.EXTRA_TEXT, "Unofficial Mech - (Write about your request..)");
        startActivity(intent);
        fab1.setVisibility(View.GONE);
    }



}