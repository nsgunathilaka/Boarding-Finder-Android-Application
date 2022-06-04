package com.nsg.boardingfinder.Utils;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    //TODO : USer Registraion

    @Multipart
    @POST("api/users/signup/{role}")
    Call<ResponseBody> userRegister(@Part MultipartBody.Part avatar,
                                    @Part("fullName") RequestBody userId,
                                    @Part("address") RequestBody address,
                                    @Part("phone") RequestBody phone,
                                    @Part("gender") RequestBody gender,
                                    @Part("occupation") RequestBody occupation,
                                    @Part("email") RequestBody email,
                                    @Part("password") RequestBody password,
                                    @Path("role")
                                    String role);

    //TODO : Login

    @FormUrlEncoded
    @POST("api/auth")
    Call<JsonObject> userLogin(@Field("email") String email, @Field("password") String password);





    //TODO : Boardings  :::::::  Boardings     :::::::  Boardings    :::::::  Boardings   :::::::  Boardings   :::::::  Boardings


    //add boarding
    @Multipart
    @POST("api/boarding")
    Call<ResponseBody> addBoarding(@Part MultipartBody.Part image,
                                    @Part("title") RequestBody title,
                                    @Part("price") RequestBody price,
                                    @Part("location") RequestBody location,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("category") RequestBody category,
                                    @Part("accommodaterId") RequestBody accommodaterId,
                                    @Part("gender") RequestBody gender,
                                    @Part("description") RequestBody description,
                                    @Part("facilities") RequestBody facilities);

    @POST("api/boarding")
    Call<ResponseBody> addBoarding23(@Body RequestBody file);

    //Get Boardings
   // @FormUrlEncoded
    @POST("api/boarding/{location}")
    Call<JsonObject> getBoardings(@Path("location") String location, @Body GetBoardingsReqModel facilities);

    //Get one Boarding
    //Row Url: http://localhost:8081/api/boarding/get?boardingId=1&ownerId=1
    @GET("api/boarding/get")
    Call<JsonObject> getBoardingById(@Query("boardingId") String boardingId, @Query("ownerId") String ownerId);


    @GET("api/boarding/getPosts/{ownerId}/{postsStatus}")
    Call<JsonObject> getPostsByStatusAndOwnerId(@Path("ownerId") String ownerId, @Path("postsStatus") String postsStatus);


    @PATCH("api/boarding/setPostStatus/{postId}/{postsStatus}")
    Call<JsonObject> setPostStatus(@Path("postId") String postId, @Path("postsStatus") String postsStatus);




    //TODO : Facilities :::::: Facilities :::::: Facilities :::::: Facilities :::::: Facilities  :::::: Facilities


    //Get All Facilities
    @GET("api/facilities")
    Call<JsonObject> getAllFacilities();

    //Add new Facility
    @FormUrlEncoded
    @POST("api/facilities")
    Call<JsonObject> addFacility(@Field("facility") String facility);

    //Update Facility
    @FormUrlEncoded
    @PATCH("api/facilities")
    Call<JsonObject> updateFacility(@Field("id") String id, @Field("facility") String facility);

    //Delete Facility
    @DELETE("api/facilities/{id}")
    Call<JsonObject> deleteFacility(@Path("id") String id);




    //TODO : User Accounts :::::: User Accounts   :::::: User Accounts   :::::: User Accounts  :::::: User Accounts


    //Get account by id
    @GET("api/users/get/{loginId}")
    Call<JsonObject> getUserProfile(@Path("loginId") String loginId);

    @FormUrlEncoded
    @POST("api/users/reset-password")
    Call<JsonObject> forgetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("api/users/update-password")
    Call<JsonObject> updatePassword(@Field("loginId") String loginId, @Field("oldPsw") String oldPsw, @Field("newPsw") String newPsw);
}
