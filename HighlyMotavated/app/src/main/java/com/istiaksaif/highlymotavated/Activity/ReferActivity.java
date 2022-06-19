package com.istiaksaif.highlymotavated.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.StorageReference;
import com.istiaksaif.highlymotavated.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity {

    private CardView copyLink,refer,generateReferLink;
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid(),key;
    private TextView storereferlink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        key = getIntent().getStringExtra("key");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        refer = findViewById(R.id.referbtn);
        copyLink = findViewById(R.id.copylink);
        generateReferLink = findViewById(R.id.createreferlink);
        storereferlink = findViewById(R.id.storereferlink);

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
                .setLink(Uri.parse("https://play.google.com/"))
                .setDynamicLinkDomain("highlymotavatedecommerce.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();
        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "  Long refer "+ dynamicLink.getUri());
        String sharelinktext  = "https://highlymotavatedecommerce.page.link/?"+
                "link=https://play.google.com/store/apps?referUser="+key+"_"+uid+
                //"link=http://www.highlymotavated.com/myref.php?referUser="+key+"_"+uid+
                "&apn="+ getPackageName()+
                "&st="+"My Refer Link"+
                "&sd="+"Reward Coins 20"+
                "&si="+getDrawable(R.drawable.icon);
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
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("referLink", shortLink.toString());
                            databaseReference.child("usersData").child(key).updateChildren(result);
                        } else {
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });
    }
    private void GetDataFromFirebase() {
        databaseReference.child("usersData").child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        try {
                            String referLink = dataSnapshot2.child("referLink").getValue().toString();
                            storereferlink.setText(referLink);
                            generateReferLink.setVisibility(View.GONE);
                        }catch (Exception e){
                            generateReferLink.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}