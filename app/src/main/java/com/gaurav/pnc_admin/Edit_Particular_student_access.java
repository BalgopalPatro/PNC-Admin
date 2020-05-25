package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Adapters.edit_access_course_list_adapter;
import com.gaurav.pnc_admin.Models.Course_list_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Edit_Particular_student_access extends AppCompatActivity {

    private RecyclerView courses;
    private List<Course_list_model> list_courses = new ArrayList<>();
    private edit_access_course_list_adapter adapter;
    private ProgressDialog pd;

    private FirebaseAuth mAuth;
    private String student_user_id;
    private DatabaseReference userref, course_list_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_particular_student_access);
        pd = new ProgressDialog(this);
        pd.setTitle("Loading");
        pd.show();

        student_user_id = getIntent().getStringExtra("student_id");
        //getSupportActionBar().setTitle(student_user_id);

        courses = findViewById(R.id.edit_access_course_recyclerlist);
        courses.setLayoutManager(new LinearLayoutManager(this));

        userref = FirebaseDatabase.getInstance().getReference("Users").child(student_user_id);
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                getSupportActionBar().setTitle(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        course_list_ref = FirebaseDatabase.getInstance().getReference("Cources");
        course_list_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String name = snap.getKey();
                    Course_list_model crs = new Course_list_model();
                    crs.setCourse(name);
                    list_courses.add(crs);
                }
                adapter = new edit_access_course_list_adapter(Edit_Particular_student_access.this, list_courses, student_user_id);
                courses.setAdapter(adapter);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}