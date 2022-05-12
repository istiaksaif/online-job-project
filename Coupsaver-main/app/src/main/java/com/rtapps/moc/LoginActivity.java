package com.rtapps.moc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ViewFlipper loginViewFlipper;
    Button loginBtn;
    Button btnOTP;
    String phone, mobNumber;
    LinearLayout backBtn;
    String mVerificationId;
    EditText editTextMobile;
    //The edittext to input the code
    EditText editTextCode;
    String countryCode;
    //firebase auth object
    FirebaseAuth mAuth;
    OtpView otpView;
    CountryCodePicker regccp;
    private String verificationId;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            loginViewFlipper.setDisplayedChild(1);

            verificationId = s;

        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.

            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.

                edtOTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.

            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    // variable for our text input
    // field for phone and OTP.
    private EditText edtPhone, edtOTP;
    // buttons for generating OTP and verifying OTP
    private Button verifyOTPBtn, generateOTPBtn;

    @Override
    protected void onStart() {
        super.onStart();
        LanguageSet.setLocale(this);
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    // string for storing our verification ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
////        FOR WHITE bELOW
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(LoginActivity.this.getResources().getColor(R.color.background));
        init();
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 44);
        }*/


    }

    private void init() {
        loginViewFlipper = findViewById(R.id.viewFlipper_login);
        generateOTPBtn = findViewById(R.id.login_button);
        generateOTPBtn.setOnClickListener(this);
        verifyOTPBtn = findViewById(R.id.verify_opt_btn);
        verifyOTPBtn.setOnClickListener(this);
        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(this);
        edtPhone = findViewById(R.id.edittext_phonenumber);
        otpView = findViewById(R.id.otp_view);
        regccp = findViewById(R.id.ccp);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_button:

//                        Code Below is To run with otp ..............................
                countryCode = regccp.getSelectedCountryCodeWithPlus();
                phone = edtPhone.getText().toString();

                mobNumber = countryCode + phone;

                if (phone.equals("")) {
                    edtPhone.setError("Enter Phone");
                    Toast.makeText(this, "Enter Phone", Toast.LENGTH_SHORT).show();

                } else {
                    sendVerificationCode(mobNumber);
                }

//                UnComment Below Line To run without otp ..............................
//                loginViewFlipper.setDisplayedChild(1);


                break;
            case R.id.verify_opt_btn:

                otpView.getText().toString();
                verifyOtp();
//
//                Intent is = new Intent(LoginActivity.this, DashboardActivity.class);
//                startActivity(is);


                break;

            case R.id.back_button:
                loginViewFlipper.setDisplayedChild(0);

                break;

            case R.id.otp_view:
                getotp();
                break;

        }
    }

    private void getotp() {


    }

    private void verifyOtp() {


        if (TextUtils.isEmpty(otpView.getText().toString())) {
            // if the OTP text field is empty display
            // a message to user to enter OTP
            Toast.makeText(LoginActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
        } else if (otpView.getText().toString().length() < 6) {
            Toast.makeText(LoginActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();


        } else {
            // if OTP field is not empty calling
            // method to verify the OTP.
            verifyCode(otpView.getText().toString());
            loginViewFlipper.setDisplayedChild(1);


        }
    }

    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.


        PhoneAuthCredential credential = null;
        try {
            credential = PhoneAuthProvider.getCredential(verificationId, code);

            signInWithCredential(credential);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Incorrect Code", Toast.LENGTH_LONG).show();
        }

        // after getting credential we are
        // calling sign in method.
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                            usersRef.orderByChild("phone").equalTo(mobNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() == null) {


                                        DatabaseReference userIDRef = usersRef.push();
                                        userIDRef.child("phone").setValue(mobNumber);
                                        userIDRef.child("ID").setValue(task.getResult().getUser().getUid());


                                    }
                                    Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                                    startActivity(i);
                                    EmailLoginActivity.getActivity().finish();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {

        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)            // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


//    @Override
//    public void onResume() {
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
//        super.onResume();
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//    }
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"Message Recieved",Toast.LENGTH_SHORT).show();
//            if (intent.getAction().equalsIgnoreCase("otp")) {
//                final String message = intent.getStringExtra("message");
//                // message is the fetching OTP
//            }
//        }
//    };
//


}