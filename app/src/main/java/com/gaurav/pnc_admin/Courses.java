package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Courses extends AppCompatActivity {

    private String cource ;
    private TextView newsubjectbtn;
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
        newsubjectbtn = findViewById(R.id.newsubjectbtn);
        getSupportActionBar().setTitle(cource);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        subjects.setLayoutManager(new GridLayoutManager(this, (int) (dpWidth/180)));

        loadSubjects();

        newsubjectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Courses.this);
                builder.setTitle("Add Subject").setCancelable(false) ;

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.new_subject_name_input, null);
                builder.setView(dialogView);

                final EditText nsname  = dialogView.findViewById(R.id.subname);
                final EditText nsiurl = dialogView.findViewById(R.id.subimglink);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!(nsname.getText().equals(null) && nsiurl.getText().equals(null))){
                            courseref.child(nsname.getText().toString()).child("img").setValue(nsiurl.getText().toString());
                            Toast.makeText(getApplicationContext(),nsname.getText()+" is added to the Course "+cource,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Give valid inputs",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nsname.setText("");
                                nsiurl.setText("");
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

    public void loadSubjects(){
        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading....!");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

