package com.nsg.boardingfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.Response.AuthResponse.AuthResponse;
import com.nsg.boardingfinder.Response.ErrorResponse.ErrorResponse;
import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppSharedPreferences;
import com.nsg.boardingfinder.Utils.RetrofitClient;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {


    private EditText email;
    private TextView linkGotoRegster, forgetPassword;
    private TextView txtAlertTitle, txtAlertDesc;
    private EditText password;
    private CircularProgressButton cirLoginButton;
    private Dialog dialog;
    private Dialog appAlertDialog;
    private RadioGroup radioGroup;
    private Button appAlertButton;
    private RadioButton radioButton;
    private Button btnCancel,btnContinue;
    private Button cancel,continueBtn;

    private String url = "http:192.168.8.236:8081/api/auth";
    Context actContext;
    SharedPreferences sharedPre;
    String apiBaseUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);

        sharedPre=getSharedPreferences("BordingFinPre",0);
        findById();
        actContext = this.getApplicationContext();

        if(AppSharedPreferences.getData(sharedPre,"user-accessToken") !=null && !AppSharedPreferences.getData(sharedPre,"user-accessToken").equals("")){
            //Redirect to Home
            switch (AppSharedPreferences.getData(sharedPre,"user-role")){
                case "app-client":
                    startActivity(new Intent(actContext, SeekerActivity.class));
                    finish();// Cant came back here after visiting Home page
                    break;
                case "accommodater":
                    startActivity(new Intent(actContext, OwnerActivity.class));
                    finish();// Cant came back here after visiting Home page
                    break;
                case "admin":
                    break;
            }
        }

        // Custom Dialog Start
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.custome_dialog);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        radioGroup = dialog.findViewById(R.id.roleRadioGroup);

        linkGotoRegster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        cancel = dialog.findViewById(R.id.btnCancel);
        continueBtn = dialog.findViewById(R.id.btnContinue);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String role;

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = dialog.findViewById(selectedId);

                if(radioButton.getText().equals("Seeker")) {
                    role = "app-client";
                } else {
                    role = "accommodater";
                }

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("userRole",role);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        // Custom Dialog End

        // Alert Dialog Start
        appAlertDialog = new Dialog(LoginActivity.this);
        appAlertDialog.setContentView(R.layout.app_alert);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appAlertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        }
        appAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appAlertDialog.setCancelable(false);
        appAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txtAlertTitle = appAlertDialog.findViewById(R.id.txtAlertTitle);
        txtAlertDesc  = appAlertDialog.findViewById(R.id.txtAlertDesc);

        appAlertButton = appAlertDialog.findViewById(R.id.btnContinue);
        appAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appAlertDialog.dismiss();
            }
        });
        // Alert Dialog End
    }

    private void findById(){
        email = findViewById(R.id.txtLoginEmail);
        password = findViewById(R.id.txtLoginPassword);
        cirLoginButton= findViewById(R.id.cirLoginButton);
        linkGotoRegster= findViewById(R.id.txtToRegister);
        forgetPassword= (TextView) findViewById(R.id.forgetPassword);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
    }

    public void Login(View v){
        String email_text =  email.getText().toString();
        String password_text =  password.getText().toString();
        if(email_text.equals("") || password_text.equals(""))
        {
            Toast.makeText(this,"All field required",Toast.LENGTH_LONG).show();
        }
        else{

            ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.userLogin(email_text,password_text);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    //Log.d("_Reponse_Code_", String.valueOf(response.code()) );
                    //Log.d("_Reponse_Body_", String.valueOf(response.body()) );

                    if (response.isSuccessful()) { //Response code : 200
                        AuthResponse authResponse=null;
                        System.out.println("_==================isSuccessful TRUE ==================");
                        JsonObject jsonObject = response.body();
                        try {
                            authResponse = new Gson().fromJson(jsonObject, com.nsg.boardingfinder.Response.AuthResponse.AuthResponse.class);
                            System.out.println(authResponse);
                        }catch (Exception e){
                            System.out.println("_================== Exception==================");
                            e.printStackTrace();
                        }

                        if(authResponse!=null){
                            try {
                                //Save User'Data
                                AppSharedPreferences.saveData(sharedPre,"user-id", String.valueOf(authResponse.getUser().getId()));
                                AppSharedPreferences.saveData(sharedPre,"user-email", authResponse.getUser().getEmail());
                                AppSharedPreferences.saveData(sharedPre,"user-name", authResponse.getUser().getName());
                                AppSharedPreferences.saveData(sharedPre,"user-role", authResponse.getUser().getRole());
                                AppSharedPreferences.saveData(sharedPre,"user-avatar", authResponse.getUser().getAvatar());
                                AppSharedPreferences.saveData(sharedPre,"user-accessToken", authResponse.getAccessToken());
                                AppSharedPreferences.saveData(sharedPre,"user-tokenType", authResponse.getTokenType());

                                //Redirect to Home
                                switch (authResponse.getUser().getRole()){
                                    case "app-client":
                                        startActivity(new Intent(actContext, SeekerActivity.class));
                                        finish();// Cant came back here after visiting Home page
                                        break;
                                    case "accommodater":
                                        startActivity(new Intent(actContext, OwnerActivity.class));
                                        finish();// Cant came back here after visiting Home page
                                        break;
                                    case "admin":
                                        startActivity(new Intent(actContext, AdminActivity.class));
                                        finish();// Cant came back here after visiting Home page
                                        break;
                                }

                            }catch (Exception e){
                                System.out.println("_================== Exception appSharedPreferences ==================");
                                e.printStackTrace();
                            }
                        }

                    }else{ //Response code : 400
                        try {
                            ErrorResponse errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                            System.err.println(errResponse.getError());
                            /**
                             * OPEN CUSTOM ERROR MESSAGE DIALOG BOX :-
                             *
                            * */
                            if(errResponse.getError().equalsIgnoreCase("Please verify your email")){
                                txtAlertTitle.setText("Oops!");
                                txtAlertDesc.setText(errResponse.getError());
                            }else{
                                txtAlertTitle.setText("Oops!");
                                txtAlertDesc.setText("Invalid Credentials");
                            }
                            appAlertDialog.show();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("_================== Error! Could not Access my API  ==================");
                }
            });
        }
    }

}