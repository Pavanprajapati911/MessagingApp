package com.example.messaging_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;

import java.util.concurrent.TimeUnit;

public class Enter_Mobile_No_Activity extends AppCompatActivity {

    EditText enterMobileNo;

    EditText enterName;
    AppCompatButton continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile_no);
        enterMobileNo = findViewById(R.id.enterMobileNumber);
        continueButton = findViewById(R.id.continueButton);
        enterName = findViewById(R.id.enterName);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enterMobileNo.getText().toString().length()==10 && enterName!=null){

                    Intent intent = new Intent(Enter_Mobile_No_Activity.this,Verify_MobileNumber_Activity.class);
                    intent.putExtra("UserMobileNumber","+91"+enterMobileNo.getText().toString());
                    intent.putExtra("UserName",enterName.getText().toString());
                    startActivity(intent);
                }else {
                    Toast.makeText(Enter_Mobile_No_Activity.this,"Enter valid mobile number",Toast.LENGTH_LONG).show();
                }



            }
        });


    }
}