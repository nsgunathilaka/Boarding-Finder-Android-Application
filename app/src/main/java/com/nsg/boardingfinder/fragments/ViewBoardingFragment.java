package com.nsg.boardingfinder.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.FullScreenImgActivity;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Response.BoardingByIdRes.BoardingByIdResponse;
import com.nsg.boardingfinder.Response.SuccessResponse.DataResponse;
import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.AppProgressDialog;
import com.nsg.boardingfinder.Utils.AppSharedPreferences;
import com.nsg.boardingfinder.Utils.Navigation;
import com.nsg.boardingfinder.Utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewBoardingFragment extends Fragment {
    private String boardingId="", accommodaterId="", currentBoardingId="";
    static  String dialNumber="";

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private TextView txtBoardingTitle,
            txtBoardingPostedDate,
            txtBoardingPrice,
            txtBoardingOwnerName,
            txtBoardingAddress,
            txtBoardingFacilities,
            txtBoardingContact,
            txtBoardingDescription,
            txtBoardingGender,
            txtBoardingElapsedTime,
            txtPostStatus;
    private Button btnCall, btnDenyPost, btnActivePost;
    private ImageSlider sliderPostImages;
    private LinearLayout layoutAdminBtns;

    private Dialog appAlertDialog;
    private Button appAlertButton;
    private static Dialog appProgressDialog;
    private TextView txtAlertTitle, txtAlertDesc;

    private String apiBaseUrl = "";
    private SharedPreferences sharedPre;
    private static Context context;

    public ViewBoardingFragment() {
        // Required empty public constructor
        Navigation.currentScreen = "ViewBoardingFragment";
    }

    public ViewBoardingFragment(String boardingId,String accommodaterId) {
        this.boardingId = boardingId;
        this.accommodaterId = accommodaterId;
        Navigation.currentScreen = "ViewBoardingFragment";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre=  getActivity().getSharedPreferences("BordingFinPre",0);
        appProgressDialog = AppProgressDialog.createProgressDialog(context);

        //TODO: Alert Dialog Start
        appAlertDialog = new Dialog(context);
        appAlertDialog.setContentView(R.layout.app_alert);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appAlertDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.background));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_boarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtBoardingTitle = (TextView)  getView().findViewById(R.id.boardingTitle);
        txtBoardingPostedDate = (TextView) getView().findViewById(R.id.boardingPostedDate);
        txtBoardingPrice = (TextView) getView().findViewById(R.id.boardingPrice);
        txtBoardingOwnerName = (TextView) getView().findViewById(R.id.boardingOwnerName);
        txtBoardingAddress = (TextView) getView().findViewById(R.id.boardingAddress);
        txtBoardingFacilities = (TextView) getView().findViewById(R.id.boardingFacilities);
        txtBoardingContact = (TextView) getView().findViewById(R.id.boardingContact);
        txtBoardingDescription = (TextView) getView().findViewById(R.id.boardingDescription);
        txtBoardingGender = (TextView) getView().findViewById(R.id.boardingGender);
        txtBoardingElapsedTime = (TextView) getView().findViewById(R.id.boardingElapsedTime);
        txtPostStatus = (TextView) getView().findViewById(R.id.txtPostStatus);
        btnCall= (Button) getView().findViewById(R.id.btnCall);
        btnDenyPost= (Button) getView().findViewById(R.id.btnDenyPost);
        btnActivePost= (Button) getView().findViewById(R.id.btnActivePost);
        layoutAdminBtns= (LinearLayout) getView().findViewById(R.id.layoutAdminBtns);
        //Image slider : Docs https://github.com/denzcoskun/ImageSlideshow
        sliderPostImages= getView().findViewById(R.id.sliderPostImages);
        // ........................................................................

        if( AppSharedPreferences.getData(sharedPre,"user-role") != null) {

            if (AppSharedPreferences.getData(sharedPre, "user-role").equals("accommodater")) {
                layoutAdminBtns.setVisibility(View.GONE);
                btnCall.setVisibility(View.VISIBLE);
            } else if (AppSharedPreferences.getData(sharedPre, "user-role").equals("admin")) {
                btnCall.setVisibility(View.GONE);
                btnActivePost.setVisibility(View.VISIBLE);
                btnDenyPost.setVisibility(View.VISIBLE);
                layoutAdminBtns.setVisibility(View.VISIBLE);
            }
        }

        // Fetch data from API ...................................................
        if(boardingId!=null && accommodaterId !=null)
        {
            if(!boardingId.equals("") && !accommodaterId.equals("")){
                getBoardingDetails();
            }
        }
        //........................................................................
        // Open Dial Pad  ........................................................
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dialNumber.equals("")){
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+dialNumber));
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, "Couldn't open dial pad!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //........................................................................

        btnActivePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{ updatePostStatus("active");},()->{});
            }
        });

        btnDenyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{ updatePostStatus("denied");},()->{});
            }
        });
    }

    public void getBoardingDetails(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.getBoardingById(boardingId,accommodaterId);
        appProgressDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) { // Code 200
                    JsonObject jsonObject = response.body();
                    BoardingByIdResponse boardingRes= new Gson().fromJson(jsonObject, BoardingByIdResponse.class);

                    System.out.println("==================Populate Boarding Details==================");
                    currentBoardingId= boardingRes.getBoarding().getId();
                    txtBoardingTitle.setText(boardingRes.getBoarding().getTitle());
                    txtPostStatus.setText(boardingRes.getBoarding().getStatus().toUpperCase());

                    switch (boardingRes.getBoarding().getStatus().toUpperCase()){
                        case "ACTIVE":
                            txtPostStatus.setTextColor(Color.parseColor("#18A558"));
                            btnActivePost.setVisibility(View.GONE);
                            break;
                        case "PENDING":
                            txtPostStatus.setTextColor(Color.parseColor("#FAD02C"));
                            break;
                        case "DENIED":
                            txtPostStatus.setTextColor(Color.parseColor("#FF2E2E"));
                            btnDenyPost.setVisibility(View.GONE);
                            break;
                    }

                    txtBoardingPostedDate.setText("Posted on "+boardingRes.getBoarding().getPostedAt());
                    txtBoardingPrice.setText("Rs."+boardingRes.getBoarding().getPrice()+" /month");
                    txtBoardingGender.setText("For "+boardingRes.getBoarding().getGender()+"s");
                    txtBoardingElapsedTime.setText(boardingRes.getBoarding().getTimeElapsed());
                    txtBoardingOwnerName.setText("For rent by "+ boardingRes.getOwner().getFullName());
                    txtBoardingAddress.setText(boardingRes.getOwner().getAddress());
                    txtBoardingContact.setText(boardingRes.getOwner().getPhone());
                    dialNumber = boardingRes.getOwner().getPhone();
                    txtBoardingDescription.setText(boardingRes.getBoarding().getDescription());

                    // Picasso.get().load(apiBaseUrl+boardingRes.getBoarding().getImage()).into(imgPostImage);
                    List<SlideModel> slideModels= new ArrayList<>();
                    //TODO: Can add more than one images as bellow
                    slideModels.add(new SlideModel(boardingRes.getBoarding().getImage()));
                    slideModels.add(new SlideModel(boardingRes.getBoarding().getImage()));
                    sliderPostImages.setImageList(slideModels, true);
                    sliderPostImages.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                           // Toast.makeText(context, "Selected item", Toast.LENGTH_LONG).show();
                         //   System.out.println("============================== INDEX: "+ i);
                            viewImgFullScreen(boardingRes.getBoarding().getImage());
                        }
                    });

                    StringBuilder facilitiesSet= new StringBuilder("");
                    for(String item: boardingRes.getBoarding().getFacilities()){
                        facilitiesSet.append(item + ", ");
                    }

                    if(boardingRes.getBoarding().getLatitude()!=null && boardingRes.getBoarding().getLongitude()!=null){
                        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
                        client = LocationServices.getFusedLocationProviderClient(getActivity());
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            showtLocation(boardingRes.getBoarding().getTitle(), Double.parseDouble(boardingRes.getBoarding().getLatitude()), Double.parseDouble(boardingRes.getBoarding().getLongitude()));
                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        }
                    }

                    txtBoardingFacilities.setText(facilitiesSet);
                    appProgressDialog.dismiss();
                } else { //Code : 400
                    appProgressDialog.dismiss();
                    showAppDialog("Error!", "Could not get post details\n Try again");
                    System.out.println("================== ERROR while getting Boardings ==================");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                appProgressDialog.dismiss();
                showAppDialog("Error!", "Could not get post details\n Try again");
                System.out.println("_RES_"+ t.getMessage());
            }
        });
    }

    private void viewImgFullScreen(String imageUrl){
        Intent intent = new Intent(context, FullScreenImgActivity.class);
        intent.putExtra("imageUrl",imageUrl);
        startActivity(intent);
    }

    private void showAppDialog(String alertTitle, String alertDescription){
        txtAlertTitle.setText(alertTitle);
        txtAlertDesc.setText(alertDescription);
        appAlertDialog.show();
    }

    private void updatePostStatus(String status){
        System.out.println("============ currentBoardingId" + currentBoardingId);
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.setPostStatus(currentBoardingId,status);
        appProgressDialog.show();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                if (response.isSuccessful()) { //Response code : 200
                    DataResponse dataRes= new Gson().fromJson(jsonObject, DataResponse.class);
                    System.out.println("========================  " + dataRes.getData());
                    switch (status){
                        case "active":
                            showAppDialog("Done", "Post has been published");
                            break;
                        case "denied":
                            showAppDialog("Done", "Post has been denied");
                            break;
                    }
                    switch (status.toUpperCase()){
                        case "ACTIVE":
                            txtPostStatus.setTextColor(Color.parseColor("#18A558"));
                            btnActivePost.setVisibility(View.GONE);
                            btnDenyPost.setVisibility(View.VISIBLE);
                            txtPostStatus.setText("ACTIVE");
                            break;
                        case "DENIED":
                            txtPostStatus.setTextColor(Color.parseColor("#FF2E2E"));
                            btnDenyPost.setVisibility(View.GONE);
                            btnActivePost.setVisibility(View.VISIBLE);
                            txtPostStatus.setText("DENIED");
                            break;
                    }

                    appProgressDialog.dismiss();
                }else { //Response code : 400
                    appProgressDialog.dismiss();
                    showAppDialog("Error!", "Could not update status\n Try again");
                    System.out.println("========================  INVALID Status given ======================== ");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                appProgressDialog.dismiss();
                showAppDialog("Error!", "Could not update status\n Try again");
                System.out.println("_RES_" + t.getMessage());
            }

        });
    }

    private void showtLocation(String DV_name, Double latitude, Double longitude) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //initialize lat lng
                            //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            LatLng latLng = new LatLng(latitude, longitude);

                            //Create maker options
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title(DV_name);

                            //Zoom map
                            googleMap.animateCamera((CameraUpdateFactory.newLatLngZoom(latLng, 10)));
                            //Add Single marker on Map
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
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