package com.gaurav.pnc_admin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Courses;
import com.gaurav.pnc_admin.Models.Course_list_model;
import com.gaurav.pnc_admin.R;

import java.util.List;

public class Course_list_adapter extends RecyclerView.Adapter<Course_list_adapter.viewholder> {

    Context mCtx;
    List<Course_list_model> courselist;

    public Course_list_adapter(Context mCtx, List<Course_list_model> courselist) {
        this.mCtx = mCtx;
        this.courselist = courselist;
    }

    @NonNull
    @Override
    public Course_list_adapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.course_list_row, parent, false);
        viewholder holder = new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Course_list_adapter.viewholder holder, int position) {
        final Course_list_model course = courselist.get(position);
        holder.name.setText(course.getCourse().toUpperCase());
        holder.setItemClickListener(new Couse_list_click_listener() {
            @Override
            public void onClick(View view, int position) {
                Context context = view.getContext();
                Intent yint = new Intent(view.getContext(), Courses.class);
                yint.putExtra("Course", course.getCourse().toUpperCase());
                context.startActivity(yint);

            }
        });
    }

    @Override
    public int getItemCount() {
        return courselist.size();
    }

    public class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        private Couse_list_click_listener itemClick;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.course_head);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(Couse_list_click_listener itemClickListener) {
            this.itemClick = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(v, getAdapterPosition());
        }
    }
}
