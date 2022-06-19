package com.istiaksaif.highlymotavated.Receiver;

import static com.istiaksaif.highlymotavated.Receiver.Constants.Content_Type;
import static com.istiaksaif.highlymotavated.Receiver.Constants.NOTIFICATION_URL;
import static com.istiaksaif.highlymotavated.Receiver.Constants.SERVER_KEY;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {
    public static void getToken(String message, String title, String hisID, Context context) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(hisID);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String token = snapshot.child("token").getValue().toString();
                    newpart(token, message, title, hisID,context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void newpart(String token, String message, String title, String hisID, Context context) {
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("message", message);
            data.put("hisID", hisID);

            to.put("to", token);
            to.put("data", data);

            sendNotification(to,context);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void sendNotification(JSONObject to, Context context) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, NOTIFICATION_URL, to, response -> {
            Log.d("notification", "sendNotification: " + response);
        }, error -> {
            Log.d("notification", "sendNotification: " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + SERVER_KEY);
                map.put("Content-Type", Content_Type);
                return map;
            }

            @Override
            public String getBodyContentType() {
                return Content_Type;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
