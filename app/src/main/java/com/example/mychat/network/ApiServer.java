package com.example.mychat.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiServer {
    @POST("send")
    Call<String> sendMessage(
            @HeaderMap HashMap<String,String> header,
            @Body String messageBody
            );
}
