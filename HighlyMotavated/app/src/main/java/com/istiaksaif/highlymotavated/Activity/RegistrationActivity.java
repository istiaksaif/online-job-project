package com.istiaksaif.highlymotavated.Activity;

import static com.istiaksaif.highlymotavated.Activity.SplashScreenActivity.saveData;
import static com.istiaksaif.highlymotavated.Utils.GetServerTimeContext.getCurrentDate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.istiaksaif.highlymotavated.Model.User;
import com.istiaksaif.highlymotavated.R;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private TextView signin;
    private TextInputEditText fullName,email,phone,password,passwordRepeat;
    private Button registrationButton;
    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        fullName = findViewById(R.id.name);
        email = findViewById(R.id.eamil);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.pass);
        passwordRepeat = findViewById(R.id.passretype);
        registrationButton = findViewById(R.id.reg_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
                }
        });

        //intent Login page
        signin = findViewById(R.id.signinactivity);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private  void collectToken(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    return;
                }
                String token = task.getResult();
                databaseReference.child("users").orderByChild("userId").equalTo(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    databaseReference.child("users").child(dataSnapshot.getKey())
                                            .child("token")
                                            .setValue(token);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void Register() {
        String FullName = fullName.getText().toString();
        String Email = email.getText().toString();
        String PHONE = phone.getText().toString();
        String Password = password.getText().toString();
        String Password_re = passwordRepeat.getText().toString();

        if (TextUtils.isEmpty(FullName)){
            Toast.makeText(RegistrationActivity.this, "please enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Email)){
            Toast.makeText(RegistrationActivity.this, "please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(PHONE)){
            Toast.makeText(RegistrationActivity.this, "please enter NID", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password)){
            Toast.makeText(RegistrationActivity.this, "please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(Password_re)){
            Toast.makeText(RegistrationActivity.this, "please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!Password.equals(Password_re)){
            passwordRepeat.setError("password not match");
            return;
        }
        else if (Password.length()<8){
            Toast.makeText(RegistrationActivity.this, "password week & password length " +
                    "at least 8 character", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!isValidEmail(Email)){
            email.setError("Invalid email");
            return;
        }
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(
                RegistrationActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String key = databaseReference.push().getKey();
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            User userhelp = new User(FullName,Email,PHONE,"","User","","",uid,"0","",key,"",getCurrentDate());
                            databaseReference.child("usersData").child(key).setValue(userhelp);
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("userId", uid);
                            result.put("key", key);
                            databaseReference.child("users").child(uid).setValue(result);
                            collectToken();
                            saveData(RegistrationActivity.this,"","");
                            Toast.makeText(RegistrationActivity.this, "Registration " +
                                    "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this,
                                    UserHomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Authentication " +
                                    "Failed "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this,
                                    RegistrationActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}