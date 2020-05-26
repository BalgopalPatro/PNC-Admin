package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.gaurav.pnc_admin.Models.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SubjectPageActivity extends AppCompatActivity {

    private String Course,subject;
    RecyclerView chapterlist;
    private DatabaseReference rootref;
    private DatabaseReference chapteref;
    public FirebaseRecyclerAdapter adapter;

    private TextView nchapaddbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_page);

        chapterlist = findViewById(R.id.chapterlist);
        chapterlist.setLayoutManager(new LinearLayoutManager(this));
        nchapaddbtn = findViewById(R.id.nchapaddbtn);
        Course = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");

        rootref = FirebaseDatabase.getInstance().getReference();
        chapteref = rootref.child("Cources").child(Course).child(getIntent().getStringExtra("sujectName")).child("Chapters");

        getSupportActionBar().setTitle(getIntent().getStringExtra("sujectName"));

        loadChapterList();

        nchapaddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SubjectPageActivity.this);
                builder.setTitle("Enter Chapter Name").setCancelable(false) ;

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.new_chapter_name_input, null);
                builder.setView(dialogView);

                final EditText nchname  = dialogView.findViewById(R.id.newchaptername);


                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!nchname.getText().toString().isEmpty()){
                            chapteref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int count = (int) dataSnapshot.getChildrenCount();
                                    chapteref.child(String.valueOf(count+1)).child("name").setValue(nchname.getText().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(),"Give valid inputs",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nchname.setText("");
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog =builder.create();
                ColorDrawable back = new ColorDrawable(Color.WHITE);
                InsetDrawable inset = new InsetDrawable(back, 20);
                alertDialog.getWindow().setBackgroundDrawable(inset);
                alertDialog.show();
            }
        });

    }

    private void loadChapterList() {

        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading...");
        loadingBar.setMessage("Please Wait");


        chapteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    loadingBar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = chapteref;
        FirebaseRecyclerOptions<Chapter> options =
                new FirebaseRecyclerOptions.Builder<Chapter>()
                        .setQuery(query, new SnapshotParser<Chapter>() {
                            @Override
                            public Chapter parseSnapshot(DataSnapshot snapshot) {
                                loadingBar.dismiss();
                                return new Chapter(snapshot.child("name").getValue().toString(),Integer.parseInt(snapshot.getKey()) );
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Chapter, MyChapterViewHolder>(options) {

            @NonNull
            @Override
            public MyChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_chapter, parent, false);
                return new MyChapterViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MyChapterViewHolder holder, final int position, @NonNull final Chapter model) {
                holder.chaptername.setText(model.getName());
                holder.chaptername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Clicked " + model.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

                holder.Videobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), VideoList.class);
                        i.putExtra("cource",Course);
                        i.putExtra("sujectName",subject);
                        i.putExtra("Chapter",model.getName());
                        i.putExtra("code", (model.getSlno())+"");
                        startActivity(i);
                    }
                });

                holder.chaptername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.isExpanded()) {
                            TransitionManager.beginDelayedTransition(holder.fullCard, new AutoTransition());
                            holder.expndView.setVisibility(View.GONE);
                            holder.chaptername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                            model.setExpanded(!model.isExpanded());
                        } else {
                            TransitionManager.beginDelayedTransition(holder.fullCard, new AutoTransition());
                            holder.expndView.setVisibility(View.VISIBLE);
                            holder.chaptername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                            model.setExpanded(!model.isExpanded());
                        }
                    }
                });
            }
        };
        chapterlist.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    class MyChapterViewHolder extends RecyclerView.ViewHolder {

        TextView chaptername, Videobtn, studyMatbtn, AssignmentBtn;
        LinearLayout expndView;
        RelativeLayout fullCard;

        public MyChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chaptername = itemView.findViewById(R.id.chaptername);
            expndView = itemView.findViewById(R.id.ExpandView);
            fullCard = itemView.findViewById(R.id.fullCard);
            Videobtn = itemView.findViewById(R.id.Videobtn);
            studyMatbtn = itemView.findViewById(R.id.studyMatbtn);
            AssignmentBtn = itemView.findViewById(R.id.AssignmentBtn);

            AssignmentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Not Available Now", Toast.LENGTH_SHORT).show();
                }
            });
            studyMatbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Not Available Now", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}