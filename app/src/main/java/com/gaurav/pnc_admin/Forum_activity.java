package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gaurav.pnc_admin.Models.User_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class Forum_activity extends AppCompatActivity {

    private View privatechatview;
    private RecyclerView chatList;
    private DatabaseReference chatsref, usersref;
    private FirebaseAuth mAuth;
    private String currentuserid;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        chatsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserid);
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");

        chatList = findViewById(R.id.Chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_faculty, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.find_faculty_option) {
            Intent findfaculty = new Intent(Forum_activity.this, Find_faculty.class);
            startActivity(findfaculty);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingbar = new ProgressDialog(this);
        loadingbar.setTitle("Loading");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        FirebaseRecyclerOptions<User_info> options =
                new FirebaseRecyclerOptions.Builder<User_info>()
                        .setQuery(chatsref, User_info.class)
                        .build();
        FirebaseRecyclerAdapter<User_info, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<User_info, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull User_info model) {
                        final String userIDS = getRef(position).getKey();
                        loadingbar.dismiss();
                        usersref.child(userIDS).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final String retname = dataSnapshot.child("name").getValue().toString();
                                    final String retinfo = dataSnapshot.child("info").getValue().toString();
                                    holder.username.setText(retname);
                                    holder.userinfo.setText(retinfo);
                                    loadingbar.dismiss();

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatintent = new Intent(getApplicationContext(), ChatActivity.class);
                                            chatintent.putExtra("visit_user_id", userIDS);
                                            chatintent.putExtra("visit_user_name", retname);
                                            startActivity(chatintent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatList.setAdapter(adapter);
        adapter.startListening();
        loadingbar.dismiss();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        TextView username, userinfo;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userinfo = itemView.findViewById(R.id.user_info);
        }
    }
}
