package com.gaurav.pnc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gaurav.pnc.Models.User_info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class login_activity extends AppCompatActivity {

    private Button sendverificationbutton, verifybutton;
    private EditText inputphonenumber, inputverificationcode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String phoneNumber;

    private DatabaseReference user_ref;
    private List<String> fac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = FirebaseAuth.getInstance();
        initialise();

        loadingBar = new ProgressDialog(this);
        sendverificationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = inputphonenumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(login_activity.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Please wait while we authenticate your number");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            login_activity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(login_activity.this, "Invalid, please enter phone number with country code", Toast.LENGTH_SHORT).show();
                sendverificationbutton.setVisibility(View.VISIBLE);
                inputphonenumber.setVisibility(View.VISIBLE);
                verifybutton.setVisibility(View.INVISIBLE);
                inputverificationcode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                sendverificationbutton.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);
                verifybutton.setVisibility(View.VISIBLE);
                inputverificationcode.setVisibility(View.VISIBLE);
            }
        };

        verifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendverificationbutton.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);

                String verificationcode = inputverificationcode.getText().toString();
                if (TextUtils.isEmpty(verificationcode)) {
                    Toast.makeText(login_activity.this, "Enter code first", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("verification code");
                    loadingBar.setMessage("Please wait while we verify your code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });
    }

    private void initialise() {
        sendverificationbutton = findViewById(R.id.send_ver_code_button);
        verifybutton = findViewById(R.id.verify_button);
        inputphonenumber = findViewById(R.id.phone_number_input);
        inputverificationcode = findViewById(R.id.verification_code_input);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final String[] check = new String[1];
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SendUserToMMainActivity();
                        } else {
                            loadingBar.dismiss();
                            String msg = task.getException().toString();
                            Toast.makeText(login_activity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMMainActivity() {
        check_for_user();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (check_phone()) {
                    Intent mainactivity = new Intent(login_activity.this, Home_activity.class);
                    startActivity(mainactivity);
                    loadingBar.dismiss();
                } else {
                    loadingBar.dismiss();
                    DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
                    String currentuserid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    HashMap<String, Object> onlineStatemap = new HashMap<>();
                    onlineStatemap.put("phone", phoneNumber);
                    onlineStatemap.put("membership", "demo");
                    onlineStatemap.put("designation", "student");
                    onlineStatemap.put("info", "null_student");
                    rootref.child("Users").child(currentuserid)
                            .updateChildren(onlineStatemap);
                    finish();
                    Intent mainactivity = new Intent(login_activity.this, Home_activity.class);
                    startActivity(mainactivity);
                }
            }
        }, 5000);
    }

    private void check_for_user() {
        fac = new ArrayList<>();
        user_ref = FirebaseDatabase.getInstance().getReference("Users");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User_info p = snap.getValue(User_info.class);
                    User_info f = new User_info();
                    String phone = p.getPhone();
                    f.setPhone(phone);
                    Log.i("list_phone_numbers", phone);
                    fac.add(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean check_phone() {
        for (String snap : fac) {
            if (snap.equalsIgnoreCase(phoneNumber)) {
                return true;
            }
        }
        return false;
    }
}
