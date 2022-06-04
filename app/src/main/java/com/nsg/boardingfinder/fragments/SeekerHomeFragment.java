package com.nsg.boardingfinder.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nsg.boardingfinder.R;
import com.nsg.boardingfinder.Response.BoardingResponse.GetAllBoardingsResponse;
import com.nsg.boardingfinder.Response.FacilitiesResponse.FacilitiesRes;
import com.nsg.boardingfinder.Response.FacilitiesResponse.Facility;
import com.nsg.boardingfinder.SeekerActivity;
import com.nsg.boardingfinder.Utils.ApiInterface;
import com.nsg.boardingfinder.Utils.GetBoardingsReqModel;
import com.nsg.boardingfinder.Utils.RetrofitClient;
import com.nsg.boardingfinder.Utils.SearchModel;
import com.nsg.boardingfinder.adapter.BoardingRecyclerviewAdapter;
import com.nsg.boardingfinder.model.Boarding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SeekerHomeFragment extends Fragment {

    private ChipGroup chip_group_choice;
    private Chip chip_location, chip_facilities, chip_gender;
    private ImageSlider sliderPostImages;
    private CircularProgressIndicator progress_circular;
    private LinearLayout cardLayout;
    private SwipeRefreshLayout swipeContainer;
    private TextView noListingsItemsLabel;

    private static RecyclerView userRecycler;
    private static BoardingRecyclerviewAdapter boardingRecyclerviewAdapter;
    private CharSequence search="";
    static FragmentManager fragmentManager;


    private List<Boarding> boardingList = new ArrayList<Boarding>();
    private List<Integer> facilitiesList = new ArrayList<Integer>();
    private List<String> genderList = new ArrayList<String>();

    boolean[] selectedFacilities;
    Integer[] facilityIDsArray;
    String[] facilityNamesArray;
    ArrayList<Integer> listFacilityOutcome = new ArrayList<>();

    boolean[] selectedGenders;
    String[] gendersArray={"Male", "Female"};
    ArrayList<Integer> listGendersOutcome = new ArrayList<>();

    public String selectedLocation = "";
    String apiBaseUrl = "";
    private static Context context;

    public SeekerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedGenders = new boolean[gendersArray.length];
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        fragmentManager = getActivity().getSupportFragmentManager();
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                new GetBoardingTask().execute("my string parameter");
            }
        }, 3000);   // 3 seconds
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seeker_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        chip_location  = (Chip) getView().findViewById(R.id.chip_location);
        chip_facilities  = (Chip) getView().findViewById(R.id.chip_facilities);
        chip_gender  = (Chip) getView().findViewById(R.id.chip_gender);
        userRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

      //  cardLayout = (LinearLayout) getView().findViewById(R.id.cardLayout);
        swipeContainer = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        noListingsItemsLabel = (TextView) getView().findViewById(R.id.noListingsItemsLabel);

        //Image slider : Docs https://github.com/denzcoskun/ImageSlideshow
        sliderPostImages= (ImageSlider) getView().findViewById(R.id.sliderPostImages);

        progress_circular = (CircularProgressIndicator) getView().findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);

        //Add chip programmatically :- https://stackoverflow.com/questions/53477424/chip-group-oncheckedchangelistener-not-triggered
        chip_group_choice = (ChipGroup) getView().findViewById(R.id.chip_group_choice);
        chip_group_choice.setSingleSelection(false);

        //restore last queries from disk
        //lastSearches = loadSearchSuggestionFromDisk();
        List<String> tempList = new ArrayList<>();
        tempList.add("item 1");

        List<SlideModel> slideModels= new ArrayList<>();
        // slideModels.add(new SlideModel(apiBaseUrl+boardingRes.getBoarding().getImage()));
        // slideModels.add(new SlideModel(R.drawable.ic_login_hero));
        //TODO: Can add more than one images as bellow
        slideModels.add(new SlideModel("http://storage.googleapis.com/covid-19-self-care-app.appspot.com/avatars/1647783431825.jpg"));
        slideModels.add(new SlideModel("http://storage.googleapis.com/covid-19-self-care-app.appspot.com/avatars/1647783431825.jpg"));
        sliderPostImages.setImageList(slideModels, true);

        //Location chip Click Event
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
                                //API call
                                new GetBoardingTask().execute("my string parameter");
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        getAllFacilities();
        //Facilities chip Click Event
        chip_facilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(selectedFacilities != null ){
                   facilitiesDialog();
               }else{
                   Toast.makeText(getActivity(), "No Facilities to choose", Toast.LENGTH_SHORT).show();
               }
            }
        });

        //Gender chip Click Event
        chip_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedGenders != null ){
                    genderDialog();
                }
            }
        });

        /**
         *      Filtering Chips  : setOnCheckedChangeListener => Only work for SingleSelection Mode
         *         chip_group_choice.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
         *             @Override
         *             public void onCheckedChanged(ChipGroup group, int checkedId) {
         *
         *               //  System.out.println("=================== CHIP checked IDS: ");
         *                 List<Integer> checkedChipIds = chip_group_choice.getCheckedChipIds();
         *                 checkedChipIds.forEach(System.out::println);
         *
         *                // System.out.println("=================== CHIP ID: " + checkedId);
         *                 facilitiesList.forEach(System.out::println);
         *             }
         *         });
         */

        // Configure the refreshing colors // setColorSchemeResources(@ColorRes int... colorResIds)
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /**
                 *       Your code to refresh the list here.
                 *       Make sure you call swipeContainer.setRefreshing(false)-
                 *      -once the network request has completed successfully.
                 */
              //  cardLayout.removeAllViewsInLayout();
                progress_circular.setVisibility(View.VISIBLE);
                new GetBoardingTask().execute("my string parameter");
            }
        });
    }

    public static void listItemOnClick(String boardingId, String accommodaterId){
        //TODO: Goto View User Fragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new ViewBoardingFragment(boardingId, accommodaterId));
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private ArrayList<SearchModel> getLocaitonData(){
        List<String> listCity = Arrays.asList(getResources().getStringArray(R.array.Spinner_items));
        ArrayList<SearchModel> items = new ArrayList<>();
        for(String city: listCity){
            items.add(new SearchModel(city));
        }
        return items;
    }


        // getBoardings API call
    public void getBoardings(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        // Default Search Filters
        selectedLocation = selectedLocation.equals("") ? "all":selectedLocation;
        if(genderList.size()==0)   for (String ge: gendersArray) genderList.add(ge);
        if(facilitiesList.size()==0) for (Integer fa: facilityIDsArray) facilitiesList.add(fa);

        System.out.println("===============selectedLocation " + selectedLocation);
        System.out.println(">>>>>>>>>>>>> facilitiesList \n");
        facilitiesList.forEach(System.out::println);
        System.out.println(">>>>>>>>>>>>> facilitiesList END ");
        System.out.println(">>>>>>>>>>>>> genderList \n");
        genderList.forEach(System.out::println);
        System.out.println(">>>>>>>>>>>>> genderList END ");

        GetBoardingsReqModel reqModel = new GetBoardingsReqModel(facilitiesList, genderList);

        Call<JsonObject> call = apiInterface.getBoardings(selectedLocation,reqModel);
       // progress_circular.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) { // Code 200
                    JsonObject jsonObject = response.body();
                    GetAllBoardingsResponse allBoardingsResponse= new Gson().fromJson(jsonObject, GetAllBoardingsResponse.class);
                    /**
                     * Remove All previsouse Cards before adding new cards
                     * Remove All items in array adding new items
                     */
                    boardingList.clear();
                    //  cardLayout.removeAllViewsInLayout();
                    //.................................
                    if(allBoardingsResponse.getData().size() > 0){
                        for(Boarding item: allBoardingsResponse.getData()){
                            boardingList.add(item);
                         }
                        progress_circular.setVisibility(View.GONE);
                        noListingsItemsLabel.setVisibility(View.GONE);
                        setUserRecycler();
                    }else{
                        noListingsItemsLabel.setVisibility(View.VISIBLE);
                    }
                    swipeContainer.setRefreshing(false);
                    setUserRecycler();
                } else { //Code : 400
                    System.out.println("================== ERROR in Boardings Request  ==================");
                        Log.d("ERROR", response.toString());
                    Toast.makeText(getActivity(), "Try again!. pill to refresh", Toast.LENGTH_SHORT).show();
                }
             //   progress_circular.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), "Try again!. pill to refresh", Toast.LENGTH_SHORT).show();
                System.out.println("================== ERROR while getting Boardings ==================");
                System.err.println("_REq Failure_"+ t.getMessage());
                swipeContainer.setRefreshing(false);
                progress_circular.setVisibility(View.GONE);
            }

        });
    }


    public void getAllFacilities(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.getAllFacilities();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) { // Code 200
                    JsonObject jsonObject = response.body();
                    FacilitiesRes facilitiesRes= new Gson().fromJson(jsonObject, FacilitiesRes.class);

                    System.out.println("==================getAllFacilities==================");
                    // facilitiesRes.getData().forEach(System.out::println);
                    List<Facility>  facilityList = facilitiesRes.getData();

                    selectedFacilities = new boolean[facilitiesRes.getData().size()];
                    facilityIDsArray = new Integer[facilitiesRes.getData().size()];
                    facilityNamesArray = new String[facilitiesRes.getData().size()];

                    for(int i=0; i<facilityList.size(); i++ ) {
                        facilityIDsArray[i]= facilityList.get(i).getId();
                        facilityNamesArray[i]= facilityList.get(i).getFacility();
                    }

                } else { //Code : 400
                    System.out.println("================== ERROR while getting AllFacilities ==================");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("_RES_"+ t.getMessage());
             //   Toast.makeText(getActivity(), "Could Load data..reload page!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void facilitiesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Set title
        builder.setTitle("Choose Facilities");
        //set dialog non cancelable
        builder.setCancelable(false);

        //      setMultiChoiceItems (items, checkedItems, ...)
        builder.setMultiChoiceItems(facilityNamesArray, selectedFacilities, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Check condition
                if(isChecked){
                    //When checkbox selected
                    //Add position in list
                    listFacilityOutcome.add(which);
                    Collections.sort(listFacilityOutcome);
                }else{
                    //When checkbox un-selected
                    //Remove position in list
                    listFacilityOutcome.remove(Integer.valueOf(which));
                }
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Feed facilitiesList to make API call
                facilitiesList.clear();
                for(int j = 0; j< listFacilityOutcome.size(); j++){
                    // Add selected facilities with Avoiding duplicate values
                    if(!facilitiesList.contains(facilityIDsArray[listFacilityOutcome.get(j)])){
                        facilitiesList.add(facilityIDsArray[listFacilityOutcome.get(j)]);
                    }
                    // System.out.print(facilityIDsArray[listFacilityOutcome.get(j)] + " - ");
                    // System.out.println(facilityNamesArray[listFacilityOutcome.get(j)]);
                }

                // Fetch boardings from API
                new GetBoardingTask().execute("my string parameter");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dilog
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Use for loop
                for(int j = 0; j< selectedFacilities.length; j++){
                    //  Remove all selection
                    selectedFacilities[j]=false;
                    //Clear day list
                    listFacilityOutcome.clear();
                }
                // Clear All means boardings with all facilities
                facilitiesList.clear();
                //TODO: ONLY for filtering purpose
                 for(Integer item: facilityIDsArray){
                 facilitiesList.add(item);
                 }
            }
        });

        // Show dialog
        builder.show();
    }


    private void genderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Set title
        builder.setTitle("Choose Gender(s)");
        //set dialog non cancelable
        builder.setCancelable(false);

        //      setMultiChoiceItems (items, checkedItems, ...)
        builder.setMultiChoiceItems(gendersArray, selectedGenders, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //Check condition
                if(isChecked){
                    //When checkbox selected
                    //Add position in list
                    listGendersOutcome.add(which);
                    Collections.sort(listGendersOutcome);
                }else{
                    //When checkbox un-selected
                    //Remove position in list
                    listGendersOutcome.remove(Integer.valueOf(which));
                }
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Feed facilitiesList to make API call
                genderList.clear();
                for(int j = 0; j< listGendersOutcome.size(); j++){
                    // Add selected facilities with Avoiding duplicate values
                    if(!genderList.contains(gendersArray[listGendersOutcome.get(j)])){
                        genderList.add(gendersArray[listGendersOutcome.get(j)]);
                    }
                    // System.out.print(facilityIDsArray[listFacilityOutcome.get(j)] + " - ");
                    // System.out.println(facilityNamesArray[listFacilityOutcome.get(j)]);
                }
                // Change Chip Text
                if(genderList.size()==1){
                    chip_gender.setText(genderList.get(0));
                }else{chip_gender.setText("Gender");}
                // Fetch boardings from API
                new GetBoardingTask().execute("my string parameter");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dilog
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Use for loop
                for(int j = 0; j< selectedGenders.length; j++){
                    //  Remove all selection
                    selectedGenders[j]=false;
                    //Clear day list
                    listGendersOutcome.clear();
                }
                chip_gender.setText("Gender");
                // Clear All means boardings with all genders
                genderList = Arrays.asList(gendersArray);
            }
        });

        // Show dialog
        builder.show();
    }

    private void  setUserRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        userRecycler.setLayoutManager(layoutManager);
        boardingRecyclerviewAdapter = new BoardingRecyclerviewAdapter(context, boardingList);
        userRecycler.setAdapter(boardingRecyclerviewAdapter);
    }

    /**
     * AsyncTask
     */
    private class GetBoardingTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            boardingList.clear();
            progress_circular.setVisibility(View.VISIBLE);
            swipeContainer.setRefreshing(true);
            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            String myString = params[0];

            // Do something that takes a long time, for example:
            getBoardings();

            System.out.println(">>>>>>>>>>>>> doInBackground ");
            return "this string is passed to onPostExecute";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            System.out.println(">>>>>>>>>>>>> onProgressUpdate ");
            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(">>>>>>>>>>>>> onPostExecute ");
            // Do things like hide the progress bar or change a TextView
            progress_circular.setVisibility(View.GONE);
        }
    }

}