package com.istiaksaif.highlymotavated.Utils;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GetServerTime {
    Activity activity;
    String url = "https://www.timeapi.io/api/Time/current/zone?timeZone=America/New_York";
    RequestQueue requestQueue;

    public GetServerTime() {
    }

    public GetServerTime(Activity activity) {
        this.activity = activity;
        requestQueue = Volley.newRequestQueue(activity);
    }

    public void getDateTime(VolleyCallBack volleyCallBack){
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            volleyCallBack.onGetDateTime(response.getString("dateTime"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public interface VolleyCallBack{
        void onGetDateTime(String dateTime);
    }
}
