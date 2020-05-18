package com.gaurav.pnc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiveruserid, Current_state, sender_user_id;
    private CircleImageView userProfileimage;
    private TextView userProfilename, userprofileinfo;
    private Button sendmessagerequestbutton, declinechatrequest;
    private FirebaseAuth mAuth;
    private DatabaseReference userref, contactref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        contactref = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiveruserid = getIntent().getExtras().get("visit_user_id").toString();
        userProfileimage = findViewById(R.id.visit_profile_image);
        userProfilename = findViewById(R.id.visit_user_name);
        userprofileinfo = findViewById(R.id.visit_profile_info);
        sendmessagerequestbutton = findViewById(R.id.send_message_request_button);
        declinechatrequest = findViewById(R.id.decline_message_request_button);
        Current_state = "new";
        Retrieveuserinfo();
    }

    private void Retrieveuserinfo() {
        userref.child(receiveruserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (dataSnapshot.hasChild("image"))) {
                    String userimage = dataSnapshot.child("image").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userinfo = dataSnapshot.child("info").getValue().toString();

                    Picasso.get().load(userimage).placeholder(R.drawable.profile_image).into(userProfileimage);
                    userProfilename.setText(username);
                    userprofileinfo.setText(userinfo);
                    managechatrequest();
                } else {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userinfo = dataSnapshot.child("info").getValue().toString();

                    userProfilename.setText(username);
                    userprofileinfo.setText(userinfo);
                    managechatrequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void managechatrequest() {
        contactref.child(sender_user_id).child(receiveruserid).child("Contacts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //String req = dataSnapshot.getValue().toString();
                            sendmessagerequestbutton.setText("Remove from Contact");
                            Current_state = "friends";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (!sender_user_id.equals(receiveruserid)) {
            sendmessagerequestbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendmessagerequestbutton.setEnabled(false);
                    if (Current_state.equals("new")) {
                        //sendchatrequest();
                        AddToContact();
                    }
                    if (Current_state.equals("friends")) {
                        RemoveSpecificContact();
                    }
                }
            });
        } else {
            sendmessagerequestbutton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {
        contactref.child(sender_user_id).child(receiveruserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactref.child(receiveruserid).child(sender_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendmessagerequestbutton.setEnabled(true);
                                                Current_state = "new";
                                                sendmessagerequestbutton.setText("Add to Contact");

                                                declinechatrequest.setVisibility(View.INVISIBLE);
                                                declinechatrequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AddToContact() { //Accept chat request -> AddtoContact
        contactref.child(sender_user_id).child(receiveruserid)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactref.child(receiveruserid).child(sender_user_id)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendmessagerequestbutton.setEnabled(true);
                                                Current_state = "friends";
                                                sendmessagerequestbutton.setText("Remove from Contact");

                                                declinechatrequest.setVisibility(View.INVISIBLE);
                                                declinechatrequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
