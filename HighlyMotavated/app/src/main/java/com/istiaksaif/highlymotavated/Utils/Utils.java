package com.istiaksaif.highlymotavated.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.istiaksaif.highlymotavated.Model.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<ProductItem> DataCache =new ArrayList<>();

    public static void show(Context c,String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public static Boolean productsExists(String key){
        for (ProductItem productItem: DataCache){
            if (productItem.getProductId().equals(key)){
                return true;
            }
        }
        return false;
    }
}
