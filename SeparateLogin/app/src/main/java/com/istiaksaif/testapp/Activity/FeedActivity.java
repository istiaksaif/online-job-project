package com.istiaksaif.testapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.istiaksaif.testapp.Adapter.FeedRecyclerViewAdapter;
import com.istiaksaif.testapp.MainActivity;
import com.istiaksaif.testapp.Model.FeedModel;
import com.istiaksaif.testapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class FeedActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION = 10004;
    private static final int PICK_IMAGE = 10001;

//    private EditText mMessageEditText;
//    private CircleImageView mMessageSendButton;
//    Utility utility;
//    SharedPreference prefs;

    ImageView mUploadFileButton;
    RecyclerView mRecyclerView;
    DatabaseReference databaseReference;

//    @Override
//    public void onBackPressed() {
//        if(rewardedVideoAd.isLoaded() && MainActivity.showAds){
//            rewardedVideoAd.show();
//        }else{
//            super.onBackPressed();
//        }
//
//    }
//
    FeedRecyclerViewAdapter adapter;
//    List<FeedModel> feedModelList;
//    private StorageReference mStorageRef;
//    ProgressDialog dialog;
//    private RewardedVideoAd rewardedVideoAd;

    int count=0;
    int AD_COUNT=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

//        AdsManager adsManager= new AdsManager(this);
//        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(FeedActivity.this);
//        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
//            @Override
//            public void onRewardedVideoAdLoaded() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdOpened() {
//
//            }
//
//            @Override
//            public void onRewardedVideoStarted() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdClosed() {
//                FeedActivity.super.onBackPressed();
//            }
//
//            @Override
//            public void onRewarded(RewardItem rewardItem) {
//                FeedActivity.super.onBackPressed();
//            }
//
//            @Override
//            public void onRewardedVideoAdLeftApplication() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdFailedToLoad(int i) {
//
//            }
//
//            @Override
//            public void onRewardedVideoCompleted() {
//
//            }
//        });
//        rewardedVideoAd.loadAd("ca-app-pub-8693178106453377/4520598007" , new AdRequest.Builder().build());
//        prefs = new SharedPreference(this);
//        dialog = new ProgressDialog(this);
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//
//        if (prefs.getCity() != null)
//            setTitle(prefs.getCity() + " Group");
//
//        feedModelList = new ArrayList<>();
//        adapter = new FeedRecyclerViewAdapter(this, feedModelList);
//        mRecyclerView = findViewById(R.id.recycler_open_channel_chat);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(FeedActivity.this);
//        mRecyclerView.setAdapter(adapter);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        loadAds(adsManager);
//
//        mUploadFileButton = findViewById(R.id.attachment_ib);
//        mMessageEditText = findViewById(R.id.edittext_chat_message);
//        mMessageSendButton = findViewById(R.id.record_button);
//
//        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String msg = mMessageEditText.getText().toString();
//                if (msg.length() > 0) {
//
//                    sendUserMessage(msg, "text", "", "");
//                }
//            }
//        });
//
//        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(FeedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
//                    }
//                } else {
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
//                }
//
//            }
//        });
//
//
//        loadMessages();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case STORAGE_PERMISSION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
//                } else {
//                    Toast.makeText(FeedActivity.this, "Storage permission required to share images", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//        }
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK)
//            if (requestCode == PICK_IMAGE) {
//                Uri uri, thumbUri;
//                uri = new CompressImage(this).compressImage(data.getData().toString(), 612, 812);
//                thumbUri = new CompressImage(this).compressImage(data.getData().toString(), 72, 96);
//
//                uploadImageToFirebaseStorage(uri, thumbUri);
//            }
//    }
//
//    private void uploadImageToFirebaseStorage(final Uri uri, final Uri thumbUri) {
//        dialog.setMessage("Uploading...");
//        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        dialog.setIndeterminate(false);
//        dialog.setProgress(0);
//        dialog.show();
//        DatabaseReference userMessagePushRef = databaseReference.child("Feed").child(prefs.getCity()).push();
//        final String pushKey = userMessagePushRef.getKey();
//
//        final StorageReference riversRef = mStorageRef.child("Message_Images").child(pushKey + ".jpg");
//
//        riversRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // Handle successful uploads on complete
//                if (taskSnapshot.getMetadata() != null) {
//                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//                    while (!urlTask.isSuccessful()) ;
//                    final Uri imageURI = urlTask.getResult();
//
//                    final StorageReference riversRef = mStorageRef.child("Message_Images").child(pushKey + "_thumb.jpg");
//                    riversRef.putFile(thumbUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            // Get a URL to the uploaded content
//                            if (taskSnapshot.getMetadata() != null) {
//                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//                                while (!urlTask.isSuccessful()) ;
//                                Uri thumbURi = urlTask.getResult();
//
//                                String text = mMessageEditText.getText().toString();
//                                if (text.length() > 0) {
//                                    sendUserMessage(text, "both", imageURI.toString(), thumbURi.toString());
//
//                                } else {
//                                    sendUserMessage(text, "img", imageURI.toString(), thumbURi.toString());
//                                }
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                        }
//                    });
//
//                } else
//                    utility.showSnackBar(getWindow().getDecorView(), "Failed to send image");
//
//                dialog.dismiss();
//                dialog.cancel();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                dialog.dismiss();
//                dialog.cancel();
//                utility.showSnackBar(getWindow().getDecorView(), "Failed to send image");
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                double pro = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                dialog.setProgress((int) pro);
//            }
//        });
//    }
//
//
//    private void sendUserMessage(String userInput, String messageType, String imagePath, String thumbPath) {
//
//        mMessageEditText.setText("");
//        FeedModel item = new FeedModel();
//        item.setFeedPerson(prefs.getName());
//        item.setFeedImg(imagePath);
//        item.setFeedText(userInput);
//        item.setFeedType(messageType);
//        item.setNumberofComments(0);
//        item.setThumbPath(thumbPath);
//        item.setFeedDate(System.currentTimeMillis());
//
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Feed").child(prefs.getCity()).push();
//        databaseReference.setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (!task.isSuccessful()) {
//                    Toast.makeText(FeedActivity.this, "failed to send messgae", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification").child(prefs.getCity()).push();
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("city", prefs.getCity());
//                    map.put("text", "feed message");
//                    map.put("uid", prefs.getUid());
//                    databaseReference.setValue(map);
//
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.mute_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_mute);
//
//        if (prefs.getMuteNotification()) {
//            item.setTitle("Unmute Notification");
//        } else {
//            item.setTitle("Mute Notification");
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_mute) {
//            if (item.getTitle().equals("Mute Notification")) {
//                prefs.setMuteNotification(true);
//                Toast.makeText(this, "notification muted", Toast.LENGTH_SHORT).show();
//                item.setTitle("Unmute Notification");
//            } else {
//                prefs.setMuteNotification(false);
//                Toast.makeText(this, "notification unmuted", Toast.LENGTH_SHORT).show();
//                item.setTitle("Mute Notification");
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    private void loadMessages() {
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("Feed").child(prefs.getCity()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                try {
//                    FeedModel feedModel = dataSnapshot.getValue(FeedModel.class);
//                    if (feedModel != null) {
//                        feedModel.setFeedId(dataSnapshot.getKey());
//                        feedModelList.add(0, feedModel);
//                        adapter.setData(0, feedModelList);
//                        if(count==AD_COUNT)
//                        {
//                            adapter.MixData();
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                FeedModel feed;
//                if (dataSnapshot.getKey() != null)
//                    for (int i = feedModelList.size() - 1; i >= 0; i--)
//                        if (dataSnapshot.getKey().equals(feedModelList.get(i).getFeedId())) {
//                            feed = dataSnapshot.getValue(FeedModel.class);
//                            if (feed != null) {
//                                feed.setFeedId(dataSnapshot.getKey());
//                            }
//                            feedModelList.set(i, feed);
//                            Collections.reverse(feedModelList);
//                            adapter.notifyDataSetChanged();
//
//                            break;
//                        }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
//    private void l(){
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("Feed").child(prefs.getCity()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    FeedModel feedModel = dataSnapshot.getValue(FeedModel.class);
//                    if (feedModel != null) {
//                        feedModel.setFeedId(dataSnapshot.getKey());
//                        feedModelList.add(0, feedModel);
//                        adapter.setData(0, feedModelList);
//                        if(count==AD_COUNT)
//                        {
//                            adapter.MixData();
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });
//    }

//    private void loadAds(AdsManager adsManager) {
//        final List<UnifiedNativeAd> ads =new ArrayList<>();
//        adsManager.createUnifiedAds(AD_COUNT, R.string.ads_native_uid, new AdUnifiedListening()
//        {
//            @Override
//            public void onAdFailedToLoad(LoadAdError loadAdError)
//            {
//                count++;
//                if(count==AD_COUNT)
//                {
//                    adapter.MixData();
//                }
//            }
//            @Override
//            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd)
//            {
//                count++;
//                ads.add(unifiedNativeAd);
//                //if(!getAdLoader().isLoading())
//                if(count== AD_COUNT)
//                {
//                    adapter.setAds(ads);
//                    adapter.MixData();
//                }
//                //Toast.makeText(SecondActivity.this, getAdLoader().isLoading()+" Succ "+v, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
