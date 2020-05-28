package com.gaurav.pnc_admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Adapters.Support_adapter;
import com.gaurav.pnc_admin.Models.User_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Support extends AppCompatActivity {

    private RecyclerView adminsRecyclerList;
    private Support_adapter adapter;
    private List<User_info> admins;
    private DatabaseReference user_ref;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Support: Admins list");

        adminsRecyclerList = findViewById(R.id.support_recyclerview);
        adminsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        inflate_recyclerview();
    }

    private void inflate_recyclerview() {
        admins = new ArrayList<>();
        user_ref = FirebaseDatabase.getInstance().getReference("Users");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User_info p = snap.getValue(User_info.class);
                    User_info ad = new User_info();

                    String name = p.getName();
                    String info = p.getInfo();
                    String designation = p.getDesignation();
                    String id = snap.getKey();

                    ad.setName(name);
                    ad.setInfo(info);
                    ad.setDesignation(designation);
                    ad.setId(id);
                    if (designation.equalsIgnoreCase("admin") && !(mAuth.getCurrentUser().getUid().equalsIgnoreCase(id))) {
                        admins.add(ad);
                    }
                }
                adapter = new Support_adapter(Support.this, admins);
                adminsRecyclerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
