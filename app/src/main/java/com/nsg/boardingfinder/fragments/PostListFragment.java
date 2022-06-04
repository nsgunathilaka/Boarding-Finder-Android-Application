package com.nsg.boardingfinder.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Response.ErrorResponse.ErrorResponse;
import com.nsg.boardingfinder.Response.OwnerPostListResponse.Boadings;
import com.nsg.boardingfinder.Response.OwnerPostListResponse.OwnerPostListResponse;

import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppProgressDialog;
import com.nsg.boardingfinder.Utils.AppSharedPreferences;
import com.nsg.boardingfinder.Utils.RetrofitClient;
import com.nsg.boardingfinder.adapter.RecyclerviewAdapter;
import com.nsg.boardingfinder.model.PostData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostListFragment extends Fragment {

    private TextView txtPostListCardSubtitle, txtPostListCardTitle;
    private TextInputEditText txtSearch;

    String apiBaseUrl = "";
    SharedPreferences sharedPre;
    private static Dialog appProgressDialog;
    static FragmentManager fragmentManager;
    String postStatus="";
    String loginId ="0";

    RecyclerView userRecycler;
    RecyclerviewAdapter recyclerviewAdapter;
    CharSequence search="";
    List<PostData> postDataList = new ArrayList<>();

    public PostListFragment() {
        // Required empty public constructor
    }

    public PostListFragment(String postStatus) {
        this.postStatus= postStatus;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre= getActivity().getSharedPreferences("BordingFinPre",0);
        appProgressDialog = AppProgressDialog.createProgressDialog(getContext());
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtPostListCardSubtitle = (TextView) getView().findViewById(R.id.txtPostListCardSubtitle);
        txtPostListCardTitle = (TextView) getView().findViewById(R.id.txtPostListCardTitle);
        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                recyclerviewAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        if( AppSharedPreferences.getData(sharedPre,"user-role") != null){
            if(AppSharedPreferences.getData(sharedPre,"user-role").equals("accommodater")){
                loginId =AppSharedPreferences.getData(sharedPre,"user-id");
                txtPostListCardTitle.setText("My "+postStatus.toUpperCase()+" Post List");
            }else if(AppSharedPreferences.getData(sharedPre,"user-role").equals("admin")){
                txtPostListCardTitle.setText(postStatus.toUpperCase()+" Post List");
                //TODO: //API will return all owners' posts by given status
                loginId = "0";
            }
        }

        if(!postStatus.equals("")){
            getPostsByStatusAndOwnerId();
        }
    }


    public static void listItemOnClick(String boardingId, String accommodaterId){
        //TODO: Goto View User Fragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new ViewBoardingFragment(boardingId, accommodaterId));
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private  void  setUserRecycler(List<PostData> postDataList){
        userRecycler = getView().findViewById(R.id.userRecycler);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        userRecycler.setLayoutManager(layoutManager);
        recyclerviewAdapter = new RecyclerviewAdapter(getActivity(), postDataList);
        userRecycler.setAdapter(recyclerviewAdapter);
    }


    //TODO: Get from API
    public void getPostsByStatusAndOwnerId(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.getPostsByStatusAndOwnerId(loginId, postStatus);
        appProgressDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) { // Code 200
                    JsonObject jsonObject = response.body();
                    OwnerPostListResponse ownerPostListRes = new Gson().fromJson(jsonObject, OwnerPostListResponse.class);

                    if(ownerPostListRes.getBordings() != null){
                        if(ownerPostListRes.getBordings().size()>0){
                            txtPostListCardSubtitle.setText("You have " + ownerPostListRes.getBordings().size() + " "+ postStatus + " posts");
                        }else{
                            txtPostListCardSubtitle.setText("You haven't any "+ postStatus + " posts");
                        }
                        if(ownerPostListRes.getBordings().size()==0){
                            txtPostListCardSubtitle.setText("You haven't any "+ postStatus + " posts");
                        }

                        if(postDataList != null)
                            postDataList.clear();
                        for(Boadings item: ownerPostListRes.getBordings()){
                            postDataList.add(new PostData(item.getTitle(),("Rs."+item.getPrice()), item.getStatus().toUpperCase(), item.getId(), item.getAccommodaterId() , item.getImage()));
                        }

                        if(postDataList != null)
                            if(postDataList.size()>0)
                                setUserRecycler(postDataList);
                    }else{
                        txtPostListCardSubtitle.setText("You haven't any "+ postStatus + " posts");
                    }
                    appProgressDialog.dismiss();
                } else { //Code : 400
                    System.out.println("==================  owner's post list is EMPTY==================");
                    txtPostListCardSubtitle.setText("You haven't any "+ postStatus + " posts");
                    ErrorResponse errResponse = null;
                    try {
                        errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.err.println(errResponse.getError());
                    appProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                txtPostListCardSubtitle.setText("You haven't any "+ postStatus + " posts");
                System.out.println("_RES_"+ t.getMessage());
                //   Toast.makeText(getActivity(), "Could Load data..reload page!", Toast.LENGTH_SHORT).show();
                appProgressDialog.dismiss();
            }
        });
    }

}