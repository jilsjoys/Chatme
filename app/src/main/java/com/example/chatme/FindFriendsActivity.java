package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {



    private ImageButton mSearchBtn;
    private EditText mSearchField;
    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        Slidr.attach(this);


        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");



        mSearchField = (EditText) findViewById(R.id.search_field);


        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.result_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));



        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    private void firebaseUserSearch(String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

    FirebaseRecyclerOptions<contacts> options =
            new FirebaseRecyclerOptions.Builder<contacts>()
                    .setQuery(reference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff"),contacts.class)
                    .build();

    FirebaseRecyclerAdapter<contacts, FindFriendHolder> adapter =
                new FirebaseRecyclerAdapter<contacts, FindFriendHolder>
                        (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FindFriendHolder holder, final int position, @NonNull contacts model) {

                        holder.username.setText(model.getName());
                        holder.userstatus.setText(model.getNum());



                       Picasso.with(FindFriendsActivity.this).load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileimage);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                 startActivity(profileIntent);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public FindFriendHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindFriendHolder viewholder = new FindFriendHolder(view);
                        return viewholder;
                    }
                };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class FindFriendHolder extends RecyclerView.ViewHolder
    {

        TextView username,userstatus;
        CircleImageView profileimage;


        public FindFriendHolder(@NonNull View itemView)
        {
            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name);
            userstatus=itemView.findViewById(R.id.user_status_name);
            profileimage=itemView.findViewById(R.id.users_profile_images);

        }
    }
}
