package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Courses extends AppCompatActivity {

    private String cource ;
    private TextView cname;
    public RecyclerView subjects;
    private DatabaseReference rootref;
    private DatabaseReference courseref;

    public FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        subjects = findViewById(R.id.subjects);

        rootref = FirebaseDatabase.getInstance().getReference();
        cource = getIntent().getStringExtra("Course");
        courseref = rootref.child("Cources").child(cource);

        getSupportActionBar().setTitle(cource);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        subjects.setLayoutManager(new GridLayoutManager(this, (int) (dpWidth/180)));

        loadSubjects();
    }

    public void loadSubjects(){
        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading....!");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        Query query = courseref;
        FirebaseRecyclerOptions<Subject> options =
                new FirebaseRecyclerOptions.Builder<Subject>()
                        .setQuery(query, new SnapshotParser<Subject>() {
                            @Override
                            public Subject parseSnapshot(DataSnapshot snapshot) {
                                Log.d("My Snap :",snapshot.toString());
                                loadingBar.dismiss();
                                return new Subject(snapshot.getKey(),snapshot.child("img").getValue().toString());
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Subject, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_subject,parent,false);
                return new MyViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final Subject model) {
                holder.subname.setText(model.getName());
                Picasso.get().load(model.getImg())
                        .fit()
                        .into(holder.img);

                holder.subject_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),SubjectPageActivity.class);
                        i.putExtra("cource",cource);
                        i.putExtra("sujectName",model.getName());
                        startActivity(i);
                    }
                });

            }
        };

        subjects.setAdapter(adapter);


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


}


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subname;
        ImageView img;
        CardView subject_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subject_card = itemView.findViewById(R.id.subject_card);
            subname = itemView.findViewById(R.id.subname);
            img = itemView.findViewById(R.id.sub_img);
        }
    }

