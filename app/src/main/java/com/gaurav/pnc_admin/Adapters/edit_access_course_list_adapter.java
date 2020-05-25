package com.gaurav.pnc_admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Models.Course_list_model;
import com.gaurav.pnc_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class edit_access_course_list_adapter extends RecyclerView.Adapter<edit_access_course_list_adapter.viewholder> {

    Context mCtx;
    List<Course_list_model> courselist;
    String userid;

    public edit_access_course_list_adapter(Context mCtx, List<Course_list_model> courselist, String userid) {
        this.mCtx = mCtx;
        this.courselist = courselist;
        this.userid = userid;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.edit_particular_access_row, parent, false);
        viewholder holder = new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        final Course_list_model course = courselist.get(position);
        holder.Course_name.setText(course.getCourse().toUpperCase());

        final DatabaseReference par_student_ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        par_student_ref.child("Course_access").child(course.getCourse())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.rby.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        holder.rby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                par_student_ref.child("Course_access").child(course.getCourse()).child("has_access").setValue("YES");
            }
        });

        holder.rbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                par_student_ref.child("Course_access").child(course.getCourse())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    par_student_ref.child("Course_access").child(course.getCourse()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return courselist.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        private TextView Course_name;
        private RadioButton rby, rbn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            Course_name = itemView.findViewById(R.id.Course_name);
            rby = itemView.findViewById(R.id.YES);
            rbn = itemView.findViewById(R.id.NO);
        }
    }
}
