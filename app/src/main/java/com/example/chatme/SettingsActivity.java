package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrinterId;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.tasks.Tasks.await;
import static com.squareup.picasso.Picasso.*;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName, userStatus,userNumber;
    private CircleImageView userProfileImage;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final int GalleryPick = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;
    private Toolbar SettingsToolBar;
    public Uri ImagUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        initializeFields();


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Updatesettings();

            }
        });


        RetrieveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();

                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, GalleryPick);

            }
        });

    }




    private void RetrieveUserInfo() {
        RootRef.child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.child(CurrentUserId).hasChild("name")) && (dataSnapshot.child(CurrentUserId).hasChild("imageurl"))) {
                            String retrieveUserName = dataSnapshot.child(CurrentUserId).child("name").getValue().toString();
                            String retrieveNumber = dataSnapshot.child(CurrentUserId).child("phonenumber").getValue().toString();
                            String retrievestatus = dataSnapshot.child(CurrentUserId).child("status").getValue().toString();


                            String retrievesimage = dataSnapshot.child(CurrentUserId).child("imageurl").getValue().toString();

                            Picasso.with(SettingsActivity.this).load(retrievesimage).into(userProfileImage);


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievestatus);
                            userNumber.setText(retrieveNumber);


                        } else if ((dataSnapshot.exists()) && (dataSnapshot.child(CurrentUserId).hasChild("name"))) {
                            String retrieveUserName = dataSnapshot.child(CurrentUserId).child("name").getValue().toString();
                            String retrievestatus = dataSnapshot.child(CurrentUserId).child("status").getValue().toString();
                           String retrieveNumber = dataSnapshot.child(CurrentUserId).child("phonenumber").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievestatus);
                           userNumber.setText(retrieveNumber);

                        } else {

                            Toast.makeText(SettingsActivity.this, "Please update your profile information", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void Updatesettings() {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();
        String setUserNumber = userNumber.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserStatus)) {
            Toast.makeText(this, "please add your status", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserNumber)) {
           Toast.makeText(this, "please enter your number", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> profileMap = new HashMap<>();

            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
          profileMap.put("phonenumber", setUserNumber);


            RootRef.child("Users").child(CurrentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                SendUserToMainActivity();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void initializeFields() {
        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userNumber = (EditText) findViewById(R.id.set_phonenumber);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);


    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagUri = data.getData();
            userProfileImage.setImageURI(ImagUri);



        }
        if (uploadTask != null && uploadTask.isInProgress())
        {
            Toast.makeText(this, "upload in progress", Toast.LENGTH_SHORT).show();


        }else
        {


            final String setUserName = userName.getText().toString();
            final String setUserStatus = userStatus.getText().toString();
            final String setUserNumber = userNumber.getText().toString();

            final StorageReference Ref = UserProfileImageRef.child(System.currentTimeMillis() + "." + getExtension(ImagUri));
            uploadTask = Ref.putFile(ImagUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(SettingsActivity.this, "iMAGE UPLOADED sUCCESFULLY", Toast.LENGTH_SHORT).show();

                            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    DatabaseReference imagestore=FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserId);
                                    HashMap<String,String>hashMap=new HashMap<>();
                                    hashMap.put("name", setUserName);
                                    hashMap.put("status", setUserStatus);
                                    hashMap.put("phonenumber", setUserNumber);
                                    hashMap.put("imageurl", String.valueOf(uri));

                                    imagestore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SettingsActivity.this, "finally completsd", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                }
                            });




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });



        }
    }
}






