package com.sarahproto.storeleaks.Api;

import com.sarahproto.storeleaks.Response.AddItemResponse;
import com.sarahproto.storeleaks.Response.UserLoginResponse;
import com.sarahproto.storeleaks.Response.UserRegisterResponse;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

public interface StoreleaksAPIService {

    /**
     * User registration.
     */
    @FormUrlEncoded
    @POST("api/v1/register")
    Call<UserRegisterResponse> userRegister(@Field("name") String userName,
                                            @Field("email") String userEmail,
                                            @Field("password") String userPassword);

    /**
     * User Login.
     */
    @FormUrlEncoded
    @POST("api/v1/login")
    Call<UserLoginResponse> userLogin(@Field("email") String userEmail,
                                      @Field("password") String userPassword);

    /**
     * Get User Info
     */
    @FormUrlEncoded
    @POST("api/v1/getUserInfo")
    Call<UserLoginResponse> getUserInfo(@Field("userId") int userId);

    /**
     * Add item
     */
    @Multipart
    @POST("api/v1/addItem")
    Call<AddItemResponse> addItem(@Part("userId") int userId,
                                  @Part("images[]") RequestBody imageFile,
                                  @Part("amount") int amount,
                                  @Part("name") String name,
                                  @Part("shop") String shop,
                                  @Part("location") String location,
                                  @Part("location_more") String location_more,
                                  @Part("description") String description);
}