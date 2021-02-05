package com.example.chatme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;



public class FriendsActivity extends AppCompatActivity {


    private RecyclerView myContactsList;

    private DatabaseReference ContacsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        Slidr.attach(this);

        myContactsList = (RecyclerView) findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        ContacsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference();





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<contacts>()
                        .setQuery(ContacsRef, contacts.class)
                        .build();


        final FirebaseRecyclerAdapter<contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull contacts model)
            {
           final   String userIDs = getRef(position).getKey();

                UsersRef.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.child(userIDs).child("userState").hasChild("state"))
                            {
                                String state = dataSnapshot.child(userIDs).child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child(userIDs).child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child(userIDs).child("userState").child("time").getValue().toString();

                                if (state.equals("online"))
                                {
                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if (state.equals("offline"))
                                {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }
                            else
                            {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }


                            if (dataSnapshot.child(userIDs).hasChild("imageurl"))
                            {
                                String userImage = dataSnapshot.child(userIDs).child("imageurl").getValue().toString();
                                String profileName = dataSnapshot.child(userIDs).child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child(userIDs).child("status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.with(FriendsActivity.this).load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);



                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        String visit_user_id = getRef(position).getKey();

                                        Intent profileIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
                                        profileIntent.putExtra("visit_user_id", visit_user_id);
                                        startActivity(profileIntent);
                                    }
                                });
                            }
                            else
                            {
                                String profileName = dataSnapshot.child(userIDs).child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child(userIDs).child("status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        String visit_user_id = getRef(position).getKey();

                                        Intent profileIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
                                        profileIntent.putExtra("visit_user_id", visit_user_id);
                                        startActivity(profileIntent);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;


        public ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status_name);
            profileImage = itemView.findViewById(R.id.users_profile_images);
            onlineIcon = (ImageView) itemView.findViewById(R.id.online_status);
        }
    }
}
