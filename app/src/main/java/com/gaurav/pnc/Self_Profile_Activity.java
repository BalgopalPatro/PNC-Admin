package com.gaurav.pnc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gaurav.pnc.Models.User_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Self_Profile_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private String currentuserid;

    private EditText name, phone, design, member;
    private Button updatebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_profile_);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();

        initializefields();
        retrieveuserinfo();

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = name.getText().toString().trim();
                rootref.child("Users").child(currentuserid).child("name").setValue(user_name);
                finish();
            }
        });
    }

    private void retrieveuserinfo() {
        rootref.child("Users").child(currentuserid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User_info info = dataSnapshot.getValue(User_info.class);
                            name.setText(info.getName());
                            phone.setText(info.getPhone());
                            design.setText(info.getDesignation());
                            member.setText(info.getMembership());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void initializefields() {
        name = findViewById(R.id.fullname);
        phone = findViewById(R.id.phone_ui);
        design = findViewById(R.id.designation_edittext);
        member = findViewById(R.id.membership_edittext);
        updatebutton = findViewById(R.id.update_button);
    }
}
