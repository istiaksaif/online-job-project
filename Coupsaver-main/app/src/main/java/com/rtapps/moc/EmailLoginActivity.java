package com.rtapps.moc;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailLoginActivity extends AppCompatActivity {

    EditText emailBox,passBox;
    Button loginSignUpBtn,phoneNumberBtn;
    TextView switchBtn,errorTV,title,forgetBtn;
    Boolean isLogin=true;
    Button gSignInButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;


    public static Activity getActivity(){
        return new EmailLoginActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);


        initViews();
        initGoogleSignIn();
    }


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

    private void initViews(){
        emailBox=findViewById(R.id.emailBox);
        passBox=findViewById(R.id.passwordBox);
        loginSignUpBtn =findViewById(R.id.login_signup_button);
        switchBtn=findViewById(R.id.switch_to);
        gSignInButton=findViewById(R.id.sign_in_button);
        progressBar=findViewById(R.id.progress);
        phoneNumberBtn=findViewById(R.id.phoneNumberBtn);
        errorTV=findViewById(R.id.errorTV);
        title=findViewById(R.id.title);
        forgetBtn=findViewById(R.id.forgetPasswordBtn);

        phoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(EmailLoginActivity.this,LoginActivity.class));
            }
        });

        loginSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isLogin){
                    switchBtn.setText(getString(R.string.already_have_account));
                    loginSignUpBtn.setText(getString(R.string.sign_up));
                    title.setText(getString(R.string.sign_up));
                    gSignInButton.setVisibility(View.GONE);
                    phoneNumberBtn.setVisibility(View.GONE);
                    forgetBtn.setVisibility(View.GONE);
                    isLogin=false;
                }
                else
                {
                    switchBtn.setText(getString(R.string.dont_have_account));
                    loginSignUpBtn.setText(getString(R.string.login));
                    title.setText(getString(R.string.login));
                    gSignInButton.setVisibility(View.VISIBLE);
                    phoneNumberBtn.setVisibility(View.VISIBLE);
                    forgetBtn.setVisibility(View.VISIBLE);
                    isLogin=true;
                }
            }
        });

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailBox.getText().toString();
                if(!email.isEmpty()){
                    resetPasswordAlert(email);
                }
                else
                {
                    errorMsg(getString(R.string.enter_email));
                }

            }
        });
    }

    private void resetPasswordAlert(String email){

       new AlertDialog.Builder(this)
                .setTitle(R.string.reset_password)
                .setMessage(R.string.reset_password_msg)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        resetPassword(email);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(R.drawable.logo_transparent)
                .show();
    }


    private void resetPassword(String email){

        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           resetLinkMessage(email);
                        }
                    }
                });
    }

    private void resetLinkMessage(String  email){

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.reset_password))
                .setMessage(getString(R.string.reset_password_link_msg) +
                        email)

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.logo_transparent)
                .show();
    }

    private void initGoogleSignIn(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

         GoogleSignInClient mGoogleSignInClient =  GoogleSignIn.getClient(this, gso);

        gSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                googleSignIn(mGoogleSignInClient);
            }
        });
    }


    private void googleSignIn(GoogleSignInClient mGoogleSignInClient) {
        showProgress();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            Log.v("EmailLoginTest","result");
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                           errorMsg(e.getMessage());
                           hideProgress();
                        }
                    }
                    else
                    {
                        hideProgress();
                    }
                }
            });


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.v("EmailLoginTest","firebase");
                            setUser(account.getEmail(),task.getResult().getUser().getUid());
                        } else {
                            errorMsg(task.getException().getMessage());
                            hideProgress();
                        }
                    }
                });
    }

    private void goToMain(){
        startActivity(new Intent(this,DashboardActivity.class));
        finish();
    }

    private void errorMsg(String msg){
        hideProgress();
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void errorMsg1(String msg){
        hideProgress();
        errorTV.setVisibility(View.VISIBLE);
        errorTV.setText(msg);

    }


    private void login(){

        showProgress();
        String  email=emailBox.getText().toString().trim();
        String password=passBox.getText().toString().trim();

        if(email.isEmpty()){
            errorMsg1(getString(R.string.enter_email));
        }
        else if(password.isEmpty()){
            errorMsg1(getString(R.string.enter_password));
        }
        else
        {
            errorTV.setVisibility(View.GONE);
            if(isLogin){signIn(email,password);}
            else{signUp(email,password);}

        }

    }

    private void signIn(String email,String password){

        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                goToMain();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                switch (e.getMessage()){

                    case "The email address is badly formatted.":{
                        errorMsg1(getString(R.string.invalid_email));
                    }
                    break;
                    case "The password is invalid or the user does not have a password.":{
                        errorMsg1(getString(R.string.wrong_password));
                    }
                    break;
                    case "There is no user record corresponding to this identifier. The user may have been deleted.":{
                        errorMsg1(getString(R.string.no_account_found));
                    }
                    break;
                    default:{
                        errorMsg1(e.getMessage());
                    }
                }
            }
        });
    }

    private void signUp(String email,String password){

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
               setUser(email,authResult.getUser().getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                switch (e.getMessage()){
                    case "The email address is badly formatted.":{
                        errorMsg1(getString(R.string.invalid_email));
                    }
                    break;
                    case "The given password is invalid. [ Password should be at least 6 characters ]":{
                        errorMsg1(getString(R.string.invalid_password));
                    }
                    break;
                    case "The email address is already in use by another account.":{
                        errorMsg1(getString(R.string.email_already_in_use));
                    }
                    break;
                    default:{
                        errorMsg1(e.getMessage());
                    }
                }



            }
        });
    }

    private void setUser(String email,String id){

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(email).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                Log.v("EmailLoginTest","onsuccess");
                if (task.isSuccessful()) {

                    DatabaseReference userIDRef = usersRef.push();
                    userIDRef.child("email").setValue(email);
                    userIDRef.child("ID").setValue(id).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            goToMain();
                            Log.v("EmailLoginTest","success");
                        }
                    });
                }
                else
                {
                    goToMain();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("EmailLoginTest",e.getMessage());
            }
        });
    }



    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
}