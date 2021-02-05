package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

private Toolbar mToolbar;
private ViewPager myviewPager;
private TabLayout myTabLayout;
private TabsAccessorAdapter myTabsAccessorAdapter;
private FirebaseUser currentUser;
private FirebaseAuth mAuth;
private DatabaseReference RootRef;
    private String currentUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


        RootRef= FirebaseDatabase.getInstance().getReference();



       mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);


       setSupportActionBar(mToolbar);
       getSupportActionBar().setTitle("ChatMe");

       myviewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
       myTabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout=(TabLayout)findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myviewPager);




    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart()
    {
        FirebaseUser currentUser =mAuth.getCurrentUser();
        super.onStart();
        if(currentUser==null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            updateUserStatus("online");
            VerifyUserExistence();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop()
    {
        super.onStop();
        FirebaseUser currentUser =mAuth.getCurrentUser();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FirebaseUser currentUser =mAuth.getCurrentUser();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    private void VerifyUserExistence()
    {
        String CurrentUserId =mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists()))
                {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






    private void SendUserToLoginActivity()
    {
        Intent loginIntent =new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToSettingsActivity()
    {
        Intent settingIntent =new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingIntent);

    }
    private void SendUserToFindfriendsActivity()
    {
        Intent friendsIntent =new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(friendsIntent);

    }
    private void SendUserToFriendsActivity()
    {
        Intent myfriendsIntent =new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(myfriendsIntent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()==R.id.main_logout_option)
        {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if(item.getItemId()==R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.main_find_friends_option)
        {

            SendUserToFindfriendsActivity();
        }
        if(item.getItemId()==R.id.my_friends)
        {

            SendUserToFriendsActivity();
        }



       return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}
