package com.nsg.boardingfinder;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.nsg.boardingfinder.Response.ErrorResponse.ErrorResponse;
import com.nsg.boardingfinder.Response.SuccessResponse.DataResponse;
import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppProgressDialog;
import com.nsg.boardingfinder.Utils.FileUtils;
import com.nsg.boardingfinder.Utils.RetrofitClient;
import com.nsg.boardingfinder.Utils.SearchModel;
import com.nsg.boardingfinder.control.InputValidation;
import com.nsg.boardingfinder.model.User;
import com.nsg.boardingfinder.database.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +  //at least 1 digit
                    "(?=.*[a-z])" + //at least 1 lower case letter
                    "(?=.*[A-Z])" + //at least 1 upper case letter
                    // "(?=.*[a-zA-Z])" + //any letter
                    "(?=.*[@#$%^&+=])" + //at least 1 special character
                    "(?=\\S+$)" + //no white spaces
                    ".{6,}" + //at least 6 characters
                    "$"); //end of the string


    public final RegisterActivity regActivity = RegisterActivity.this;
    private DatabaseHelper dbHelper;
    private User user;
    private EditText name,email,phone,address,password,rePassword;

    private Dialog appAlertDialog;
    private Button appAlertButton;
    private TextView txtAlertTitle, txtAlertDesc;

    private CircularProgressButton btn_Reg;
    private ImageView slideButton;
    private TextView goToLogin;
    private CircleImageView userImage;

    private InputValidation inputValidation;
    private Context actContext;
    RadioGroup genderRadioGroup,occupationRadioGroup;
    private RadioButton genderRadioButton,occupationRadioButton;
    private Chip chip_location;

    String userRole;
    private static boolean isRegisterSuccess=false;
    String apiBaseUrl="";

    CharSequence[] options = {"Camera", "Gallery", "Cancel"};
    public ActivityResultLauncher<Intent> activityResultLaunch;
    public String selectedImage = "";
    public String fileDialogSelectedOption = "";
    private Dialog appProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        appProgressDialog = AppProgressDialog.createProgressDialog(RegisterActivity.this);
        actContext = this.getApplicationContext();
        initView();
        initObject();
        initListener();

        userRole = getIntent().getStringExtra("userRole");
        chip_location  = findViewById(R.id.chip_location);

        userImage = (CircleImageView) findViewById(R.id.userImage);

        // Alert Dialog Start
        appAlertDialog = new Dialog(RegisterActivity.this);
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

        /**
         * Redirect to Login
         * Alert Close
         */
        appAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appAlertDialog.dismiss();
                if(isRegisterSuccess){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(actContext, LoginActivity.class));
                            finish();
                        }
                    }, 1000 ); // 1.0 sec delay before execute run()
                }
            }
        });
        // Alert Dialog End


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requirePermission();

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Select Image");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals("Camera")) {
                            Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            activityResultLaunch.launch(takePic);
                            fileDialogSelectedOption = "Camera";
                        } else if (options[which].equals("Gallery")) {
                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLaunch.launch(gallery);
                            fileDialogSelectedOption = "Gallery";
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == -1) {
                            switch (fileDialogSelectedOption) {
                                case "Camera":
                                    //TODO : do stuff when image captured by camera
                                    Bitmap image = (Bitmap) result.getData().getExtras().get("data");
                                    selectedImage = FileUtils.getPath(RegisterActivity.this, getImageUri(RegisterActivity.this, image));
                                    userImage.setImageBitmap(image);  // <-- Show image in ImageView
                                    break;
                                case "Gallery":
                                    // TODO : do stuff when image Selected from gallery
                                    Uri imageUri = result.getData().getData();
                                    selectedImage = FileUtils.getPath(RegisterActivity.this, imageUri);
                                    userImage.setImageURI(imageUri);
                                    //Picasso.get().load(image).into(imageView); // <-- Show image in ImageView
                                    break;
                            }
                        } else if (result.getResultCode() == 0) { // Not selected
                            // TODO : do stuff when image not Selected

                        }
                    }
                });
    }

    private void initView(){

        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        phone = findViewById(R.id.txtMobile);
        address = findViewById(R.id.txtAddress);
        password = findViewById(R.id.txtPassword);
        rePassword = findViewById(R.id.txtRePassword);

        btn_Reg= findViewById(R.id.btnReg);

        genderRadioGroup = findViewById(R.id.gender_radio);
        occupationRadioGroup = findViewById(R.id.occupation_radio);

        int selectedGenderID = genderRadioGroup.getCheckedRadioButtonId();
        genderRadioButton = findViewById(selectedGenderID);

        int selectedOccupationID = occupationRadioGroup.getCheckedRadioButtonId();
        occupationRadioButton = findViewById(selectedOccupationID);
    }

    private void initListener() {
        btn_Reg.setOnClickListener(this);
    }

    private void initObject(){
        inputValidation = new InputValidation(regActivity);
        dbHelper = new DatabaseHelper(regActivity);
        user = new User();
    }


    @Override
    public void onClick(View v) {
        switch ( (v.getId())){
            case R.id.btnReg:
                confirmInput();
                break;
        }
    }

    public void confirmInput() {

        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        phone = findViewById(R.id.txtMobile);
        address = findViewById(R.id.txtAddress);
        password = findViewById(R.id.txtPassword);
        rePassword = findViewById(R.id.txtRePassword);

        if(email.getText().toString().isEmpty()) {
            Toast.makeText(this,"Email is Empty",Toast.LENGTH_LONG).show();
        }
        else if (name.getText().toString().isEmpty()) {
            Toast.makeText(this,"Name is Empty",Toast.LENGTH_LONG).show();
        }
        else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this,"Password is Empty",Toast.LENGTH_LONG).show();
        }
        else if (!password.getText().toString().equals(rePassword.getText().toString())){
            Toast.makeText(this,"Password doesn't match",Toast.LENGTH_LONG).show();
        }
        else if (address.getText().toString().isEmpty()) {
            Toast.makeText(this,"Address is Empty",Toast.LENGTH_LONG).show();
        }
        else if (phone.getText().toString().isEmpty()) {
            Toast.makeText(this,"Phone number is Empty",Toast.LENGTH_LONG).show();
        }
        else{
            btn_Reg.startAnimation();
            appProgressDialog.show();

            MultipartBody.Part filePart = null;
            if(!selectedImage.equals("")){
                File file = new File(Uri.parse(selectedImage).getPath());
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                filePart = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
                // System.out.println("getTotalSpace " + file.getTotalSpace() + "\n" + file.getName());
                System.out.println("=========== Image selected ========");
            }else {
                //Default image fill set to this profile
                System.out.println("=========== Image Not selected ========");
            }


            RequestBody _fullName = RequestBody.create(MediaType.parse("multipart/form-data"), name.getText().toString());
            RequestBody _address  = RequestBody.create(MediaType.parse("multipart/form-data"), address.getText().toString());
            RequestBody _phone = RequestBody.create(MediaType.parse("multipart/form-data"), phone.getText().toString());
            RequestBody _occupation = RequestBody.create(MediaType.parse("multipart/form-data"), occupationRadioButton.getText().toString());
            RequestBody _gender = RequestBody.create(MediaType.parse("multipart/form-data"), genderRadioButton.getText().toString());
            RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), email.getText().toString());
            RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), password.getText().toString());

            ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);
            Call<ResponseBody> call = apiInterface.userRegister(filePart, _fullName,_address,_phone,_gender,_occupation,_email,_password,userRole);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) { //Response code : 200
                        try {
                            DataResponse dataResponse = new Gson().fromJson(response.body().string(), DataResponse.class);
                            System.out.println("================== RESPONSE ==================");
                            System.out.println(dataResponse.getData());


                            if(dataResponse.getData().contains(email.getText().toString()+" has been registered as a "+userRole)){
                                isRegisterSuccess=true;
                                System.out.println("================== contains true ==================");

                                emptyInputEditText();
                                btn_Reg.revertAnimation();
                                txtAlertTitle.setText("Successfully Registered");
                                txtAlertDesc.setText("Active email before login! ");
                                appAlertDialog.show();
                              //  showDialog(actContext,"","",()->{});
                                //Redirection happen at the alert button click
                            }
                        } catch (IOException e) {
                            isRegisterSuccess=false;
                            e.printStackTrace();
                        }
                        appProgressDialog.dismiss();
                    }else{ //Response code : 400
                        isRegisterSuccess=false;
                        btn_Reg.revertAnimation();
                        try {
                            ErrorResponse errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                            System.err.println(errResponse.getError());
                            /**
                             * OPEN CUSTOM ERROR MESSAGE DIALOG BOX :-
                             *
                             * */
                            txtAlertTitle.setText("Oops!");
                            txtAlertDesc.setText(errResponse.getError());
                            appAlertDialog.show();

                            // Toast.makeText(actContext, errResponse.getError(), Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        appProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    appProgressDialog.dismiss();
                    isRegisterSuccess=false;
                    btn_Reg.revertAnimation();
                    //  System.out.println("_Reponse_Body_  "+ t.getMessage());
                    Log.d("_Reponse_Body_", t.getMessage().toString());
                }
            });
        }

    }


    private void emptyInputEditText(){
        name.setText(null);
        email.setText(null);
        password.setText(null);
        rePassword.setText(null);
        address.setText(null);
        phone.setText(null);
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "myImage", "");
        return Uri.parse(path);
    }

    private ArrayList<SearchModel> getLocaitonData(){
        List<String> listCity = Arrays.asList(getResources().getStringArray(R.array.Spinner_items));
        ArrayList<SearchModel> items = new ArrayList<>();
        for(String city: listCity){
            items.add(new SearchModel(city));
        }
        return items;
    }

    public void requirePermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public static void showDialog(
            @NonNull final Context context,
            String heading,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(heading)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }
}