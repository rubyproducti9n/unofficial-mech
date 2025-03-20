package com.rubyproducti9n.unofficialmech;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    private HomeAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    WebView webView;
    LinearProgressIndicator progressBar;

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideo();
            }
        });

        webView = (WebView) view.findViewById(R.id.webView);
        progressBar = (LinearProgressIndicator) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        webView.loadUrl("https://rubyproducti9n.github.io/mech/index.html");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
//        webView.setWebViewClient(new WebViewClient());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        List<HomeItem1> items = Arrays.asList(
                new HomeItem1(
                        "02:32",
                        "https://rubyproducti9n.github.io/mech/thumbnail/thmb_workshop.jpg",
                        "Workshop Visit, Manmad | Unofficial ...",
                        "https://rubyproducti9n.github.io/mech/img/unofficial_channel_art.png",
                        "Unofficial Mech • ",
                        "2023-02-26",
                        "https://youtu.be/aH16QQoPQdk"),
                new HomeItem1(
                        "04:19",
                        "https://rubyproducti9n.github.io/mech/thumbnail/thmb_antarang_2k22.webp",
                        "ANTARANG 2K22 Official Video | 2K22",
                        "https://rubyproducti9n.github.io/mech/img/unofficial_channel_art.png",
                        "Unofficial Mech • ",
                        "2022-05-25",
                        "https://youtu.be/SVmoLLn4I_s")
//                new HomeItem1(
//                        "SHORTS",
//                        "https://rubyproducti9n.github.io/mech/thumbnail/placeholder.png",
//                        "Oreo Biscuit Prank | Part I",
//                        "https://rubyproducti9n.github.io/mech/img/unofficial_channel_art.png",
//                        "Unofficial Mech • ",
//                        "2023-05-25",
//                        "https://youtube.com/shorts/BCOoZgfmv90?feature=share"),
//                new HomeItem1(
//                        "SHORTS",
//                        "https://rubyproducti9n.github.io/mech/thumbnail/placeholder.png",
//                        "Oreo Biscuit Prank | Part I",
//                        "https://rubyproducti9n.github.io/mech/img/unofficial_channel_art.png",
//                        "Unofficial Mech • ",
//                        "2023-05-25",
//                        "https://youtube.com/shorts/9BaYEpmXZcs?feature=share")
        );
        adapter = (HomeAdapter) new HomeAdapter(items);
//
        recyclerView.setAdapter(adapter);


        return view;
    }

    public class WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            return super.shouldOverrideUrlLoading(view, request);
//            view.loadUrl(url);
        return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void addVideo(){
        String url = "https://forms.gle/PuAvcpU1hf7XhwEd9";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }


}