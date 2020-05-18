package com.gaurav.pnc.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gaurav.pnc.Models.User_info;
import com.gaurav.pnc.ProfileActivity;
import com.gaurav.pnc.R;

import java.util.List;

public class Find_faculty_adapter extends RecyclerView.Adapter<Find_faculty_adapter.viewholder> {
    Context mCtx;
    List<User_info> faculty;

    public Find_faculty_adapter(Context mCtx, List<User_info> faculty) {
        this.mCtx = mCtx;
        this.faculty = faculty;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.users_display_layout, parent, false);
        viewholder holder = new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Find_faculty_adapter.viewholder holder, int position) {
        final User_info info = faculty.get(position);
        holder.name.setText(info.getName());
        holder.info.setText(info.getInfo());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String visit_user_id = info.getId();
                Intent profileintent = new Intent(view.getContext(), ProfileActivity.class);
                profileintent.putExtra("visit_user_id", visit_user_id);
                view.getContext().startActivity(profileintent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return faculty.size();
    }

    public class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, info;
        private CardView card;
        private Find_faculty_onClick_listener itemClick;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_profile_name);
            info = itemView.findViewById(R.id.user_info);
            card = itemView.findViewById(R.id.user_display_cardview);
        }

        public void setItemClickListener(Find_faculty_onClick_listener itemClickListener) {
            this.itemClick = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(v, getAdapterPosition());
        }
    }
}
