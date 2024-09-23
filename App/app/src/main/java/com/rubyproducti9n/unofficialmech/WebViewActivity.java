package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getServerUrl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebViewClientCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WebViewActivity extends AppCompatActivity {
    static LinearProgressIndicator progress;
    static MaterialToolbar toolbar;
    static MaterialCardView webErr;
    static WebView webView;
    ExtendedFloatingActionButton fab1;
    static String s = getServerUrl();
    public static String link;
//    static String s = "https://google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
//        link = "https://google.com";
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

        if (activity!=null && activity.endsWith("pdf")){
            openInDocs(link);
        }

        if (link!=null){
            toolbar.setTitle(R.string.app_name);
            toolbar.setSubtitle("loading...");
            if (link.equals(s)){
                fab1.setVisibility(View.GONE);
                new CheckServerOnlineTask().execute(s);
            }else{
                fab1.setVisibility(View.VISIBLE);
                webView.loadUrl(link);
                webErr.setVisibility(View.GONE);
            }
//            webView.loadUrl(link);
        }else{
            getFile("https://firebasestorage.googleapis.com/v0/unofficial-mech.appspot.com/o/ADHAR.pdf?alt=media&token=e42cf2fa-c306-46af-aed3-d09e435c9cb2");
//            webView.loadUrl("https://firebasestorage.googleapis.com/v0/unofficial-mech.appspot.com/o/ADHAR.pdf?alt=media&token=e42cf2fa-c306-46af-aed3-d09e435c9cb2");
//            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
//            Intent intent1 = new Intent(WebViewActivity.this, MainActivity.class);
//            startActivity(intent1);
//            finishAffinity();
        }

    }

    private void reload(String url){
        webView.loadUrl(url);
    }

    private void toggleDesktopView() {
        boolean isDesktopViewEnabled = webView.getSettings().getUserAgentString().contains("Mozilla");
        String userAgent;
        if (isDesktopViewEnabled) {
            userAgent = WebSettings.getDefaultUserAgent(this);
            int index = userAgent.indexOf("/");
        } else {
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";
        }
        webView.getSettings().setUserAgentString(userAgent);
    }
    private class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ProjectToolkit.fadeOut(progress);

            String pageTitle = view.getTitle();
            if (pageTitle.startsWith("http://")){
                toolbar.setSubtitle("WARNING! Unsafe site");
                toolbar.setNavigationIcon(R.drawable.round_warning_24);
                fab1.setVisibility(View.VISIBLE);
            }else{
                toolbar.setSubtitle(pageTitle);
            }

        }


//        @Override
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            super.onReceivedError(view, request, error);
//            toolbar.setSubtitle("Error occurred while loading!");
//            fab1.setVisibility(View.VISIBLE);
//        }
    }

    private void initiateErr(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle("Oops!")
                .setMessage("Server seems offline, check again later\nServer time: 9 PM onwards")
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initiateRequest();
                    }
                });
        builder.show();
    }



    public class CheckServerOnlineTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            String serverUrl = urls[0];
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                Log.e("MyApp", "Error checking server: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isOnline) {
            super.onPostExecute(isOnline);
            if (isOnline) {
                webView.loadUrl(s);
            } else {
                // Server is offline, show an error message to the user
                initiateErr();
                webErr.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                toolbar.setSubtitle("Offline");
                initiateRequest();
//                Toast.makeText(getApplicationContext(), "Server is offline!", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    private boolean isXamppServerOnline(String serverUrl) {
//        try {
//            // Create a URL object with a short timeout to avoid long waits
//            URL url = new URL(serverUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setConnectTimeout(5000); // Set a timeout of 5 seconds
//
//            // Check if the connection can be established
//            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
//        } catch (IOException e) {
//            Log.e("Unofficial Mech", "Error checking server: " + e.getMessage());
//            return false; // Assume offline if an exception occurs
//        }
//    }


//    private class MyWebViewClient extends WebViewClient {
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            // Handle URL loading if needed (e.g., open links in external browser)
//            return super.shouldOverrideUrlLoading(view, url);
//        }
//    }

    private void getFile(String url){

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

    private void openInDocs(String pdfUrl){
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