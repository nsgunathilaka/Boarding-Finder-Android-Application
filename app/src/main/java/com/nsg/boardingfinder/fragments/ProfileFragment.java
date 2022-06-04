package com.nsg.boardingfinder.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.LoginActivity;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Response.ErrorResponse.ErrorResponse;
import com.nsg.boardingfinder.Response.FacilitiesResponse.FacilitiesRes;
import com.nsg.boardingfinder.Response.FacilitiesResponse.Facility;
import com.nsg.boardingfinder.Response.UserResponse.UserProfileRes;
import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppProgressDialog;
import com.nsg.boardingfinder.Utils.AppSharedPreferences;
import com.nsg.boardingfinder.Utils.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment{

    private TextView txtUserName, txtUserRole, txtFullName,txtEmail,txtContactNum;
    private TextView linkLogout;
    private String apiBaseUrl = "";
    private SharedPreferences sharedPre;
    private CircleImageView userImage;
    private MaterialCardView cardViewProfile, cardViewSettings;
    private Button btnGotoSettings, btnGoBack,btnUpdatePassoword;
    private TextInputEditText txtCurrentPassword, txtNewPassword, txtConfirmNewPassword;
    private static Dialog appProgressDialog;
    private static Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre= getActivity().getSharedPreferences("BordingFinPre",0);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtUserName = (TextView) getView().findViewById(R.id.txtUserName);
        txtUserRole = (TextView) getView().findViewById(R.id.txtUserRole);
        txtFullName = (TextView) getView().findViewById(R.id.txtFullName);
        txtEmail = (TextView) getView().findViewById(R.id.txtEmail);
        txtContactNum = (TextView) getView().findViewById(R.id.txtContactNum);
        linkLogout = (TextView) getView().findViewById(R.id.linkLogout);
        userImage = (CircleImageView) getView().findViewById(R.id.userImage);
        btnGotoSettings = (Button) getView().findViewById(R.id.btnGotoSettings);
        btnGoBack = (Button) getView().findViewById(R.id.btnGoBack);
        btnUpdatePassoword = (Button) getView().findViewById(R.id.btnUpdatePassoword);
        cardViewProfile = (MaterialCardView) getView().findViewById(R.id.cardViewProfile);
        cardViewSettings = (MaterialCardView) getView().findViewById(R.id.cardViewSettings);
        appProgressDialog = AppProgressDialog.createProgressDialog(getContext());

        txtCurrentPassword = (TextInputEditText) getView().findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (TextInputEditText) getView().findViewById(R.id.txtNewPassword);
        txtConfirmNewPassword = (TextInputEditText) getView().findViewById(R.id.txtConfirmNewPassword);

        linkLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove user from persistant storage
                if(AppSharedPreferences.getData(sharedPre,"user-accessToken") != null){
                    // Remove user's data
                    AppSharedPreferences.removeData(sharedPre,"user-id");
                    AppSharedPreferences.removeData(sharedPre,"user-email");
                    AppSharedPreferences.removeData(sharedPre,"user-name");
                    AppSharedPreferences.removeData(sharedPre,"user-role");
                    AppSharedPreferences.removeData(sharedPre,"user-avatar");
                    AppSharedPreferences.removeData(sharedPre,"user-accessToken");
                    AppSharedPreferences.removeData(sharedPre,"user-tokenType");
                }
                //Redirect to login
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewProfile.setVisibility(View.VISIBLE);
                btnGotoSettings.setVisibility(View.VISIBLE);
                cardViewSettings.setVisibility(View.GONE);
            }
        });

        getUserAccount();

        btnGotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewProfile.setVisibility(View.GONE);
                btnGotoSettings.setVisibility(View.GONE);
                cardViewSettings.setVisibility(View.VISIBLE);
            }
        });

        btnUpdatePassoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{

                    String newPassword = txtNewPassword.getText().toString().trim();
                    String confirmPassword = txtConfirmNewPassword.getText().toString().trim();

                    if(txtNewPassword.getText().toString().trim().equals(txtConfirmNewPassword.getText().toString().trim())){

                        updatePassword(txtCurrentPassword.getText().toString().trim(), txtConfirmNewPassword.getText().toString().trim());
                    }
                    else{
                        showDialog(context, "Oops...!","Confirm password Not match",()->{});
                    }

                },()->{

                });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    //TODO: Get from API
    public void updatePassword(String oldPswd, String newPswd){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        String loginId ="0";
        if( AppSharedPreferences.getData(sharedPre,"user-id") != null){
            loginId =AppSharedPreferences.getData(sharedPre,"user-id");
        }
        appProgressDialog.show();
        Call<JsonObject> call = apiInterface.updatePassword(loginId,oldPswd, newPswd);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) { // Code 200
                    appProgressDialog.dismiss();
                    showDialog(context, "Oops...!","Successfuly updated..!",()->{});
                    txtCurrentPassword.setText("");
                    txtConfirmNewPassword.setText("");
                    txtNewPassword.setText("");
                    cardViewProfile.setVisibility(View.VISIBLE);
                    btnGotoSettings.setVisibility(View.VISIBLE);
                    cardViewSettings.setVisibility(View.GONE);
                } else { //Code : 400
                    ErrorResponse errResponse = null;
                    try {
                        errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                        showDialog(context, "Oops...!",errResponse.getError(),()->{});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.err.println(errResponse.getError());
                    appProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("_RES_"+ t.getMessage());
                appProgressDialog.dismiss();
            }
        });
    }


    //TODO: Get from API
    public void getUserAccount(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        String loginId ="0";
        if( AppSharedPreferences.getData(sharedPre,"user-id") != null){
           loginId =AppSharedPreferences.getData(sharedPre,"user-id");
        }
        System.out.println("Login ID "+loginId+"");
        appProgressDialog.show();
        Call<JsonObject> call = apiInterface.getUserProfile(loginId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) { // Code 200
                    JsonObject jsonObject = response.body();
                    UserProfileRes userProfRes= new Gson().fromJson(jsonObject, UserProfileRes.class);
                    txtUserName.setText(userProfRes.getLogin().getName());
                    txtUserRole.setText(userProfRes.getLogin().getRole().toUpperCase());
                    txtFullName.setText("FULL NAME: "+ userProfRes.getFullName());
                    txtEmail.setText("EMAIL: "+ userProfRes.getLogin().getEmail());
                    txtContactNum.setText("CONTACT: "+ userProfRes.getPhone());

                    Picasso.get().load(userProfRes.getLogin().getAvatar()).into(userImage);
                    System.out.println("==================================== " + userProfRes.getLogin().getAvatar());
                    appProgressDialog.dismiss();
                } else { //Code : 400
                    System.out.println("================== ERROR while getting user account data ==================");
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
                System.out.println("_RES_"+ t.getMessage());
                appProgressDialog.dismiss();
                //   Toast.makeText(getActivity(), "Could Load data..reload page!", Toast.LENGTH_SHORT).show();
            }
        });
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