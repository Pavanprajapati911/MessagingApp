package com.example.messaging_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.messaging_app.userModels.usermodel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Verify_MobileNumber_Activity extends AppCompatActivity {
    EditText enterOTP;
    TextView resendButton;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    AppCompatButton confirmButton;

    String verificationID, userMobileNumber, userName;

    PhoneAuthProvider.ForceResendingToken mforceResendingToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile_number);

        enterOTP = findViewById(R.id.enterOTP);
        confirmButton = findViewById(R.id.confirmbutton);
        resendButton = findViewById(R.id.Resend);

        userMobileNumber = getIntent().getStringExtra("UserMobileNumber");
        userName = getIntent().getStringExtra("UserName");

        firebaseAuth.useAppLanguage();

        PhoneAuthOptions.Builder smsBuilder =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(userMobileNumber)
                        .setTimeout(120L,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                enterOTP.setText(phoneAuthCredential.getSmsCode());
                                Intent intent = new Intent(Verify_MobileNumber_Activity.this,MainActivity.class);
                                startActivity(intent);

                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.d("MSG", "onVerificationFailed: "+e);
                                Toast.makeText(Verify_MobileNumber_Activity.this,"error occured try again "+e,Toast.LENGTH_LONG).show();
                                finish();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationID = s;
                                mforceResendingToken = forceResendingToken;

                            }
                        });

        SendingOTP(smsBuilder,false);


        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendingOTP(smsBuilder,true);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationID,enterOTP.getText().toString());
                    verifyOTP(authCredential);
            }
        });


    }

    private void SendingOTP(PhoneAuthOptions.Builder smsBuilder, Boolean resend){

        if (!resend){
            PhoneAuthProvider.verifyPhoneNumber(smsBuilder.build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(smsBuilder.setForceResendingToken(mforceResendingToken).build());
        }

    }

    private void verifyOTP(PhoneAuthCredential phoneAuthCredential){

        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            checkUserAlreadyRegisteredOrNot(userMobileNumber);
                        }else {

                            Log.d("MSG", "onError: "+task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(Verify_MobileNumber_Activity.this,"invalid code entered",Toast.LENGTH_LONG).show();
                                finish();

                            }
                        }

                    }
                });

    }

    private void checkUserAlreadyRegisteredOrNot(String userMobileNumber){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(userMobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){

                    userLogin();
                    registerUserInDatabase(userMobileNumber,userName);

                }else {

                    userLogin();
                    Intent intent = new Intent(Verify_MobileNumber_Activity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MSG", "onCancelled: "+error);
            }
        });



    }

    private void registerUserInDatabase(String userMobileNumber, String UserName){

        usermodel user = new usermodel(UserName,userMobileNumber);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(userMobileNumber).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Intent intent = new Intent(Verify_MobileNumber_Activity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MSG", "onFailure: "+e);
                Toast.makeText(Verify_MobileNumber_Activity.this,"cant registered",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void userLogin(){

        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);

        SharedPreferences.Editor editor = login.edit();

        editor.putBoolean("LoggedInOrNot",true);
        editor.apply();

        SharedPreferences sharedPreferences = getSharedPreferences("userdetails",MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        Log.d("MSGING", "userLogin: "+userMobileNumber+" "+userName);
        editor1.putString("mobilenumber",userMobileNumber);
        editor1.putString("username",userName);
        editor1.apply();

        SharedPreferences sharedPreferences1 = getSharedPreferences("FirstTimeAfterInstall",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences1.edit();
        editor2.putBoolean("FirstTimeOpenAfterInstall",true);
        editor2.apply();

    }

}