package com.nsg.boardingfinder.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.LoginActivity;
import com.nsg.boardingfinder.OwnerActivity;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.RegisterActivity;
import com.nsg.boardingfinder.Response.ErrorResponse.ErrorResponse;
import com.nsg.boardingfinder.Response.FacilitiesResponse.FacilitiesRes;
import com.nsg.boardingfinder.Response.FacilitiesResponse.Facility;
import com.nsg.boardingfinder.Response.SuccessResponse.DataResponse;
import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppProgressDialog;
import com.nsg.boardingfinder.Utils.AppSharedPreferences;
import com.nsg.boardingfinder.Utils.FileUtils;
import com.nsg.boardingfinder.Utils.RetrofitClient;
import com.nsg.boardingfinder.Utils.SearchModel;
import com.nsg.boardingfinder.Utils.TabAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class NewBoardingFragment extends Fragment {

    private static String apiBaseUrl = "";
    private static Context context;
    private static SharedPreferences sharedPre;
    private static Dialog appProgressDialog;

    private Button textButtonNext, textButtonCancel;

    private Dialog appAlertDialog;
    private Button appAlertButton;
    private TextView txtAlertTitle, txtAlertDesc;
    StringBuilder errFields;

    private ChipGroup chip_group_choice;
    private Chip chip_male, chip_female, chip_location, chip_pick_location, chip_facilities, chip_image;
    private TextInputEditText txtTitle, txtPrice, txtCategory, txtDescription, txt_latitude, txt_longitude;

    private static String checkedGender = "", selectedLocation = "";
    private static List<Integer> facilitiesList = new ArrayList<>();

    private boolean[] selectedFacilities;
    private Integer[] facilityIDsArray;
    private String[] facilityNamesArray;
    private ArrayList<Integer> listFacilityOutcome = new ArrayList<>();
    private CharSequence[] options = {"Camera", "Gallery", "Cancel"};

    private String selectedImage = "";
    private String fileDialogSelectedOption = "";

    //TODO: For select image
    private ActivityResultLauncher<Intent> activityResultLaunch;
    //TODO: For Pick Location
    public static ActivityResultLauncher<Intent> activityResultLaunchPickLocation;

    //TODO: For Payment Method
    public ActivityResultLauncher<Intent> activityResultLaunch_payment;
    public static boolean isPaymentSuccess = false;

    public NewBoardingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre = getActivity().getSharedPreferences("BordingFinPre", 0);
        appProgressDialog = AppProgressDialog.createProgressDialog(getContext());

        activityResultLaunchPickLocation = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {


                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Place place = PlacePicker.getPlace(data, context);
                            StringBuilder stringBuilder = new StringBuilder();
                            String latitude = String.valueOf(place.getLatLng().latitude);
                            String longitude = String.valueOf(place.getLatLng().longitude);
                            System.out.println("=============== LON LAT : " + latitude + "  " + longitude);
                            chip_location.setText("Change location");
                            txt_longitude.setText(longitude);
                            txt_latitude.setText(latitude);
                        }else{
                            if(txt_longitude.getText().toString().equals("")){
                                chip_location.setText("Pick location");
                            }
                        }
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        System.out.println("====================ResultCode " + result.getResultCode());
                        if (result.getResultCode() == -1) {
                            switch (fileDialogSelectedOption) {
                                case "Camera":
                                    //TODO : do stuff when image captured by camera
                                    Bitmap image = (Bitmap) result.getData().getExtras().get("data");
                                    selectedImage = FileUtils.getPath(getActivity(), getImageUri(getActivity(), image));
                                    //imageView.setImageBitmap(image);  // <-- Show image in ImageView
                                    chip_image.setText("Image selected");
                                    break;
                                case "Gallery":
                                    // TODO : do stuff when image Selected from gallery
                                    Uri imageUri = result.getData().getData();
                                    selectedImage = FileUtils.getPath(getActivity(), imageUri);
                                    chip_image.setText("Image selected");
                                    //Picasso.get().load(image).into(imageView); // <-- Show image in ImageView
                                    break;
                            }
                        } else if (result.getResultCode() == 0) { // Not selected
                            // TODO : do stuff when image not Selected
                            chip_image.setText("Image");
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_boarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textButtonNext = (Button) getView().findViewById(R.id.textButtonNext);
        textButtonCancel = (Button) getView().findViewById(R.id.textButtonCancel);

        textButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Identify Current Tab of post creation tabs
                if (AppSharedPreferences.getData(sharedPre, "user-id") == null) {
                    showAppDialog("Session out!", "Please Do Login again");
                }else{
                    if (!validateDetailsForm()) {
                        //TODO: show error dialog
                        showAppDialog("Following fields also Required!", errFields.toString());
                    } else {
                        //TODO: Allow user to make a Payment
                        initiatePaymentRequest();
                    }
                }
            }
        });

        textButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restCreatePostForm();
            }
        });

        //TODO: Alert Dialog Start
        appAlertDialog = new Dialog(getActivity());
        appAlertDialog.setContentView(R.layout.app_alert);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appAlertDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.background));
        }
        appAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        appAlertDialog.setCancelable(false);
        appAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txtAlertTitle = appAlertDialog.findViewById(R.id.txtAlertTitle);
        txtAlertDesc = appAlertDialog.findViewById(R.id.txtAlertDesc);
        appAlertButton = appAlertDialog.findViewById(R.id.btnContinue);
        appAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appAlertDialog.dismiss();
            }
        });
        // Alert Dialog End

        chip_male = (Chip) getView().findViewById(R.id.chip_male);
        chip_female = (Chip) getView().findViewById(R.id.chip_female);
        chip_location = (Chip) getView().findViewById(R.id.chip_location);
        chip_pick_location = (Chip) getView().findViewById(R.id.chip_pick_location);
        chip_location.setText("Pick location");
        chip_facilities = (Chip) getView().findViewById(R.id.chip_facilities);
        chip_image = (Chip) getView().findViewById(R.id.chip_image);
        txtTitle = (TextInputEditText) getView().findViewById(R.id.txtTitle);
        txtPrice = (TextInputEditText) getView().findViewById(R.id.txtPrice);
        txtCategory = (TextInputEditText) getView().findViewById(R.id.txtCategory);
        txtDescription = (TextInputEditText) getView().findViewById(R.id.txtDescription);
        txt_longitude = (TextInputEditText) getView().findViewById(R.id.txt_longitude);
        txt_latitude = (TextInputEditText) getView().findViewById(R.id.txt_latitude);
        chip_group_choice = (ChipGroup) getView().findViewById(R.id.chip_group_choice);
        chip_group_choice.setSingleSelection(true); //required for working as a radio btn, setOnCheckedChangeListener

        chip_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chip_male.setChipBackgroundColorResource((R.color.colorAppPrimary));
                chip_female.setChipBackgroundColorResource((R.color.colorGray));
                checkedGender = "Male";
            }
        });

        chip_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chip_female.setChipBackgroundColorResource((R.color.colorAppPrimary));
                chip_male.setChipBackgroundColorResource((R.color.colorGray));
                checkedGender = "Female";
            }
        });

        chip_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(getActivity(), "Search...",
                        "What are you looking for...?", null, getLocaitonData(),
                        new SearchResultListener<SearchModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SearchModel item, int position) {
                                // If filtering is enabled, [position] is the index of the item in the filtered result, not in the unfiltered source
                                //   Log.d("_location_", item.getTitle().toString() );
                                chip_location.setText(item.getTitle());
                                selectedLocation = item.getTitle();
                                System.out.println("=============== " + selectedLocation);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        chip_pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    activityResultLaunchPickLocation.launch(builder.build(getActivity()));
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        getAllFacilities();

        chip_facilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFacilities != null) {
                    facilitiesDialog();
                } else {
                    Toast.makeText(getActivity(), "No Facilities to choose", Toast.LENGTH_SHORT).show();
                }
                //   uploadFileToServer();
            }
        });

        chip_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requirePermission();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        activityResultLaunch_payment = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        System.out.println("====================ResultCode " + result.getResultCode());
                        if (result.getData() != null && result.getData().hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {

                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) result.getData().getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                            if (result.getResultCode() == Activity.RESULT_OK) {
                                String msg;
                                if (response != null) {
                                    if (response.isSuccess()) { //TODO: Payment Success
                                        isPaymentSuccess = true;
                                        uploadPostToServer();
                                        msg = " Payment Success Activity result:" + response.getData().toString();
                                    } else {
                                        msg = "Result:" + response.toString();
                                        paymentFailed();
                                    }
                                } else {
                                    paymentFailed();
                                    msg = "Result: no response";
                                    System.out.println("======================= " + msg);
                                }
                            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                                paymentFailed();
                                if (response != null)
                                    System.out.println("======================= RESULT_CANCELED :  " + response.toString());
                                else
                                    System.out.println("======================= User canceled the request");
                            }
                        }
                    }
                });
    }

    private void paymentFailed() {
        isPaymentSuccess = false;
        showPaymentErrorDialog(context, "Can't Submit", "Can't Submit post \nDue Payment is not successded\nYou can retry",
                () -> {
                // try again
                    initiatePaymentRequest();
                },
                () -> {
                // will pay manual
                    isPaymentSuccess = true;
                    uploadPostToServer();
                },
                () -> {
                // cancel post
                    isPaymentSuccess = false;
                });

        //maunal confrm cancel
    }

    StringBuilder postFacilitiesBuilder;

    private String getPostFacilities() {
        postFacilitiesBuilder = null;
        postFacilitiesBuilder = new StringBuilder("");
        List<Integer> listFacilities = facilitiesList;
        for (int i = 0; i < listFacilities.size(); i++) {
            postFacilitiesBuilder.append(String.valueOf(listFacilities.get(i)));
            if (i != (listFacilities.size() - 1)) {
                postFacilitiesBuilder.append(",");
            }
        }
        return postFacilitiesBuilder.toString();
    }


    // TODO: validate Details Form @CreatePostStepOneFragment
    private boolean validateDetailsForm() {
        boolean isFormValid = true;
        errFields = null;
        errFields = new StringBuilder("\n");
        if (txtTitle.toString().trim().equals("")) {
            errFields.append("Post Title \n");
            isFormValid = false;
        }
        if (txtPrice.getText().toString().trim().equals("")) {
            errFields.append("Boarding Price \n");
            isFormValid = false;
        }
        if (checkedGender.equals("")) {
            errFields.append("Gender \n");
            isFormValid = false;
        }
        if (txtCategory.equals("")) {
            errFields.append("Category \n");
            isFormValid = false;
        }
        if (txtDescription.toString().trim().equals("")) {
            errFields.append("Description \n");
            isFormValid = false;
        }
        if (selectedLocation.equals("")) {
            errFields.append("Location \n");
            isFormValid = false;
        }
        if (selectedImage.equals("")) {
            errFields.append("Image \n");
            isFormValid = false;
        }
        if (facilitiesList.size() == 0) {
            errFields.append("Facilities \n");
            isFormValid = false;
        }
        return isFormValid;
    }

    public void uploadPostToServer() {
        String userId = AppSharedPreferences.getData(sharedPre, "user-id");

        //TODO: get selectedImage from Chid-Fragment : this.createPostStepOneFrgmt.getPostImage()
        MultipartBody.Part filePart = null;
        if(!selectedImage.equals("")){
            File file = new File(Uri.parse(selectedImage).getPath());
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            filePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
            // System.out.println("getTotalSpace " + file.getTotalSpace() + "\n" + file.getName());
            System.out.println("=========== Image selected ========");
        }else {
            //Default image fill set to this profile
            System.out.println("=========== Image Not selected ========");
        }

        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), txtTitle.getText().toString().trim());
        RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), txtPrice.getText().toString().trim());
        RequestBody location = RequestBody.create(MediaType.parse("multipart/form-data"), selectedLocation);

        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"), txt_longitude.getText().toString().trim());
        RequestBody latitude = RequestBody.create(MediaType.parse("multipart/form-data"), txt_latitude.getText().toString().trim());

        RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), txtCategory.getText().toString().trim());
        RequestBody accommodaterId = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody gender = RequestBody.create(MediaType.parse("multipart/form-data"), checkedGender);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), txtDescription.getText().toString().trim());
        RequestBody facilities = RequestBody.create(MediaType.parse("multipart/form-data"), getPostFacilities());

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.addBoarding(filePart, title, price, location, longitude, latitude, category, accommodaterId, gender, description, facilities);
        appProgressDialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // TODO : response.code() , response.body() , response.errorBody()
                if (response.isSuccessful()) { //TODO: Response code : 200
                    try {
                        DataResponse dataResponse = new Gson().fromJson(response.body().string(), DataResponse.class);
                        System.out.println("================== RESPONSE ==================");
                        System.out.println(dataResponse.getData());
                        if (dataResponse.getData().equalsIgnoreCase("New boarding has been added!")) {
                            txtTitle.setText("");
                            txtPrice.setText("");
                            txtCategory.setText("");
                            txtDescription.setText("");
                            txt_longitude.setText("");
                            txt_latitude.setText("");

                            showDialog(context, "Thank you!!", "Your post has been successfully submitted! \nit will publish as soon as admin's approval.", () -> {
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appProgressDialog.dismiss();
                } else { //TODO: Response code : 400
                    ErrorResponse errResponse = null;
                    try {
                        errResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                        showDialog(context, "ERROR!", errResponse.getError(), () -> {
                        });
                        System.out.println("==================ERR RESPONSE ==================" + errResponse.getError());

                        System.out.println(errResponse.getError());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //TODO: t.getMessage()
                Log.d("_Reponse_Body_", t.getMessage().toString());
                appProgressDialog.dismiss();
            }
        });
    }

    private void showAppDialog(String alertTitle, String alertDescription) {
        txtAlertTitle.setText(alertTitle);
        txtAlertDesc.setText(alertDescription);
        appAlertDialog.show();
    }

    //TODO: MaterialAlertDialog
    public static void showConfirmDialog(
            @NonNull final Context context,
            String title,
            String msg,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Try to pay again",
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

    public static void showDialog(
            @NonNull final Context context,
            String title,
            String msg,
            @Nullable Runnable confirmCallback
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }

    public static void showPaymentErrorDialog(
            @NonNull final Context context,
            String title,
            String msg,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable manualPayCallback,
            @Nullable Runnable cancelCallback
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Try to pay again",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setPositiveButton("Pay later",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (manualPayCallback != null) manualPayCallback.run();
                        })
                .setNeutralButton("Cancel Post",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
    }


    public Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "myImage", "");
        return Uri.parse(path);
    }

    public void requirePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private ArrayList<SearchModel> getLocaitonData() {
        List<String> listCity = Arrays.asList(getResources().getStringArray(R.array.Spinner_items));
        ArrayList<SearchModel> items = new ArrayList<>();
        for (String city : listCity) {
            items.add(new SearchModel(city));
        }
        return items;
    }

    //TODO: Get from API
    public void getAllFacilities() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.getAllFacilities();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) { // Code 200
                    JsonObject jsonObject = response.body();
                    FacilitiesRes facilitiesRes = new Gson().fromJson(jsonObject, FacilitiesRes.class);

                    System.out.println("==================getAllFacilities==================");
                    // facilitiesRes.getData().forEach(System.out::println);
                    List<Facility> facilityList = facilitiesRes.getData();

                    selectedFacilities = new boolean[facilitiesRes.getData().size()];
                    facilityIDsArray = new Integer[facilitiesRes.getData().size()];
                    facilityNamesArray = new String[facilitiesRes.getData().size()];

                    for (int i = 0; i < facilityList.size(); i++) {
                        facilityIDsArray[i] = facilityList.get(i).getId();
                        facilityNamesArray[i] = facilityList.get(i).getFacility();
                    }

                } else { //Code : 400
                    System.out.println("================== ERROR while getting AllFacilities ==================");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("_RES_" + t.getMessage());
            }
        });
    }

    private void facilitiesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Set title
        builder.setTitle("Choose Facilities");
        builder.setCancelable(false);

        //      setMultiChoiceItems (items, checkedItems, ...)
        builder.setMultiChoiceItems(facilityNamesArray, selectedFacilities, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Check condition
                if (isChecked) {
                    listFacilityOutcome.add(which);
                    Collections.sort(listFacilityOutcome);
                } else {
                    listFacilityOutcome.remove(Integer.valueOf(which));
                }
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Feed facilitiesList to make API call
                facilitiesList.clear();
                for (int j = 0; j < listFacilityOutcome.size(); j++) {
                    if (!facilitiesList.contains(facilityIDsArray[listFacilityOutcome.get(j)])) {
                        facilitiesList.add(facilityIDsArray[listFacilityOutcome.get(j)]);
                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int j = 0; j < selectedFacilities.length; j++) {
                    //  Remove all selection
                    selectedFacilities[j] = false;
                    //Clear day list
                    listFacilityOutcome.clear();
                }

                facilitiesList.clear();

            }
        });
        // Show dialog
        builder.show();
    }

    public void restCreatePostForm(){
        txtTitle.setText("");
        txtPrice.setText("");
        txtCategory.setText("");
        txtDescription.setText("");
        selectedLocation="";
        chip_location.setText("Location");
        chip_image.setText("Image");

        for (int j = 0; j < selectedFacilities.length; j++) {
            //  Remove all selection
            selectedFacilities[j] = false;
            //Clear day list
            listFacilityOutcome.clear();
        }
        facilitiesList.clear();
    }

    private void initiatePaymentRequest() {
        InitRequest req = new InitRequest();
        req.setMerchantId("1219440");       // Your Merchant PayHere ID
        //req.setMerchantSecret("ugknesnvn"); // Your Merchant secret (Add your app at Settings > Domains & Credentials, to get this))
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(1000.00);             // Final Amount to be charged
        req.setOrderId("230000123");        // Unique Reference ID
        req.setItemsDescription("Door bell wireless");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Saman");
        req.getCustomer().setLastName("Perera");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(context, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

        activityResultLaunch_payment.launch(intent);
    }
}