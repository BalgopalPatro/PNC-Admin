package com.gaurav.pnc_admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Adapters.Edit_Access_adapter;
import com.gaurav.pnc_admin.Models.User_info;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Edit_Access extends AppCompatActivity {

    private RecyclerView StudentsRecyclerList;
    private Edit_Access_adapter adapter;
    private List<User_info> Students;
    private DatabaseReference user_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_access_activity);

        getSupportActionBar().setTitle("Students");

        StudentsRecyclerList = findViewById(R.id.students_recyclerlist);
        StudentsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        inflate_recyclerview();
    }

    private void inflate_recyclerview() {
        Students = new ArrayList<>();
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
                    if (designation.equalsIgnoreCase("student")) {
                        Students.add(fac);
                    }
                }
                adapter = new Edit_Access_adapter(Edit_Access.this, Students);
                StudentsRecyclerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}