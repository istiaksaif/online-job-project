package com.istiaksaif.highlymotavated.Utils;


import static com.istiaksaif.highlymotavated.Receiver.Constants.NOTIFICATION_URL;

import com.istiaksaif.highlymotavated.Interface.ApiInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Apiutil {
    private static Retrofit retrofit = null;
    public static ApiInterface getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(NOTIFICATION_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}
