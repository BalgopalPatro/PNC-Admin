package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gaurav.pnc_admin.Models.User_info;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class login_activity extends AppCompatActivity {

    private Button sendverificationbutton, verifybutton;
    private EditText inputphonenumber, inputverificationcode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String phoneNumber;
    private ToggleSwitch switchtoggle;
    private int pos = 0;

    private DatabaseReference user_ref;
    private List<User_info> fac = fac = new ArrayList<>();

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
                pos = switchtoggle.getCheckedTogglePosition();
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
                Toast.makeText(login_activity.this, "Invalid, please enter phone number with country code"+e.getMessage(), Toast.LENGTH_SHORT).show();
                sendverificationbutton.setVisibility(View.VISIBLE);
                inputphonenumber.setVisibility(View.VISIBLE);
                switchtoggle.setVisibility(View.VISIBLE);
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
                switchtoggle.setVisibility(View.INVISIBLE);
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
        switchtoggle = findViewById(R.id.toggleswitch);
        switchtoggle.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                pos = position;
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        hideSoftKeyboard(inputverificationcode);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity();
                        } else {
                            loadingBar.dismiss();
                            String msg = task.getException().toString();
                            Toast.makeText(login_activity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        /*get_users task = new get_users();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
        fac = new ArrayList<>();
        user_ref = FirebaseDatabase.getInstance().getReference("Users");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User_info p = snap.getValue(User_info.class);
                    User_info f = new User_info();

                    String phone = p != null ? p.getPhone() : null;
                    String designation = p != null ? p.getDesignation() : null;

                    f.setPhone(phone);
                    f.setDesignation(designation);

                    fac.add(f);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final_call();
            }
        }, 10000);
    }

    protected void hideSoftKeyboard(EditText input) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    private boolean check_phone() {
        for (User_info snap : fac) {
            if (snap.getPhone().equalsIgnoreCase(phoneNumber)) {
                if (snap.getDesignation().equalsIgnoreCase("admin")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check_user() {
        for (User_info snap : fac) {
            if (snap.getPhone().equalsIgnoreCase(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private void final_call() {
        loadingBar.dismiss();
        switch (pos) {
            case 0:
                boolean che = check_user();
                if (che) {
                    Toast.makeText(getBaseContext(), "This phone number is already taken", Toast.LENGTH_SHORT).show();
                    sendverificationbutton.setVisibility(View.VISIBLE);
                    inputphonenumber.setVisibility(View.VISIBLE);
                    switchtoggle.setVisibility(View.VISIBLE);
                    verifybutton.setVisibility(View.INVISIBLE);
                    inputverificationcode.setVisibility(View.INVISIBLE);
                } else {
                    Intent registerfaculty = new Intent(login_activity.this, Faculty_registration.class);
                    registerfaculty.putExtra("phn", phoneNumber);
                    startActivity(registerfaculty);
                    sendverificationbutton.setVisibility(View.VISIBLE);
                    inputphonenumber.setVisibility(View.VISIBLE);
                    switchtoggle.setVisibility(View.VISIBLE);
                    verifybutton.setVisibility(View.INVISIBLE);
                    inputverificationcode.setVisibility(View.INVISIBLE);
                }
                break;
            case 1:
                if (check_phone()) {
                    Intent mainactivity = new Intent(login_activity.this, Home_activity.class);
                    startActivity(mainactivity);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "User is not a member of admin", Toast.LENGTH_SHORT).show();
                    sendverificationbutton.setVisibility(View.VISIBLE);
                    inputphonenumber.setVisibility(View.VISIBLE);
                    switchtoggle.setVisibility(View.VISIBLE);
                    verifybutton.setVisibility(View.INVISIBLE);
                    inputverificationcode.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }
}