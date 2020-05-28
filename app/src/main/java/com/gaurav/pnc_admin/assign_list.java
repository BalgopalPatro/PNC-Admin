package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.gaurav.pnc_admin.Models.StudyOrAssign;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class assign_list extends AppCompatActivity {

    private String CourseName, subject, chapter;
    private DatabaseReference rootref;
    private DatabaseReference assignListref;
    private String chapterSl;
    private ProgressDialog loadingBar;

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView assignList;
    private String title, url;
    private StorageTask uploadTask, uploadfiletask;
    private Uri fileUri;
    private String checker = "", myfileurl = "", filename = "";
    private String filename1, fileurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_list);

        CourseName = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");
        chapter = getIntent().getStringExtra("Chapter");
        chapterSl = getIntent().getStringExtra("code");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setTitle("Assignments");
        assignList = findViewById(R.id.assignList);

        assignList.setLayoutManager(new LinearLayoutManager(this));

        rootref = FirebaseDatabase.getInstance().getReference();
        assignListref = rootref.child("Cources").child(CourseName).child(subject).child("Chapters").child(chapterSl).child("assignments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadVideos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addassign, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        assignListref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    assignList.setVisibility(View.VISIBLE);
                    TextView no = findViewById(R.id.no_assign);
                    no.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_assign_option:
                addAssign();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog lb;
        if (requestCode == 151 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            lb = new ProgressDialog(this);
            lb.setTitle("Sending...");
            lb.setMessage("Please Wait");
            lb.setCanceledOnTouchOutside(false);
            lb.show();

            fileUri = data.getData();
            filename = getFileName(fileUri);

            if (!checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Cources").child(CourseName)
                        .child(subject).child("Chapters").child(chapterSl).child("assignments");

                final StorageReference filePath = storageReference.child(filename);

                uploadfiletask = filePath.putFile(fileUri);
                uploadfiletask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        lb.setMessage((int) p + " % Uploading.....");
                    }
                });
                uploadfiletask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadurl = task.getResult();
                            myfileurl = downloadurl.toString();
                            assignListref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int count = (int) dataSnapshot.getChildrenCount();
                                    assignListref.child(String.valueOf(count + 1)).child("name").setValue(filename);
                                    assignListref.child(String.valueOf(count + 1)).child("url").setValue(myfileurl);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            lb.dismiss();
                        }
                    }
                });
                uploadfiletask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lb.dismiss();
                        Toast.makeText(assign_list.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                lb.dismiss();
                Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addAssign() {
        checker = "pdf";
        String[] mimeTypes =
                {"application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "text/plain"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "Select file"), 151);
    }

    private void loadVideos() {
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading...");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();
        Query query = assignListref;

        FirebaseRecyclerOptions<StudyOrAssign> options =
                new FirebaseRecyclerOptions.Builder<StudyOrAssign>()
                        .setQuery(query, new SnapshotParser<StudyOrAssign>() {
                            @NonNull
                            @Override
                            public StudyOrAssign parseSnapshot(@NonNull DataSnapshot snapshot) {
                                loadingBar.dismiss();
                                String name = " ";
                                String url = " ";
                                int key = 0;
                                if (snapshot.hasChild("name") && snapshot.hasChild("url")) {
                                    name = snapshot.child("name").getValue().toString();
                                    url = snapshot.child("url").getValue().toString();
                                    key = Integer.parseInt(snapshot.getKey());
                                }
                                return new StudyOrAssign(name, url, key);
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<StudyOrAssign, AssignViewHolder>(options) {
            @NonNull
            @Override
            public AssignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_study_assign, parent, false);
                return new AssignViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull AssignViewHolder myVideoViewHolder, int i, @NonNull final StudyOrAssign study) {
                loadingBar.dismiss();
                filename1 = study.getSlno() + ": " + study.getName();
                fileurl = study.getUrl();
                myVideoViewHolder.name.setText(filename1);
                myVideoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), Pdf.class);
                        i.putExtra("filename", study.getName());
                        i.putExtra("fileurl", study.getUrl());
                        startActivity(i);
                    }
                });
            }
        };
        loadingBar.dismiss();
        assignList.setAdapter(adapter);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    class AssignViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public AssignViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.study_assign_name);
        }
    }
}
