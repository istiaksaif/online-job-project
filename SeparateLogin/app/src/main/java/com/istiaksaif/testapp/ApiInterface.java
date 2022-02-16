package com.istiaksaif.testapp;

import static com.istiaksaif.testapp.Constants.Content_Type;
import static com.istiaksaif.testapp.Constants.SERVER_KEY;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({"Authorization: key="+SERVER_KEY, "Content-Type:"+Content_Type})
    @POST("fcm/send")
    Call<PushNotification> sendNotification(@Body PushNotification notification);
}
