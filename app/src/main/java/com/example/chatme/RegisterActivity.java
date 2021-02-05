package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail,UserPassword,InputPhoneNumber,UserName;
    private TextView AlreadyHaveAccountLink;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();






        InitializeFields();
        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent =new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             CreateNewAccount();
            }


        });

    }

    private void CreateNewAccount()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        final String name=UserName.getText().toString();
        final String phonenumber=InputPhoneNumber.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"please enter email address.",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"please enter password address.",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"please enter your name",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(phonenumber))
        {
            Toast.makeText(this,"please enter phonenumber.",Toast.LENGTH_SHORT).show();
        }
        else
        {


            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait,while creating");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                String CurrentUserId=mAuth.getCurrentUser().getUid();

                                RootRef.child("Users").child(CurrentUserId).child("name").setValue(name);
                                RootRef.child("Users").child(CurrentUserId).child("phonenumber").setValue(phonenumber);
                                RootRef.child("Users").child(CurrentUserId).child("status").setValue("");


                                Intent mainIntent =new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                                Toast.makeText(RegisterActivity.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });



        }

    }






    private void InitializeFields()
    {
        CreateAccountButton=(Button)findViewById(R.id.register_button);
        UserEmail=(EditText) findViewById(R.id.register_email);
        UserName=(EditText) findViewById(R.id.name1);

        InputPhoneNumber=(EditText)findViewById(R.id.phonenum);
        UserPassword=(EditText) findViewById(R.id.register_password);
       AlreadyHaveAccountLink=(TextView)findViewById(R.id.already_have_account_link);
        loadingBar = new ProgressDialog(this);


    }
}
