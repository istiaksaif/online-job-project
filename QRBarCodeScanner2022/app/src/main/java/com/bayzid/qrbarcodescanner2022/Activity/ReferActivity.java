package com.bayzid.qrbarcodescanner2022.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bayzid.qrbarcodescanner2022.R;
import com.bayzid.qrbarcodescanner2022.utils.CheckInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity {

    private CardView copyLink,refer,generateReferLink;
    private DatabaseReference databaseReference;

    private TextView storereferlink;
    private String phoneToken;
    private TextView checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        refer = findViewById(R.id.referbtn);
        copyLink = findViewById(R.id.copylink);
        generateReferLink = findViewById(R.id.createreferlink);
        storereferlink = findViewById(R.id.storereferlink);
        checkInternet = findViewById(R.id.networkcheck);
        phoneToken = Settings.Secure.getString(ReferActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);
        GetDataFromFirebase();

        generateReferLink.setOnClickListener(view ->{
            createlink();
        });
        refer.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,  storereferlink.getText().toString());
            intent.setType("text/plain");
            startActivity(intent);
        });
        copyLink.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Refer link", storereferlink.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied Refer link is "+storereferlink.getText().toString(), Toast.LENGTH_SHORT).show();
        });
    }
    public void createlink(){
        Log.e("main", "create link ");
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("naxz"))
                .setDynamicLinkDomain("qrbarcodescanner2022.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();
        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "  Long refer "+ dynamicLinkUri);
        String sharelinktext  = "https://qrbarcodescanner2022.page.link/?"+
                "link=https://qrbarcodescanner2022.page.link/naxz"+
                "&apn="+ getPackageName()+
                "&st="+"My Refer Link"+
                "&sd="+"Reward will Given based on ongoing offer"+
                "&si="+getDrawable(R.drawable.splashimg);
        Log.e("main", "create link "+sharelinktext);
        // shorten the link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())
                .setLongLink(Uri.parse(sharelinktext))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            storereferlink.setText(shortLink.toString());
                            generateReferLink.setVisibility(View.GONE);
                            refer.setVisibility(View.VISIBLE);
                            copyLink.setVisibility(View.VISIBLE);
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("referLink", shortLink.toString());
                            String token = phoneToken;
                            databaseReference.child("usersData").child(token).updateChildren(result);
                        } else {
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });
    }
    private void GetDataFromFirebase() {
        databaseReference.child("usersData").child(phoneToken)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        try {
                            String referLink = dataSnapshot2.child("referLink").getValue().toString();
                            storereferlink.setText(referLink);
                            generateReferLink.setVisibility(View.GONE);
                            refer.setVisibility(View.VISIBLE);
                            copyLink.setVisibility(View.VISIBLE);
                        }catch (Exception e){
                            generateReferLink.setVisibility(View.VISIBLE);
                            refer.setVisibility(View.GONE);
                            copyLink.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!CheckInternet.isConnectedToInternet(ReferActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(ReferActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }
}