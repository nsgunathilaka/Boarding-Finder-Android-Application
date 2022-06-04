package com.nsg.boardingfinder.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Response.ErrorResponse.ErrorResponse;
import com.nsg.boardingfinder.Response.FacilitiesResponse.FacilitiesRes;
import com.nsg.boardingfinder.Response.FacilitiesResponse.Facility;

import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppProgressDialog;
import com.nsg.boardingfinder.Utils.RetrofitClient;
import com.nsg.boardingfinder.adapter.FacilitiesRecyclerviewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FacilityListFragment extends Fragment {

    private static TextView txtPostListCardSubtitle;
    private TextInputEditText txtSearch;
    private Button btnAddNew;

    private static String apiBaseUrl = "";
    SharedPreferences sharedPre;
    private static Dialog appProgressDialog;
    private static Dialog appCustomUpdateDialog;
    private Dialog appCustomDialog;
    String postStatus="";
    String loginId ="0";
    private static Context context;

    private static RecyclerView userRecycler;
    private static FacilitiesRecyclerviewAdapter recyclerviewAdapter;
    CharSequence search="";
    private static List<Facility> postDataList = new ArrayList<>();

    public FacilityListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre= getActivity().getSharedPreferences("BordingFinPre",0);
        appProgressDialog = AppProgressDialog.createProgressDialog(getContext());

        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        context = getContext();
        appProgressDialog = AppProgressDialog.createProgressDialog(context);

        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_form_facility);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        TextInputEditText txtFacilityName = appCustomDialog.findViewById(R.id.txtFacilityName);
        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);
        labelFormHeading.setText("Add New Facility");
        btn_dialog_btnAdd.setText("Save");

        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtFacilityName.getText().toString().trim().equals("")){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{
                    //TODO: Display Confirm Dialog
                    showConfirmDialog(getActivity(),() -> {
                        //TODO: confirmCall
                        String vacName = txtFacilityName.getText().toString().trim();
                        txtFacilityName.setText("");
                        appCustomDialog.dismiss();
                        submitFacilityData(vacName);
                    }, ()->{
                        //TODO: cancelCall
                    });
                }
            }
        });
        // TODO: Custom Dialog End ::::::::::::::::::::::::::::::::::::::::::
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facility_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddNew = (Button) getView().findViewById(R.id.btnAddNew);
        txtPostListCardSubtitle = (TextView) getView().findViewById(R.id.txtPostListCardSubtitle);
        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);
        userRecycler = getView().findViewById(R.id.userRecycler);

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

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });

        getAllFacilities();
    }


    //TODO : caller is locateed @ VaccineRecyclerviewAdapter.java
    public static void deleteItem(String id){
        showConfirmDialog(context, ()->{
            //TODO: confirm
            ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.deleteFacility(id);
            appProgressDialog.show();
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    if (response.isSuccessful()) { //Response code : 200
                        showDialog(context,"Successfull!", "Facility deleted.",()->{
                        });
                       getAllFacilities();
                        appProgressDialog.dismiss();
                    }else{ //Response code : 400
                        ErrorResponse errResponse = null;
                        try {
                            errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                            showDialog(context, "Opps..!", errResponse.getError(), () -> {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        appProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    showDialog(context,"Opps...!", "Could not connect \n Check Internet Connection",()->{});
                    System.out.println("_==================Error! Could not Access my API  ==================");
                    appProgressDialog.dismiss();
                }
            });
        },()->{});

    }

    public static void updateItem(String id, String facility){
        showUpdateDialog(id, facility);
    }


    public static void updateFaclityData(String id, String facility){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.updateFacility(id,facility);
        appProgressDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (response.isSuccessful()) { //Response code : 200
                    showDialog(context,"Successfull!", "Facility data updated.",()->{});
                    //TODO: Take latest data
                    getAllFacilities();
                    appProgressDialog.dismiss();
                }else{ //Response code : 400

                    ErrorResponse errResponse = null;
                    try {
                        errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                        showDialog(context,"Opps...!", errResponse.getError(),()->{});
                    } catch (IOException e) {
                        e.printStackTrace();
                        showDialog(context,"Opps...!", "Cannot save, Try again",()->{});
                    }
                    appProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showDialog(context,"Opps...!", "Could not connect \n Check Internet Connection",()->{});
                System.out.println("_==================Error! Could not Access my API  ==================");
                appProgressDialog.dismiss();
            }
        });
    }


    private static void setUserRecycler(List<Facility> postDataList){
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        userRecycler.setLayoutManager(layoutManager);
        recyclerviewAdapter = new FacilitiesRecyclerviewAdapter(context, postDataList);
        userRecycler.setAdapter(recyclerviewAdapter);
    }

    //TODO: Get from API
    public static void getAllFacilities() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.getAllFacilities();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) { // Code 200

                    JsonObject jsonObject = response.body();
                    FacilitiesRes facilitiesRes = new Gson().fromJson(jsonObject, FacilitiesRes.class);

                    List<Facility> facilityList = facilitiesRes.getData();
                    for(Facility item:facilityList){
                        postDataList.add(item);
                    }

                    if(facilitiesRes.getData() != null){
                        if(facilitiesRes.getData().size()>0){
                            txtPostListCardSubtitle.setText("Facilities Count " + facilitiesRes.getData().size() + " ");
                        }else{
                            txtPostListCardSubtitle.setText("No facilities to show");
                        }
                        if(facilitiesRes.getData().size()==0){
                            txtPostListCardSubtitle.setText("No facilities to show");
                        }

                        if(postDataList != null)
                            postDataList.clear();
                        for(Facility item:facilityList){
                            postDataList.add(item);
                        }

                        if(postDataList != null)
                            if(postDataList.size()>0)
                                setUserRecycler(postDataList);
                    }else{
                        txtPostListCardSubtitle.setText("You haven't any facility");
                    }
                    appProgressDialog.dismiss();
                } else { //Code : 400
                    System.out.println("================== ERROR while getting AllFacilities ==================");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("_RES_" + t.getMessage());
                //   Toast.makeText(getActivity(), "Could Load data..reload page!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitFacilityData(String facName){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.addFacility(facName);

        appProgressDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (response.isSuccessful()) { //Response code : 200
                    showDialog(context,"Successfull!", "Facility saved.",()->{
                        //TODO: get latest data from db
                       getAllFacilities();
                    });
                }else{ //Response code : 400

                    ErrorResponse errResponse = null;
                    try {
                        errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                        showDialog(context,"Opps...!", errResponse.getError(),()->{});
                    } catch (IOException e) {
                        e.printStackTrace();
                        showDialog(context,"Opps...!", "Cannot save, Try again",()->{});
                    }

                }
                appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                appProgressDialog.dismiss();
                showDialog(context,"Opps...!", "Could not connect \n Check Internet Connection",()->{});
                System.out.println("_==================Error! Could not Access my API  ==================");
            }
        });
    }



    private static void showUpdateDialog(String id, String facility){
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomUpdateDialog = new Dialog(context);

        // >> TODO: Add Dialog Layout
        appCustomUpdateDialog.setContentView(R.layout.dialog_layout_form_facility);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomUpdateDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.alert_background));
        }
        appCustomUpdateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomUpdateDialog.setCancelable(false);
        appCustomUpdateDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        TextView labelFormHeading = appCustomUpdateDialog.findViewById(R.id.labelFormHeading);
        TextInputEditText txtFacilityName = appCustomUpdateDialog.findViewById(R.id.txtFacilityName);
        Button btn_dialog_btnCancel = appCustomUpdateDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomUpdateDialog.findViewById(R.id.btn_dialog_btnAdd);
        labelFormHeading.setText("Add New Facility");

        labelFormHeading.setText("Update Facility");
        btn_dialog_btnAdd.setText("Update");
        txtFacilityName.setText(facility);

        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomUpdateDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtFacilityName.getText().toString().trim().equals("") ){
                    showDialog(context,"Opps..!", "All fields are required.", ()->{});
                }else{
                    //TODO: Display Confirm Dialog
                    showConfirmDialog(context,() -> {
                        //TODO: confirmCall
                        String facName = txtFacilityName.getText().toString().trim();
                        txtFacilityName.setText("");
                        appCustomUpdateDialog.dismiss();
                        updateFaclityData(id ,facName);
                    }, ()->{
                        //TODO: cancelCall
                    });
                }
            }
        });
        appCustomUpdateDialog.show();
        // TODO: Custom Dialog End ::::::::::::::::::::::::::::::::::::::::::
    }


    public static  void showDialog(
            @NonNull final Context context,
            String title,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }

    public static  void showConfirmDialog(
            @NonNull final Context context,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure ?")
                .setMessage("Are you sure to continue this task")
                .setCancelable(false)
                .setPositiveButton("Confirm",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Cancel",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
    }
}