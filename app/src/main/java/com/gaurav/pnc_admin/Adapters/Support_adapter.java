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

import com.gaurav.pnc_admin.ChatActivity;
import com.gaurav.pnc_admin.Models.User_info;
import com.gaurav.pnc_admin.R;

import java.util.List;

public class Support_adapter extends RecyclerView.Adapter<Support_adapter.viewholder> {

    Context mCtx;
    List<User_info> admins;

    public Support_adapter(Context mCtx, List<User_info> admins) {
        this.mCtx = mCtx;
        this.admins = admins;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.users_display_layout, parent, false);
        viewholder holder = new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final User_info info = admins.get(position);
        holder.name.setText(info.getName());
        holder.info.setText(info.getInfo());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String visit_user_id = info.getId();
                String visit_name = info.getName();
                /*Intent profileintent = new Intent(view.getContext(), ProfileActivity.class);
                profileintent.putExtra("visit_user_id", visit_user_id);
                view.getContext().startActivity(profileintent);*/
                Intent chatintent = new Intent(view.getContext(), ChatActivity.class);
                chatintent.putExtra("visit_user_id", visit_user_id);
                chatintent.putExtra("visit_user_name", visit_name);
                view.getContext().startActivity(chatintent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    public class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, info;
        private CardView card;
        private Support_onclick_listener itemClick;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_profile_name);
            info = itemView.findViewById(R.id.user_info);
            card = itemView.findViewById(R.id.user_display_cardview);
        }

        public void setItemClickListener(Support_onclick_listener itemClickListener) {
            this.itemClick = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClick.onClick(v, getAdapterPosition());
        }
    }
}
