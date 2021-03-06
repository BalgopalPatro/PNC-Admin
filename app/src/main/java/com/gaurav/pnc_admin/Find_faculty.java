package com.gaurav.pnc_admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Adapters.Find_faculty_adapter;
import com.gaurav.pnc_admin.Models.User_info;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Find_faculty extends AppCompatActivity {

    private RecyclerView FindFriendRecyclerList;
    private Find_faculty_adapter adapter;
    private List<User_info> faculty;
    private DatabaseReference user_ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_faculty);
        getSupportActionBar().setTitle("Find Faculty");

        FindFriendRecyclerList = findViewById(R.id.find_friend_recyclerlist);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        inflate_recyclerview();
    }

    private void inflate_recyclerview() {
        faculty = new ArrayList<>();
        user_ref = FirebaseDatabase.getInstance().getReference("Users");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User_info p = snap.getValue(User_info.class);
                    User_info fac = new User_info();

                    String name = p.getName();
                    String info = p.getInfo();
                    String designation = p.getDesignation();
                    String id = snap.getKey();

                    fac.setName(name);
                    fac.setInfo(info);
                    fac.setDesignation(designation);
                    fac.setId(id);
                    if (!designation.equalsIgnoreCase("student")) {
                        faculty.add(fac);
                    }
                }
                adapter = new Find_faculty_adapter(Find_faculty.this, faculty);
                FindFriendRecyclerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}