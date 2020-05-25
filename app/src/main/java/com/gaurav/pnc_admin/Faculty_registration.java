package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Faculty_registration extends AppCompatActivity {

    private EditText nameinput, emailinput, infoinput;
    private Button registerinput;
    private String phoneNumber;
    private ProgressDialog lb;

    private FirebaseAuth mAuth;
    private DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_registration);
        lb = new ProgressDialog(this);
        initialize();
        phoneNumber = getIntent().getStringExtra("phn").trim();

        registerinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lb.setTitle("Please wait");
                lb.show();

                String currentuserid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                String name = nameinput.getText().toString().trim();
                String email = emailinput.getText().toString().trim();
                String info = infoinput.getText().toString().trim();

                HashMap<String, Object> onlineStatemap = new HashMap<>();
                onlineStatemap.put("name", name);
                onlineStatemap.put("email", email);
                onlineStatemap.put("info", info);
                onlineStatemap.put("phone", phoneNumber);
                onlineStatemap.put("membership", "demo");
                onlineStatemap.put("designation", "faculty");

                userref.child("Users").child(currentuserid)
                        .updateChildren(onlineStatemap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    lb.dismiss();
                                    Toast.makeText(Faculty_registration.this, "Faculty registered success", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    finish();
                                } else {
                                    lb.dismiss();
                                    Toast.makeText(Faculty_registration.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference();

        nameinput = findViewById(R.id.name_get);
        emailinput = findViewById(R.id.email_get);
        infoinput = findViewById(R.id.info_get);
        registerinput = findViewById(R.id.register_button);
    }
}
