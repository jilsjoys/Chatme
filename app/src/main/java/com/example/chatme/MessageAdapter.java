package com.example.chatme;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.startActivity;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    Context context;


    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;

        public ImageView messageSenderPicture, messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);

            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }

    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int position)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
     //   messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);


        if (Objects.equals(fromMessageType, "text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage() );
                //+ "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
            else
            {
             //   messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage() );
                //+ "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        }
        else if(Objects.equals(fromMessageType, "image"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.with(context).load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

            }
            else
                {

                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                }
        }

        if(fromUserID.equals(messageSenderId))
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(position).getType().equals("image"))
                    {
                        Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                        intent.putExtra("url", userMessagesList.get(position).getMessage());
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
        else
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Objects.equals(userMessagesList.get(position).getType(), "image"))
                    {
                        Intent intent=new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                        intent.putExtra("url",userMessagesList.get(position).getMessage());
                        messageViewHolder.itemView.getContext().startActivity(intent);

                    }
                }
            });
        }


        if(fromUserID.equals(messageSenderId))
        {
            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public  boolean onLongClick(View view) {
                    if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Delete for everyone",
                                        "Cancel"

                                };
                        AlertDialog.Builder buider = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        buider.setTitle("Delete message ?");
                        buider.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                   deleteSendMessage(position, messageViewHolder);
                                   Intent intent = new Intent(messageViewHolder.itemView.getContext(),ChatActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);



                                } else if (i == 1) {
                                    deleteMessageForEveryone(position, messageViewHolder);
                                  Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                 messageViewHolder.itemView.getContext().startActivity(intent);


                                }

                            }


                        });
                        buider.show();
                    } else if (Objects.equals(userMessagesList.get(position).getType(), "image")) {

                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Delete for everyone",
                                        "Cancel"

                                };
                        AlertDialog.Builder buider = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        buider.setTitle("Delete message ?");
                        buider.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    deleteSendMessage(position, messageViewHolder);
                                   Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                   messageViewHolder.itemView.getContext().startActivity(intent);

                                }


                                else if (i == 1) {
                                    deleteMessageForEveryone(position, messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);


                                }


                            }
                        });
                        buider.show();





                    }
                    return true;
                }
            });
        }
        else
        {
            messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view)
                {
                    if(Objects.equals(userMessagesList.get(position).getType(), "text"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete for me",

                                        "Cancel"

                                };
                        AlertDialog.Builder buider =new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        buider.setTitle("Delete message ?");
                        buider.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }



                            }
                        });
                        buider.show();
                    }
                    else  if(Objects.equals(userMessagesList.get(position).getType(), "image"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {


                                        "Delete for me",

                                        "Cancel"

                                };
                        AlertDialog.Builder buider =new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        buider.setTitle("Delete message ?");
                        buider.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {

                                 if(i==0)
                                {
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent=new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);


                                }


                            }
                        });
                        buider.show();
                    }
return true;
                }
            });

        }
    }







    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }
    private  void deleteSendMessage(final int position,final MessageViewHolder holder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue();



    }
    private  void deleteReceiveMessage(final int position,final MessageViewHolder holder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue();

    }
    private  void deleteMessageForEveryone(final int position,final MessageViewHolder holder)
    {
        final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    rootRef.child("Messages").child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue();

                }

            }
        });

    }



}
