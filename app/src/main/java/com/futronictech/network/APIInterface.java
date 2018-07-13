package com.futronictech.network;

import com.futronictech.model.BWStaff_Datum;
import com.futronictech.model.Branch_Datum;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("branch/9/attendee")
    Call<List<BWStaff_Datum>> doGetUserList();

    @GET("branch")
    Call<List<Branch_Datum>> doGetBranch();



    /*@FormUrlEncoded
    @POST("users")
    Call<UserList> createUser(@Body UserList userList);*/
}
