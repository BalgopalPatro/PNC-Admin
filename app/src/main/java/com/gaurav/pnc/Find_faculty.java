package com.gaurav.pnc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.gaurav.pnc.Adapters.Find_faculty_adapter;
import com.gaurav.pnc.Models.User_info;
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

    /*@Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User_info> options = new FirebaseRecyclerOptions.Builder<User_info>()
                .setQuery(UserRef, User_info.class)
                .build();

        FirebaseRecyclerAdapter<User_info, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<User_info, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull User_info model) {
                        holder.username.setText(model.getName());
                        holder.userinfo.setText(model.getInfo());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();
                                Intent profileintent = new Intent(Find_faculty.this, ProfileActivity.class);
                                profileintent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileintent);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFriendRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        TextView username, userinfo;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userinfo = itemView.findViewById(R.id.user_info);
        }
    }*/
}