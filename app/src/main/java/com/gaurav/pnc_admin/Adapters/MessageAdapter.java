package com.gaurav.pnc_admin.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.pnc_admin.Forum_activity;
import com.gaurav.pnc_admin.ImageViewerActivity;
import com.gaurav.pnc_admin.Models.Messages;
import com.gaurav.pnc_admin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewholder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersref;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewholder messageViewholder, final int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        final Messages messages = userMessagesList.get(position);
        String fromuserid = messages.getFrom();
        String fromMessageType = messages.getType();

        usersref = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);
        usersref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverimage = dataSnapshot.child("image").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        messageViewholder.receiver_message_text_cont.setVisibility(View.GONE);
        messageViewholder.receiverMessageText.setVisibility(View.GONE);
        messageViewholder.senderMessageText.setVisibility(View.GONE);
        messageViewholder.messagesenderpicture.setVisibility(View.GONE);
        messageViewholder.messagereceiverpicture.setVisibility(View.GONE);
        messageViewholder.receiver_message_time.setVisibility(View.GONE);
        messageViewholder.sender_message_text_cont.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {
            if (fromuserid.equals(messageSenderId)) {
                messageViewholder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewholder.senderMessageText.setBackgroundResource(R.drawable.my_message);
                messageViewholder.sender_message_time.setText(messages.getTime() + " - " + messages.getDate());
                messageViewholder.senderMessageText.setText(messages.getMessage());
                messageViewholder.sender_message_text_cont.setVisibility(View.VISIBLE);
            } else {
                messageViewholder.receiver_message_text_cont.setVisibility(View.VISIBLE);
                messageViewholder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewholder.receiverMessageText.setBackgroundResource(R.drawable.their_message);
                messageViewholder.receiverMessageText.setText(messages.getMessage());
                messageViewholder.receiver_message_time.setVisibility(View.VISIBLE);
                messageViewholder.receiver_message_time.setText(messages.getTime() + " - " + messages.getDate());
            }
        }
        else if (fromMessageType.equals("image")) {
            if (fromuserid.equals(messageSenderId)) {
                messageViewholder.messagesenderpicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewholder.messagesenderpicture);
            } else {
                messageViewholder.messagesenderpicture.setVisibility(View.GONE);
                messageViewholder.messagereceiverpicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewholder.messagereceiverpicture);
            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")) {
            if (fromuserid.equals(messageSenderId)) {
                messageViewholder.messagesenderpicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatapp-10276.appspot.com/o/Image%20files%2Ffile.png?alt=media&token=fb090da2-a1cb-41c9-b19d-d03d1f30105d")
                        .into(messageViewholder.messagesenderpicture);
            } else {
                messageViewholder.messagereceiverpicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatapp-10276.appspot.com/o/Image%20files%2Ffile.png?alt=media&token=fb090da2-a1cb-41c9-b19d-d03d1f30105d")
                        .into(messageViewholder.messagereceiverpicture);
            }
        }

        if (fromuserid.equals(messageSenderId)) {
            messageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMessagesList.get(position).getType().equals("pdf") ||
                            userMessagesList.get(position).getType().equals("docx")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download and view",
                                        "Cancel",
                                        "Delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewholder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    deletesentmessage(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 3) {
                                    deletemessageforeveryone(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel",
                                        "Delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewholder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    deletesentmessage(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 2) {
                                    deletemessageforeveryone(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                }
                                //else if(position == 3){}
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("image")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "View this image",
                                        "Cancel",
                                        "Delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewholder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    deletesentmessage(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 1) {
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 3) {
                                    deletemessageforeveryone(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();
                    }
                }
            });
        } else {
            messageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download and view",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewholder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    deletereceivemessage(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("text")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewholder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    deletereceivemessage(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(position).getType().equals("image")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "View this image",
                                        "Cancel",
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewholder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0) {
                                    deletereceivemessage(position, messageViewholder);
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), Forum_activity.class);
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                } else if (i == 1) {
                                    Intent intent = new Intent(messageViewholder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    messageViewholder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void deletesentmessage(final int position, final MessageViewholder holder) {
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        rootref.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deletereceivemessage(final int position, final MessageViewholder holder) {
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        rootref.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deletemessageforeveryone(final int position, final MessageViewholder holder) {
        final DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        rootref.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    rootref.child("Messages")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue();
                    Toast.makeText(holder.itemView.getContext(), "Deleted success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MessageViewholder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText,receiver_message_time,sender_message_time;
        public ImageView messagesenderpicture, messagereceiverpicture;
        public LinearLayout receiver_message_text_cont,sender_message_text_cont;

        public MessageViewholder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            messagereceiverpicture = itemView.findViewById(R.id.message_receiver_image_view);
            messagesenderpicture = itemView.findViewById(R.id.message_sender_image_view);
            receiver_message_time = itemView.findViewById(R.id.receiver_message_time);
            receiver_message_text_cont = itemView.findViewById(R.id.receiver_message_text_cont);
            sender_message_text_cont = itemView.findViewById(R.id.sender_message_text_cont);
            sender_message_time = itemView.findViewById(R.id.sender_message_time);
        }
    }
}

