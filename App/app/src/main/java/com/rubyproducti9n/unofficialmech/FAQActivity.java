package com.rubyproducti9n.unofficialmech;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FAQActivity extends AppCompatActivity {
    ConstraintLayout faqContainer;
    List<ItemFaq> faqList;
    FaqAdapter faqAdapter;
    RecyclerView faqRecyclerView;

    private CircularProgressIndicator progressInd;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqactivity);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        faqRecyclerView = findViewById(R.id.recyclerView);
        String[] questions = {"Question 1", "Question2"};
        String[] answers = {"Ans 1", "Ans2"};


        faqList = new ArrayList<>();
        faqList.add(new ItemFaq("I forgot my password how can I change the password?", "Click on the below button that says 'Change password'. Click \"Request Password Reset\" and we'll reset your password in 7 working days. Please note that we will contact you regarding your password.\n" +
                "\n" +
                "A random password will be generated by our system so that you will never loose it again"));
        faqList.add(new ItemFaq("How to enable Quick-Menu?", "To enable Quick-Menu Go to Settings > Open quick-menu on startup."));
        faqList.add(new ItemFaq("How can I update my account information?", "Updating your account information will be available soon in the future updates, stay tuned!"));
        faqList.add(new ItemFaq("How can I delete my account?", "You can request for account deletion. Go to My account by clicking on the top right profile icon from home. Go on My details and click on Delete my account. Please note that even after deleting your account some details like PRN, Division and Your Name will not be deleted for our records."));
        faqList.add(new ItemFaq("How do I troubleshoot common problems with the app?", "1. If the app gets crashed frequently then you should contact us at mechanical.official73@gmail.com to get the issue solved as soon as possible \n \n" +
                "2. While on Home page open menu from the top-left corner and go to Feedback. A Google Form will open in your private browser, fill in the details and describe your problem in detail and provide screenshot if any."));
        faqList.add(new ItemFaq("How do you protect my personal information?", "Secure Storage: We store your data at Google, which has industry-leading security practices. This means your information is kept in highly guarded data centers that use the latest technology to prevent unauthorized access.\n" +
                "\n" +
                "Encryption Magic: When your information travels between your device and Google's servers, it's scrambled with a special code called encryption. This makes it unreadable to anyone who shouldn't see it, like a secret message only you and Google can understand.\n" +
                "\n" +
                "Password Power:  We take passwords seriously.  Your password is encrypted as well, so it's never stored in plain text. This means even if someone were to peek into our secret storage, they wouldn't be able to see your actual password."));


        faqAdapter = new FaqAdapter(faqList, FAQActivity.this);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        faqRecyclerView.setAdapter(faqAdapter);

        progressInd = findViewById(R.id.progress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 100){
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressInd.setProgress(progressStatus, true);
                        }
                    });
                    try {
                        Thread.sleep(15);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ProjectToolkit.fadeOut(progressInd);
                        ProjectToolkit.fadeIn(faqRecyclerView);
                    }
                }, 500);
            }
        }).start();



    }


}