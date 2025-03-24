package com.rubyproducti9n.unofficialmech;


import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isBtech;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.isFaculty;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.pref;
import static com.rubyproducti9n.unofficialmech.ProjectToolkit.serviceCheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLibrary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLibrary extends Fragment {

    Context context;
    String timetable = null;
    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private static final String PHONEPE_PACKAGE_NAME = "com.phonepe.app";
    String[] timeTABLE = {"https://drive.google.com/file/d/19ykn51CB5uJeiuAvDEPOVyZmoNwXouk0/view?usp=sharing",
            "https://drive.google.com/file/d/1K7Xg0H10juIKu9BOs5_CypK7A_UzQCyu/view?usp=sharing",
            "https://drive.google.com/file/d/1uN1W3gw1h-AnEG0HtdN6x8PXlw9lwQMq/view?usp=sharing"};
//    A: https://drive.google.com/file/d/1gmBkUKL9ssR-cx6uuQDRuMHbP3rNkrNX/view?usp=drivesdk
//    B: https://drive.google.com/file/d/1_Do71DdGJHrZRlpNwwZmG9B4lUfFwr6O/view?usp=drivesdk
//    C: https://drive.google.com/file/d/1gmOJbcLxuKrRyCGUUQTE2xUo-0DxUD7u/view?usp=drivesdk

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentLibrary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLibrary.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLibrary newInstance(String param1, String param2) {
        FragmentLibrary fragment = new FragmentLibrary();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);


//        FloatingActionButton fab = view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(requireContext()(), PluginsActivity.class);
//                startActivity(intent);
//            }
//        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String div = preferences.getString("auth_division", null);
        String userRole = preferences.getString("auth_userole", null);

//        CircularProgressIndicator progressIndicator = view.findViewById(R.id.progressBar);
//        progressIndicator.setVisibility(View.GONE);


        MaterialCardView ai_tools = (MaterialCardView) view.findViewById(R.id.mc_aiTools);
        MaterialCardView examRegistration = (MaterialCardView) view.findViewById(R.id.mc_student);
        MaterialCardView erp = (MaterialCardView) view.findViewById(R.id.mc_ERP);
        MaterialCardView geminiModel = (MaterialCardView) view.findViewById(R.id.mc_gemini);
        MaterialCardView timeTable = (MaterialCardView) view.findViewById(R.id.mc_TimeTable);
        MaterialCardView project = (MaterialCardView) view.findViewById(R.id.mc_projects);
        MaterialCardView facultyCheck = view.findViewById(R.id.mc_facultyCheck);
        MaterialCardView pay = (MaterialCardView) view.findViewById(R.id.mc_superset);
        MaterialCardView guidelines = (MaterialCardView) view.findViewById(R.id.mc_guidlines);

        MaterialCardView unofficialAlbum = (MaterialCardView) view.findViewById(R.id.mc_album);
        BottomSheetProfileEdit bottomSheetFragment = new BottomSheetProfileEdit();

        if (userRole != null) {
            if (isFaculty(requireContext())){
                unofficialAlbum.setAlpha(1.0f);
                unofficialAlbum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateAlbum();
                    }
                });

                ai_tools.setVisibility(View.GONE);
                ai_tools.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateAiTools();
                    }
                });

                examRegistration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateIonStudent();
                    }
                });

                erp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateERP();
                    }
                });

                geminiModel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateGemini();
                    }
                });

                timeTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateCalendar();
                    }
                });

                project.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateProject();
                    }
                });

                facultyCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateFacultyCheck();
                    }
                });

                guidelines.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateGuidelines();
                    }
                });

                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isBtech(pref(requireActivity()).getString("auth_prn", null))){
                            initiateSuperset();
                        }else{
                            Toast.makeText(requireContext(), "You are not eligible for Superset placement service", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else{
                unofficialAlbum.setAlpha(1.0f);
                unofficialAlbum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateAlbum();
                    }
                });

                ai_tools.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateAiTools();
                    }
                });

                examRegistration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateIonStudent();
                    }
                });


                erp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateERP();
                    }
                });

                geminiModel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateGemini();
                    }
                });

                timeTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateCalendar();
                    }
                });

                project.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateProject();
                    }
                });

                facultyCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateFacultyCheck();
                    }
                });

                guidelines.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateGuidelines();
                    }
                });
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initiateSuperset();
                    }
                });
            }
        }else{
            examRegistration.setAlpha(1.0f);
            ai_tools.setAlpha(1.0f);
            erp.setAlpha(1.0f);
            geminiModel.setAlpha(1.0f);
            guidelines.setAlpha(1.0f);
            timeTable.setAlpha(1.0f);
            project.setAlpha(0.5f);
            facultyCheck.setAlpha(0.5f);

            ai_tools.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateAiTools();
                }
            });


            timeTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateCalendar();
                }
            });
            examRegistration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateIonStudent();
                }
            });


            erp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateERP();
                }
            });

            geminiModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateGemini();
                }
            });
            unofficialAlbum.setAlpha(1.0f);
            unofficialAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), AlbumActivity.class);
                    startActivity(intent);
                }
            });
            guidelines.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateGuidelines();
                }
            });

            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateSuperset();
                }
            });
        }

//        if (userRole!=null && userRole.equals("Faculty")){
//            unofficialAlbum.setAlpha(0.5f);
//        }else if(userRole==null){
//            unofficialAlbum.setAlpha(0.5f);
//        }
//        else{
//            unofficialAlbum.setAlpha(1.0f);
//            unofficialAlbum.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(requireContext()(), AlbumActivity.class);
//                    startActivity(intent);
//                }
//            });
//        }


        return view;
    }

    private boolean getServerStat(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean status = pref.getBoolean("serverStat", false);
        return status;
    }

    public static void options(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setItems(new String[]{"Register for Exam", "View Result (Server 1)", "View Result (Server 2)", "Download ION Student App"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("link", "http://136.233.62.179/ionems_sce_student/login");
                        context.startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(context, WebViewActivity.class);
                        intent1.putExtra("link", "http://136.233.62.180/iondvs_student/login");
                        context.startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(context, WebViewActivity.class);
                        intent2.putExtra("link", "http://192.168.54.201/iondvs_student");
                        context.startActivity(intent2);
                        break;
                    case 3:
                        MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(context)
                                .setTitle("Warning")
                                .setMessage("ION Student app may be outdated, it is recommended to notify to Exam Cell about this issue.\n\nAre you sure to download the outdated version of the app?")
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        download(context);
                                    }
                                })
                                .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                ;builder1.show();
                        break;
                }
            }
        }).show();
    }

    private static void download(Context context){
        PackageManager packageManager = context.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage("com.ionidea.ionamsstudent");

        if (launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            DownloadApk.startDownloadingApk(context, "https://github.com/rubyproducti9n/group-3/raw/main/ion/IonStudent_base.apk", "ion_student.apk");
        }
    }
    private void initiateIonStudent(){
        options(getContext());
    }
    private void initiateAiTools(){
        Intent intent = new Intent(getContext(), AIToolsActivity.class);
        startActivity(intent);
    }
    private void initiateERP(){

        String[] items = {"Login", "Accounts", "Attendance", "Examination", "Project Monitoring", "Study Material", "Timetable"};
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("ERP")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(requireContext(), WebViewActivity.class);
                        switch(which){
                            case 0:
//                                Intent intent = new Intent(requireContext(), WebViewActivity.class);
//                                intent.putExtra("link", "https://mysanjivani.edupluscampus.com/"); // Pass your URL here
//                                startActivity(intent);
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/")));
                                break;
                            case 1:
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/Accounts")));
                                break;
                            case 2:
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/Attendance")));
                                break;
                            case 3:
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/Examination")));
                                break;
                            case 4:
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/project-monitoring")));
                                break;
                            case 5:
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/StudyMaterial")));
                                break;
                            case 6:
                                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mysanjivani.edupluscampus.com/TimeTable")));
                                break;
                        }
                    }
                });
        b.show();

//        Intent intent = new Intent(getContext(), AppStoreActivity.class);
//        startActivity(intent);
    }
    private void initiateGemini(){
        Intent intent = new Intent(getContext(), ArtificialIntelligenceActivity.class);
        intent.putExtra("service", "gemini-pro");
        startActivity(intent);
    }
    private void initiateCalendar(){
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Manager")
                        .setMessage("Oops! this feature is in maintenance mode. Please try again later")
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
//        startActivity(new Intent(requireContext(), CalendarActivity.class));
    }
    private void initiateTimeTable(View view, String userRole, String div){
        serviceCheck(getContext(), new ProjectToolkit.ServiceCheckCallBack() {
            @Override
            public void onResult(Boolean result) {
                if (userRole!=null){
                    if (!result){
                        view.setAlpha(0.5f);
                    }else{
                        view.setAlpha(1.0f);
                        ProjectToolkit.fadeIn(view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                        String viewUrl = getTimeTable(getContext(), div);
//                        intent.setData((Uri.parse(viewUrl)));
//                        startActivity(intent);
                                if (div!=null){
                                    if(div.equals("A")){
                                        intent.setData((Uri.parse(timeTABLE[0])));
                                        startActivity(intent);
                                    }else if (div.equals("B")){
                                        intent.setData((Uri.parse(timeTABLE[1])));
                                        startActivity(intent);
                                    } else if (div.equals("C")) {
                                        intent.setData((Uri.parse(timeTABLE[2])));
                                        startActivity(intent);
                                    }else{
                                        view.setVisibility(View.GONE);
                                    }
                                }else{
                                    view.setVisibility(View.GONE);
                                    Log.d("User error", "User was not Logged In");
                                }

                                //String url =  "https://drive.google.com/file/d/1_Do71DdGJHrZRlpNwwZmG9B4lUfFwr6O/view?usp=drivesdk";
//                String fileNameFromUrl = getFileNameFromUrl(url);
//                ImageMagnifierActivity.openPdf(getContext(), fileNameFromUrl, "123");

//                Toast.makeText(getContext(), "Div:" + div, Toast.LENGTH_SHORT).show();
//                String url = "https://drive.google.com/file/d/1_Do71DdGJHrZRlpNwwZmG9B4lUfFwr6O/view?usp=drivesdk";
//
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("faculty_console/timetable/");
//                if (div !=null){
//                    ref.child(div).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            timetable = snapshot.getValue(String.class);
//                            if (timetable!=null && timetable.isEmpty()){
//                                try{
//                                    String url = "https://drive.google.com/file/d/1_Do71DdGJHrZRlpNwwZmG9B4lUfFwr6O/view?usp=drivesdk";
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData((Uri.parse(url)));
//                                    startActivity(intent);
//                                }catch (ParseException e){
//                                    Toast.makeText(getContext(), "Parsing error!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
                            }
                        });
                    }
                }else{
                    view.setAlpha(0.5f);
                }
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressIndicator.setVisibility(View.GONE);
//                    }
//                },800);
            }

        });
    }
    private void initiateProject(){
//        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
//        builder.setTitle("Coming Soon!");
//        builder.setMessage("We are working on this feature and will be available in next update");
//        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();

                Intent intent = new Intent(getContext(), ProjectActivity.class);
                startActivity(intent);
        //bottomSheetFragment.show(getChildFragmentManager(), "BottomSheet");
    }
    private void initiateFacultyCheck(){
        if (getServerStat()){
        FacultyCheckActivity bottomSheetCreateAccount = new FacultyCheckActivity();
        bottomSheetCreateAccount.show(requireActivity().getSupportFragmentManager(), "BottomSheet");
    }else{
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Oops!");
        builder.setMessage("Something went wrong, please restart the app and try again later. If issue persists contact us");
        builder.setPositiveButton("Contact Us", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+ "mechanical.official73@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Your App - Subject");
                intent.putExtra(Intent.EXTRA_TEXT, "Your App - Email Body");
                startActivity(intent);
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
    }
    private void initiateAlbum(){
        Intent intent = new Intent(getContext(), AlbumActivity.class);
        startActivity(intent);
    }
    private void initiateGuidelines(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Guidelines")
                .setMessage(R.string.guidlines)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void initiateSuperset(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.joinsuperset.com/students/"));
        requireActivity().startActivity(intent);
    }

    public void loadPaymentOptions(int amount){
        String[] item = {"Google Pay", "PhonePe"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pay using:")
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                initiateGooglePay(amount);
                                break;
                            case 1:
                                initiatePhonePePayment(amount);
                                break;
                        }
                    }
                });
        builder.show();
    }

    public void initiatePhonePePayment(double amount) {
        String uri = "upi://pay?pa=" + "7020162178@axl" + "&am=" + amount;
//        String url = "https://PhonePe/upi/pay?pa=" + merchantId + "&am=" + amount;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        intent.setPackage(PHONEPE_PACKAGE_NAME);
        startActivity(intent);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "PhonePe app was not found. Opening Google Pay...", Toast.LENGTH_SHORT).show();
//            initiateGooglePay();
//        }
    }

    private void initiateGooglePay(double amount){
        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "7020162178@axl")
//                                .appendQueryParameter("pa", "BCR2DN4TR3X3BBQP@upi")
                        .appendQueryParameter("pn", "Unofficial Mech")
                        .appendQueryParameter("mc", "6596-8373-5506")
                        .appendQueryParameter("tr", "Order_" + System.currentTimeMillis())
                        .appendQueryParameter("tn", "Service fees")
                        .appendQueryParameter("am", String.valueOf(amount))
                        .appendQueryParameter("cu", "INR")
//                                .appendQueryParameter("url", "https://test.merchant.website")
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
        startActivity(intent);
    }
    public void suggestFeedback(){
        String url = "https://forms.gle/vj47o28p7dvaB2gY9";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
}