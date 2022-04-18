package com.example.isiuniversity.api;

import com.example.isiuniversity.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {
 @GET("/user/agent/profile")
 Call<UserResponse> getUserById(
         @Path("id") int id
 );
}
