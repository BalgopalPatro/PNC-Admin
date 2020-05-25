package com.gaurav.pnc_admin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc_admin.Edit_Particular_student_access;
import com.gaurav.pnc_admin.Models.User_info;
import com.gaurav.pnc_admin.R;

import java.util.List;

public class Edit_Access_adapter extends RecyclerView.Adapter<Edit_Access_adapter.viewholder> {

    Context mCtx;
    List<User_info> students;

    public Edit_Access_adapter(Context mCtx, List<User_info> students) {
        this.mCtx = mCtx;
        this.students = students;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.users_display_layout, parent, false);
        viewholder holder = new Edit_Access_adapter.viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final User_info info = students.get(position);
        holder.name.setText(info.getName());
        holder.info.setText(info.getInfo());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String visit_user_id = info.getId();
                Intent profileintent = new Intent(view.getContext(), ProfileActivity.class);
                profileintent.putExtra("visit_user_id", visit_user_id);
                view.getContext().startActivity(profileintent);*/
                String id = info.getId();
                Intent edit_access_student = new Intent(view.getContext(), Edit_Particular_student_access.class);
                edit_access_student.putExtra("student_id", id);
                view.getContext().startActivity(edit_access_student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name, info;
        private CardView card;
        private Edit_Access_onClick_listener itemClick;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_profile_name);
            info = itemView.findViewById(R.id.user_info);
            card = itemView.findViewById(R.id.user_display_cardview);
        }

        public void setItemClickListener(Edit_Access_onClick_listener itemClickListener) {
            this.itemClick = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(v, getAdapterPosition());
        }
    }
}
