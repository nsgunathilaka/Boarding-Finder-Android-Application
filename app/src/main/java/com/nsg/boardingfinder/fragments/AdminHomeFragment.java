package com.nsg.boardingfinder.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Utils.AppSharedPreferences;

public class AdminHomeFragment extends Fragment {

    SharedPreferences sharedPre;
    TextView tvOwnerName,tvOwnerEmail;
    MaterialCardView cardActivePost, cardPendingPost, cardDeniedPost, cardFacilities;


    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvOwnerName = (TextView) getView().findViewById(R.id.tvOwnerName);
        tvOwnerEmail = (TextView) getView().findViewById(R.id.tvOwnerEmail);
        cardActivePost = (MaterialCardView) getView().findViewById(R.id.cardActivePost);
        cardPendingPost = (MaterialCardView) getView().findViewById(R.id.cardPendingPost);
        cardDeniedPost = (MaterialCardView) getView().findViewById(R.id.cardDeniedPost);
        cardFacilities = (MaterialCardView) getView().findViewById(R.id.cardFacilities);

        sharedPre= getActivity().getSharedPreferences("BordingFinPre",0);
        if(AppSharedPreferences.getData(sharedPre,"user-name") !=null){
            tvOwnerName.setText(AppSharedPreferences.getData(sharedPre, "user-name"));
        }
        if(AppSharedPreferences.getData(sharedPre,"user-email") !=null){
            tvOwnerEmail.setText(AppSharedPreferences.getData(sharedPre, "user-email"));
        }

        cardActivePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PostListFragment("active");
                loadFragment(fragment);
            }
        });

        cardPendingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PostListFragment("pending");
                loadFragment(fragment);
            }
        });

        cardDeniedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PostListFragment("denied");
                loadFragment(fragment);
            }
        });

        cardFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FacilityListFragment();
                loadFragment(fragment);
            }
        });
    }

    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}