package com.rubyproducti9n.unofficialmech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAlbum#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAlbum extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WebView webView;
    LinearProgressIndicator progressBar;

    ExtendedFloatingActionButton Extfab;
    private ImageAdapter adapter;
    private List<Item> filteredItem;
    AppBarLayout appBarLayout;
    boolean isAppBarExpanded = true;

    public FragmentAlbum() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAlbum.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAlbum newInstance(String param1, String param2) {
        FragmentAlbum fragment = new FragmentAlbum();
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

        View view = inflater.inflate(R.layout.fragment_album, container, false);


        filteredItem = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) new GridLayoutManager(getContext(), 1);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position % 2 == 0) {
//                    return GridLayoutManager.LayoutParams.MATCH_PARENT;
//                } else {
//                    return 1;
//                }
//            }
//        });
        recyclerView.setLayoutManager(layoutManager);
        List<Item> items = Arrays.asList(
                new Item(getString(R.string.host) + "memories/last mem/m1.jpg", "Final B.Tech Memos • 21 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m2.jpg", "Final B.Tech Memos • 09 Mar 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m3.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m4.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m5.jpg", "Final B.Tech Memos • 16 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m6.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m7.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m8.jpg", "Final B.Tech Memos • 16 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m9.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m10.jpg", "Final B.Tech Memos • 16 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m11.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m12.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m13.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m14.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m15.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m16.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m17.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m18.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m19.jpg", "Final B.Tech Memos • 09 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m20.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m21.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m22.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m23.jpg", "Final B.Tech Memos • 16 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m24.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m25.jpg", "Final B.Tech Memos • 16 Nov 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m26.jpg", "Final B.Tech Memos • 07 Oct 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m27.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m28.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),
                new Item(getString(R.string.host) + "memories/last mem/m29.jpg", "Final B.Tech Memos • 23 Dec 2024", ""),

                new Item(getString(R.string.host) + "new 2024/n1.jpg", "Gryphon's Training Academy Phase II • 07 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n2.jpg", "After College Godavari Vibes • 01 Mar 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n3.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n4.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n5.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n6.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n7.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n8.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n9.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n10.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n11.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n12.jpg", "Friends = Sukoon • 31 May 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n13.jpg", "Friends = Sukoon • 31 May 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n14.jpg", "Friends = Sukoon • 31 May 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n15.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n16.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n17.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n18.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n19.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n20.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n21.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n22.jpg", "Gryphon's Training Academy Phase II • 13 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n23.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n24.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n25.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n26.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n27.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n28.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n29.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n30.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n31.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n32.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n33.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n34.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n35.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n36.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n37.jpg", "Gryphon's Training Academy Phase II • 15 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n38.jpg", "Gryphon's Training Academy Phase II • 06 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n39.jpg", "Gryphon's Training Academy Phase II • 06 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n40.jpg", "Gryphon's Training Academy Phase II • 06 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n41.jpg", "Gryphon's Training Academy Phase II • 06 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n42.jpg", "Gryphon's Training Academy Phase II • 06 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n43.jpg", "Gryphon's Training Academy Phase II • 06 Jul 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n44.jpg", "Teachers Day • 05 Sep 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n45.jpg", "Teachers Day • 05 Sep 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n46.jpg", "Field Visit Nagar • 28 Mar 2024", "field visit"),
                new Item(getString(R.string.host) + "new 2024/n47.jpg", "Debate Competition • 28 Jan 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n48.jpg", "Dahi Handiii • 27 Aug 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n49.jpg", "Dahi Handiii • 27 Aug 2024", "training"),
                new Item(getString(R.string.host) + "new 2024/n50.jpg", "Dahi Handiii • 27 Aug 2024", "training"),


                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0006.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0008.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0013.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0016.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0018.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0019.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240129-WA0021.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0000.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0001.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0004.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0007.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0008.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0009.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0010.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0011.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0012.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0013.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0014.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0015.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0016.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0017.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0029.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0043.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0045.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0047.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0049.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0050.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240201-WA0052.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240202-WA0016.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG-20240202-WA0017.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),
                new Item(getString(R.string.host) + "memories/gryphons/IMG_1626.jpg", "Gryphon's Training Academy • 28 Jan 2023", "training"),

                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g1.jpg", "Nash robotics • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g2.jpg", "Swaminarayan Mandir, Nasik • 19 Nov 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g3.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g4.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g5.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g6.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g7.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g8.jpg", "Swaminarayan Mandir, Nasik • 19 Nov 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g9.jpg", "Swaminarayan Mandir, Nasik • 19 Nov 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g10.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g11.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g12.jpg", "Swaminarayan Mandir, Nasik • 19 Nov 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g13.jpg", "Swaminarayan Mandir, Nasik • 19 Nov 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g14.jpg", "Swaminarayan Mandir, Nasik • 19 Nov 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Nasik (Nash robotics)/g15.jpg", "Nash robotics, Nasik • 19 Nov 2023", "field visit"),

                new Item(getString(R.string.host) + "memories/verul/g24.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g23.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g22.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g21.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g20.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g19.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g18.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g17.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g16.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g15.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g14.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g13.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g12.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g11.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g10.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g9.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g8.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g7.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g6.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g5.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g4.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g3.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g2.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/verul/g1.jpg", "Ajanta Verul caves • 29 April 2023", "trip"),
                new Item(getString(R.string.host) + "memories/Yugotsav 2k23/g4.jpg", "Yugotsav 2k23 • 27 Mar 2023", "gathering"),
                new Item(getString(R.string.host) + "memories/Yugotsav 2k23/g3.jpg", "Yugotsav 2k23 • 27 Mar 2023", "gathering"),
                new Item(getString(R.string.host) + "memories/Yugotsav 2k23/g2.jpg", "Yugotsav 2k23 • 27 Mar 2023", "gathering"),
                new Item(getString(R.string.host) + "memories/Yugotsav 2k23/g1.jpg", "Yugotsav 2k23 • 27 Mar 2023", "gathering"),
                new Item(getString(R.string.host) + "memories/Yugotsav 2k23/d2.jpg", "Yugotsav 2k23 • 29 Mar 2023", "gathering"),
                new Item(getString(R.string.host) + "memories/Yugotsav 2k23/d1.jpg", "Yugotsav 2k23 • 28 Mar 2023", "gathering"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g1.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g2.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g3.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g4.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g5.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g6.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g7.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g8.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g9.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g10.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g12.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g13.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g14.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/manmad workshop/g15.jpg", "Railway workshop, Manmad • 24 Feb 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/g1.jpg", "Girls Squad • 20 Jan 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/g2.jpg", "Sona Pipes Pvt Ltd. • 14 Jan 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/g3.jpg", "Sona Pipes Pvt Ltd. • 14 Jan 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/g4.jpg", "Sona Pipes Pvt Ltd. • 14 Jan 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/g5.jpg", "Sona Pipes Pvt Ltd. • 14 Jan 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/g6.jpg", "Sona Pipes Pvt Ltd. • 14 Jan 2023", "field visit"),
                new Item(getString(R.string.host) + "memories/u1.jpg", "International Event • 10 Jan 2023", "event"),
                new Item(getString(R.string.host) + "memories/u2.jpg", "International Event • 10 Jan 2023", "event"),
                new Item(getString(R.string.host) + "memories/u3.jpg", "International Event • 10 Jan 2023", "event"),
                new Item(getString(R.string.host) + "memories/FY/g1.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g2.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g3.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g4.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g5.jpg", "FY Annual fest • 01 Jun 2022", "other"),
                new Item(getString(R.string.host) + "memories/FY/g6.jpg", "FY Annual fest • 01 Jun 2022", "other"),
                new Item(getString(R.string.host) + "memories/FY/g7.jpg", "FY Annual fest • 01 Jun 2022", "other"),
                new Item(getString(R.string.host) + "memories/FY/g8.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g9.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g10.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g11.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g12.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g13.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g14.jpg", "FY Annual fest • 01 Jun 2022", "gathering"),
                new Item(getString(R.string.host) + "memories/FY/g15.jpg", "FY Annual fest • 01 Jun 2022", "gathering")
        );

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean shuffle = pref.getBoolean("shuffle", true);
        if (shuffle){
            Collections.shuffle(items);
        }
        adapter = (ImageAdapter) new ImageAdapter(getContext(), items);

        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0){
                    //scrolling down
                    if (isAppBarExpanded){

                    }
                }
            }
        });

        Chip chipAddMemories = (Chip) view.findViewById(R.id.chipAddMemories);
        chipAddMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadsActivity.class);
                startActivity(intent);
            }
        });

        Chip chipTrip = (Chip) view.findViewById(R.id.chipTrip);
        chipTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setFilterReader(new TripFilterReader());
            }
        });

        Chip chipEvent = (Chip) view.findViewById(R.id.chipEvent);
        chipEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setFilterReader(new EventFilterReader());
            }
        });

        Chip chipGathering = (Chip) view.findViewById(R.id.chipGathering);
        chipGathering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setFilterReader(new GatheringFilterReader());
            }
        });

        Chip chipFieldVisit = (Chip) view.findViewById(R.id.chipFieldVisit);
        chipFieldVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setFilterReader(new FieldVisitFilterReader());
            }
        });

        Chip chipOther = (Chip) view.findViewById(R.id.chipOther);
        chipOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setFilterReader(new OtherFilterReader());
            }
        });

        Chip chipClass = (Chip) view.findViewById(R.id.chipClass);
        chipClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setFilterReader(new ClassFilterReader());
            }
        });

        Extfab = (ExtendedFloatingActionButton) view.findViewById(R.id.uploadtBtn);
        Extfab.setVisibility(View.GONE);


        Extfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        //ImageAdapter imgAdapter = (ImageAdapter) new ImageAdapter((ImageAdapter.OnImageLoadListner) getActivity());

        FirebaseDatabase database = (FirebaseDatabase) FirebaseDatabase.getInstance();
        DatabaseReference dataUploadRef = database.getReference("app-configuration/uploads");
        dataUploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean uploadsValue = snapshot.getValue(Boolean.class);

                if(uploadsValue == true){
                    chipAddMemories.setEnabled(true);
                }else{
                    chipAddMemories.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
    public void upload(){
        String url = "https://forms.gle/K5BJw9wrxaC66BLBA";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData((Uri.parse(url)));
        startActivity(intent);
    }
    public void onImageLoadComplete(){
        progressBar.setVisibility(View.GONE);
    }



}