package com.nsg.boardingfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class ForgetPasswordActivity extends AppCompatActivity {

    private CircularProgressButton btnReset;
    private EditText txtLoginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        btnReset = (CircularProgressButton) findViewById(R.id.btnReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
    }

    public void forgetPassword(){
        String email_text =  txtLoginEmail.getText().toString().trim();

        if(email_text.equals(""))
        {
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_LONG).show();
        }
        else{
            String apiBaseUrl = getResources().getString(R.string.apiBaseUrl);

            ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.forgetPassword(email_text);
            btnReset.startAnimation();
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    if (response.isSuccessful()) { //Response code : 200
                        btnReset.revertAnimation();
                        txtLoginEmail.setText("");
                        showDialog(ForgetPasswordActivity.this, "Done!","You will receive an email for reset password",()->{
                            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                        });

                    }else{ //Response code : 400
                        System.out.println("_==================Error 400==================");
                        btnReset.revertAnimation();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    btnReset.revertAnimation();
                    System.out.println("_==================Error! Could not Access my API  ==================");
                }
            });
        }
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
}