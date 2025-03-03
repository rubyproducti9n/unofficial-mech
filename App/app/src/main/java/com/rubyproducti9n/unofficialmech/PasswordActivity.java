package com.rubyproducti9n.unofficialmech;

import static com.rubyproducti9n.unofficialmech.ProjectToolkit.animateVertically;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.getSystemAdValue;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;
import java.util.concurrent.Executors;


public class PasswordActivity extends BaseActivity {

    ImageView bgImg2;
    MaterialCardView blurMc;
    ProgressDialog progressDialog;
    private RewardedAd rewardedAd;
    String getUserName, getUserEmail, uid;
    boolean getUserEmailVerified;
    RewardedInterstitialAd rewardedInterstitialAd;
    Uri getUserPhotoUrl;
    private Animation rotate, rotateReverse;
    BroadcastReceiver broadcastReceiver;
    TextInputEditText materialText;
    TextInputEditText materialEmail;
    MaterialButton createAccountBtn;
    MaterialButton loginBtn;
    TextView expiryDateTxtView;
    ImageView cog1, cog2, cog4;
    private FirebaseAuth mAuth;
    TextView slogans;
    MaterialCardView mc, logo;
    private static final String[] welcomeMessages = {
            "Welcome to Unofficial Mech! Where every click brings you closer to amazing experiences.",
            "Unlock a world of possibilities at Unofficial Mech. Login now!",
            "Join the Unofficial Mech family and discover a seamless digital journey.",
            "Your adventure begins here! Login to Unofficial Mech and start exploring.",
            "Step into the future with Unofficial Mech. Login for an unparalleled experience.",
            "Elevate your moments with Unofficial Mech. Register to make memories.",
            "Connect, Create, and Conquer with Unofficial Mech. Login to your success story.",
            "Welcome back! Your Unofficial Mech journey continues here.",
            "Login now and witness the magic of Unofficial Mech.",
            "Empower your day with Unofficial Mech. Registration is just a click away.",
            "Discover more, experience more. Join us at Unofficial Mech today!",
            "Your Unofficial Mech account is your gateway to a world of possibilities.",
            "Login and let the adventure begin – exclusively at Unofficial Mech.",
            "Start your day right with Unofficial Mech. Login for positivity and inspiration.",
            "Welcome to the heartbeat of innovation – welcome to Unofficial Mech.",
            "Your journey, your rules. Register now at Unofficial Mech.",
            "Unlock your potential with Unofficial Mech. Your success story starts here.",
            "Revolutionize your routine. Join us at Unofficial Mech and login to change.",
            "Empowering connections, one login at a time – welcome to Unofficial Mech.",
            "Life’s an adventure. Start yours with Unofficial Mech.",
            "Turn dreams into reality. Register at Unofficial Mech and make it happen.",
            "Login and let your uniqueness shine with Unofficial Mech.",
            "At Unofficial Mech, we turn clicks into unforgettable experiences. Join us!",
            "Your digital sanctuary awaits. Login to Unofficial Mech now.",
            "Welcome to the future of Unofficial Mech. Experience the difference.",
            "Start your journey to success – login to Unofficial Mech today!",
            "Join the revolution. Login to Unofficial Mech for a transformative experience.",
            "At Unofficial Mech, every login is a step towards a brighter tomorrow.",
            "Innovation begins with you. Register at Unofficial Mech and be the change.",
            "Welcome aboard! Your ticket to Unofficial Mech excellence is just a login away.",
            "Your ideas, our platform. Register at Unofficial Mech and bring them to life.",
            "Ignite your passion. Join Unofficial Mech and login to your dreams.",
            "Seamless experiences, endless possibilities. Login to Unofficial Mech now.",
            "Discover a world tailored to you – login to Unofficial Mech today!",
            "At Unofficial Mech, your journey is our priority. Login and let’s go!",
            "Transform the ordinary into extraordinary. Register at Unofficial Mech now.",
            "Welcome to Unofficial Mech – where dreams become achievements.",
            "Join the community of achievers. Login to Unofficial Mech and stand out.",
            "Elevate your lifestyle. Login to Unofficial Mech for a premium experience.",
            "Your canvas, your masterpiece. Paint your future with Unofficial Mech.",
            "Dive into innovation. Register at Unofficial Mech and make waves.",
            "At Unofficial Mech, we turn dreams into reality. Login and dream big!",
            "Your digital companion for success. Join Unofficial Mech and conquer.",
            "Seize the day with Unofficial Mech. Login now for a brighter tomorrow.",
            "Your journey, your story. Write it with Unofficial Mech.",
            "At Unofficial Mech, every login is a step towards greatness. Join us!",
            "Create, connect, and captivate. Welcome to Unofficial Mech.",
            "Embrace the future. Register at Unofficial Mech for a new beginning.",
            "Start your adventure with Unofficial Mech. Login now and explore.",
            "Join the league of extraordinary. Login to Unofficial Mech and thrive."
    };
    AdManager adManager;

    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


//        if (requestCode == 1) {
//            // Check if all permissions are granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
//                // All permissions granted, proceed with your app functionalities
//            } else {
//                // Handle permission denial for all permissions
//                requestAllPermissions();
////                Toast.makeText(this, "All permissions are required for the app to function properly. Please grant them in settings.", Toast.LENGTH_SHORT).show();
//            }
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        adManager = new AdManager(PasswordActivity.this);
        adManager.loadInterstitialAd(PasswordActivity.this);
//        adManager.loadRewardedVideoAd();

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestAllPermissions();
        }
        //checkPermissions();
//        new Task1().execute();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

//        getCurrentUserAuth(new CurrentUser()
//            @Override
//            public void onUserFound(String name, String email, String uid, Uri avatarUrl, boolean verified) {
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PasswordActivity.this);
//                builder.setTitle("User Info:");
//                builder.setMessage("Name: " + name + "\n" +
//                        "Email: " + email + "\n" +
//                        "UID: " + uid + "\n" +
//                        "AvatarURL: " + avatarUrl + "\n" +
//                        "Verified Email: " + verified + "\n");
//                builder.show();
//            }
//        });


        slogans = findViewById(R.id.textView4);
        setRandomWelcomeMessage();

        ProjectToolkit.disableStatusBar(this);


        mc = findViewById(R.id.blur_mc);
        logo = findViewById(R.id.img_logo);
//        logo.setTransitionName("logo");
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideAndFadeIn(logo, mc, PasswordActivity.this);
                            }
                        });
                    }
                });
//                new Task2().execute();
//                hideAndFadeIn(mc);
//                animateViews(logo, mc, PasswordActivity.this);
            }
        });

        ImageView bgImg1 = findViewById(R.id.bgImg1);
        bgImg2 = findViewById(R.id.bgImg2);
        blurMc = findViewById(R.id.blur_mc);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        animateVertically(PasswordActivity.this, blurMc, 0, 1000);
//                        animateVertically(PasswordActivity.this, bgImg2, -250, 2000);
                    }
                });
            }
        });
//        animateVertically(this, bgImg1, 250, 2000);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//        ImageView bgImg = findViewById(R.id.robotic_heart);
//        ImageView blur= findViewById(R.id.blur);

//        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.morph_yellow2);
//        Bitmap tBg = BitmapFactory.decodeResource(getResources(), R.drawable.morph_yellow2);
//        Bitmap blurredBitmap = ProjectToolkit.blurBitmap(this, bg, 24);
//        bgImg.setImageBitmap(blurredBitmap);

//        Picasso.get().load("https://static.vecteezy.com/system/resources/previews/020/911/452/non_2x/blur-hand-draw-brush-free-png.png").into(blur);

        broadcastReceiver = new ConnectionReceiver();
        registerNetworkBroadcast();

        createAccountBtn = findViewById(R.id.create_account);
        createAccountBtn.setEnabled(true);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new MaterialAlertDialogBuilder(PasswordActivity.this)
//                        .setTitle("Oops!")
//                                .setMessage("We have paused this service for a while. You can still access the content using SignIn as Guest button. We apologise for the inconvenience")
//                                        .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        }).show();
                startActivity(new Intent(PasswordActivity.this, CreateAccount.class));
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer = MediaPlayer.create(this, R.raw.clock_ticking_sfx);
//        mediaPlayer.setLooping(true);
        //mediaPlayer.start();

//        cog1 = findViewById(R.id.cog);
//        cog2 = findViewById(R.id.cog2);
//        cog4 = findViewById(R.id.cog4);


        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        rotateReverse = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateReverse.setDuration(20000);
        rotateReverse.setRepeatCount(Animation.INFINITE);
        rotateReverse.setInterpolator(new LinearInterpolator());


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cog1.startAnimation(rotate);
//                cog2.startAnimation(rotateReverse);
//                cog4.startAnimation(rotate);
//            }
//        },100);



        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new MaterialAlertDialogBuilder(PasswordActivity.this)
//                    .setTitle("Oops!")
//                    .setMessage("We have paused this service for a while. You can still access the content using SignIn as Guest button. We apologise for the inconvenience")
//                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
//                BottomSheetLogin bottomSheetFragment = new BottomSheetLogin();
//                bottomSheetFragment.show(getSupportFragmentManager(), "BottomSheet");
                startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
            }
        });

        MaterialButton guest = findViewById(R.id.guestbtn);
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CustomDialog.showCustomDialog(
//                        PasswordActivity.this,
//                        "SignIn as guest",
//                        "While signing by Guest, your access will be limited you'll need to login or create a new account to enjoy all features",
//                        "SignIn",
//                        "Cancel",
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                showDialog();
//                                loadRewardInterstitial(PasswordActivity.this, PasswordActivity.this);
////                                loadRewardedAd(PasswordActivity.this);
//                            }
//                        },
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        }
//                );

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PasswordActivity.this);
                builder.setTitle("SignIn as guest");
                builder.setMessage("While signing by Guest, your access will be limited you'll need to login or create a new account to enjoy all features. Login will be granted after watching the full ad.\n\n Are you sure to proceed? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PasswordActivity.this);
                        SharedPreferences.Editor authEditor = pref.edit();
                        authEditor.putString("auth_userId", null);
                        authEditor.putString("auth_name", null);
                        authEditor.putString("auth_contact", null);
                        authEditor.putString("auth_division", null);
                        authEditor.putString("auth_email", null);
                        authEditor.putString("auth_password", null);
                        authEditor.putString("auth_altPassword", null);
                        authEditor.putString("auth_prn", null);
                        authEditor.putString("auth_gender", null);
                        authEditor.putString("auth_rollNo", null);
                        authEditor.putString("auth_userole", null);
                        authEditor.apply();
                        showDialog();
                        loadRewardInterstitial(PasswordActivity.this, PasswordActivity.this);
//                        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
//                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });


        MaterialButton help = findViewById(R.id.helpbtn);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] item = {"Terms & Conditions", "Privacy Policy", "FAQs", "About"};
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PasswordActivity.this);
                builder.setTitle("Help");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                showTerms();
                                break;
                            case 1:
                                showPrivacyPoicy();
                                break;
                            case 2:
                                startActivity(new Intent(PasswordActivity.this, FAQActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(PasswordActivity.this, AboutActivity.class));
                                break;
                        }
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });



        if (Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.DynamicWhite));
        }
    }

    private void showTerms(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PasswordActivity.this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage(R.string.termNconditions);
        builder.setIcon(R.drawable.round_supervisor_account_24);
        builder.setCancelable(false);
        builder.setNegativeButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void showPrivacyPoicy(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PasswordActivity.this);
        builder.setTitle("Privacy Policy");
        builder.setMessage(R.string.privacyPolicy);
        builder.setIcon(R.drawable.shield_question_24dp_e8eaed_fill0_wght400_grad0_opsz24);
        builder.setCancelable(false);
        builder.setNegativeButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser!=null){
//
//        }else{
//            Intent intent = new Intent(this, )
//        }
    }

    public void animateViews(View view1, View view2, Context context) {
        // Animation duration (in milliseconds)
        int animationDuration = 2000;

        // Get display metrics for density conversion
        float density = context.getResources().getDisplayMetrics().density;

        // Define animation movements (in dp)
        int movementUp = -300; // Slide view1 up by 500dp
        int movementDown = 324; // Slide view2 down by 1024dp

        // Convert movements to pixels
        int movementUpInPixels = (int) (movementUp * density);
        int movementDownInPixels = (int) (movementDown * density);

        // Create AnimatorSet for view1 animation
        AnimatorSet animatorSet1 = createAnimation(view1, movementUpInPixels, animationDuration);

        // Create AnimatorSet for view2 animation (reversed direction)
        AnimatorSet animatorSet2 = createAnimation(view2, movementDownInPixels, animationDuration);

        // Start animations with a slight delay for a cascading effect (optional)
        animatorSet1.setStartDelay(100);
        animatorSet1.start();
        animatorSet2.start();
    }

    private AnimatorSet createAnimation(View view, int movement, int duration) {
        // Define animation properties
        float scaleFactor = 0.9f; // Scale down to 90% for "squished" effect
        float translationOffset = movement * scaleFactor; // Adjust translation based on scale

        // Create AnimatorSet for combined animation
        AnimatorSet animatorSet = new AnimatorSet();

        // Animate translationY for vertical movement
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(view, "translationY", movement, 0f);

        // Animate scaleX and scaleY for "squish" effect
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", scaleFactor, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", scaleFactor, 1f);

        // Set animation duration and easing
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AnticipateOvershootInterpolator()); // BounceInterpolator for elastic feel

        // Combine animations and play
        animatorSet.playTogether(translationYAnimator, scaleXAnimator, scaleYAnimator);

        return animatorSet;
    }

    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading content, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setRandomWelcomeMessage() {
        Random random = new Random();
        int index = random.nextInt(welcomeMessages.length);
        String randomWelcomeMessage = welcomeMessages[index];

        slogans.setText(randomWelcomeMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestAllPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.CALL_PHONE,
                android.Manifest.permission.VIBRATE,
                Manifest.permission.INTERNET
        };
        ActivityCompat.requestPermissions(this, permissions, 1);
    }


    private void createUserAuth(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("TAG", "Created User with email:SUCCESS!");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                        }else{
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void signInUserAuth(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(PasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public interface CurrentUser{
        void onUserFound(String name, String email, String uid, Uri avatarUrl, boolean verified);
    }
    private void getCurrentUserAuth(CurrentUser callback){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUserName = user.getDisplayName();
            getUserEmail = user.getEmail();
            getUserPhotoUrl = user.getPhotoUrl();

            getUserEmailVerified = user.isEmailVerified();

            uid = user.getUid();
            callback.onUserFound(getUserName, getUserEmail, uid, getUserPhotoUrl, getUserEmailVerified);
        }else{
            callback.onUserFound(null,null,null,null,false);
        }
    }


    public void checkPassword(){
        String Password = materialText.getText().toString();
        String Email = materialEmail.getText().toString();
        if(Password.equals("mech.beta") && Email.equals("om.lokhande34@gmail.com") || Email.equals("example")){
            Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        }else if (Password.isEmpty()){
            Toast.makeText(this, "Provide a password", Toast.LENGTH_SHORT).show();
        }
        else if(Password.equals("dev@9693")){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PasswordActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Do you want to enter in Developer mode?");
            builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(PasswordActivity.this, DeveloperActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else{
            Intent intent = new Intent(PasswordActivity.this, AccessDeniedActivity.class);
            startActivity(intent);

        }
    }

    private void hideAndFadeIn(View element, View view2, Context context) {
        final int dur = 500;
        view2.animate()
                        .alpha(0f)
                                .setDuration(dur)
                .setStartDelay(250)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                                .start();
        element.animate()
                .alpha(0f)  // Fade to 0 opacity
                .setDuration(dur)  // Set duration to 500 milliseconds
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
//                        element.setVisibility(View.GONE);  // Hide after animation
                        // Schedule fade-in after hiding
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animateViews(element, view2, context);
//                                element.setVisibility(View.VISIBLE);  // Make visible for fade-in
                                element.animate()
                                        .alpha(1f)  // Fade to full opacity
                                        .setDuration(dur)  // Set duration to 500 milliseconds
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .start();
                                view2.animate()
                                        .alpha(1f)
                                        .setDuration(dur)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .start();
                            }
                        }, 2000);  // Delay for 3 seconds
                    }
                })
                .start();
    }


    private void performAnim(){
        MaterialCardView mc = findViewById(R.id.blur_mc);
        MaterialCardView drag = findViewById(R.id.dragBtn);
        mc.setOnTouchListener(new View.OnTouchListener() {
            private float startY, initialY;
            private boolean bottomSheetTriggered = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        initialY = drag.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getY() - startY;

                        // Ensure mc doesn't exceed -112dp even if user drags further
                        deltaY = Math.min(deltaY, 1048 - initialY);

                        // Set mc's Y position directly for smooth dragging
                        mc.setY(initialY + deltaY);
                        // Limit dragging to downwards movement with a maximum of 50dp

                        // Restrict up-drag (deltaY positive)
                        if (deltaY > 0) {
                            mc.setY(initialY);  // Reset to original position
                            deltaY = 0;  // Set deltaY to 0 to prevent animation issues
                        }

//                        deltaY = Math.min(deltaY, -126);

//                        drag.setY(initialY + deltaY);
//                        mc.setY(initialY + deltaY);
                        if (-118 > deltaY){
                            mc.animate()
                                    .translationY(-12)
                                    .setDuration(500)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .start();
                        }
//                        if (-128 > Math.min(deltaY, -112)){
//                            // Animate mc card with 50dp translationY
//                        mc.animate()
//                                .translationY(-12)
//                                .setDuration(500)
//                                .setInterpolator(new AccelerateDecelerateInterpolator())
//                                .start();
//
//                        }

                        // Trigger bottom sheet only once and animate elements back
                        if (!bottomSheetTriggered && deltaY <= -112) {
//                            updateBottomSheetState();
                            bottomSheetTriggered = true;
//                            animateElementsBack();
//                            BottomSheetAccountMenu bottomSheetAccountMenu = new BottomSheetAccountMenu();
//                            bottomSheetAccountMenu.show(getSupportFragmentManager(), "tags");
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        // Reset triggered flag for subsequent drags
                        bottomSheetTriggered = false;
                        return false;
                }
                return false;
            }

            private void animateElementsBack() {
                mc.animate()
                        .translationY(0)  // Assuming mc's original translationY is 0
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                // Add animation for drag view back to its initial position if needed
            }
        });
        mc.performClick();
    }

    protected void registerNetworkBroadcast(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

    }
    protected void unregisteredNetwork(BroadcastReceiver broadcastReceiver){
        try {
            unregisteredNetwork(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void loadInterstitialAd(Activity context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PasswordActivity.this);
        if (getSystemAdValue(pref)){
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, "ca-app-pub-5180621516690353/7275112879", adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    loginUser(PasswordActivity.this);
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    interstitialAd.show(context);
                    loginUser(PasswordActivity.this);
                }
            });
        }
    }

    private void loadRewardInterstitial(Activity activity, Context context){
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                //ca-app-pub-5180621516690353/6410422068
                RewardedInterstitialAd.load(context, "ca-app-pub-5180621516690353/6410422068",
                        new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                                Log.d(ContentValues.TAG, "Ad was loaded.");
                                rewardedInterstitialAd = ad;
                                rewardedInterstitialAd.show(activity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        loginUser(PasswordActivity.this);
                                        Toast.makeText(activity, "Content was loaded!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(ContentValues.TAG, loadAdError.toString());
                                rewardedInterstitialAd = null;
                                loginUser(PasswordActivity.this);
                                loadInterstitialAd(PasswordActivity.this);
                            }
                        });
            }
        });
    }

    private void loadRewardedAd(Activity context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PasswordActivity.this);
        if (getSystemAdValue(pref)){
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(context, "ca-app-pub-5180621516690353/2338712470",
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.d(ContentValues.TAG, loadAdError.toString());
                            rewardedAd = null;
                            loadInterstitialAd(PasswordActivity.this);
                        }
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd = ad;
                            if (progressDialog!=null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            rewardedAd.show(context, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    loginUser(PasswordActivity.this);
                                }
                            });
                            Log.d(ContentValues.TAG, "Ad was loaded.");
                        }
                    });
        }
    }

    private void loginUser(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor authEditor = pref.edit();
        authEditor.putString("auth_userId", null);
        authEditor.putString("auth_name", null);
        authEditor.putString("auth_contact", null);
        authEditor.putString("auth_division", null);
        authEditor.putString("auth_email", null);
        authEditor.putString("auth_password", null);
        authEditor.putString("auth_altPassword", null);
        authEditor.putString("auth_prn", null);
        authEditor.putString("auth_gender", null);
        authEditor.putString("auth_rollNo", null);
        authEditor.putString("auth_userole", null);
        authEditor.apply();
        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onGearIconClick(ImageView cog){
        Animation scaleDownAnim = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDownAnim.setDuration(200);
        scaleDownAnim.setFillAfter(true);
        scaleDownAnim.setInterpolator(new LinearInterpolator());

        Animation scaleUpAnim = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUpAnim.setDuration(200);
        scaleUpAnim.setFillAfter(true);
        scaleUpAnim.setInterpolator(new LinearInterpolator());

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleDownAnim);
        animationSet.addAnimation(scaleUpAnim);

        cog.startAnimation(animationSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisteredNetwork(broadcastReceiver);
    }


    private class Task1 extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            return null;
        }
    }

    private class Task2 extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            return null;
        }
    }

}