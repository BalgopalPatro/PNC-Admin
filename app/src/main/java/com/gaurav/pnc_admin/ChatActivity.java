package com.gaurav.pnc_admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.pnc_admin.R;
import com.gaurav.pnc_admin.Adapters.MessageAdapter;
import com.gaurav.pnc_admin.Models.Messages;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private final List<Messages> messagesList = new ArrayList<>();
    private String messagereceiverid, messagereceivername, messagereceiverimage, messagesenderid;
    private TextView username, userlastseen;
    private CircleImageView userimage;
    private Toolbar chat_toolbar;
    private ImageButton sendmessagebutton, sendfilesbutton;
    private EditText messageinputtext;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private String savecurrenttime, savecurrentdate;
    private String checker = "", myurl = "", myfileurl = "";
    private StorageTask uploadTask, uploadfiletask;
    private Uri fileUri;

    private boolean onactivityresulty = false, comple = false;
    private int ssss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messagesenderid = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();

        messagereceiverid = getIntent().getExtras().get("visit_user_id").toString();
        messagereceivername = getIntent().getExtras().get("visit_user_name").toString();
        //messagereceiverimage = getIntent().getExtras().get("visit_image").toString();

        initialisecontrollers();
        getSupportActionBar().setTitle(messagereceivername);
        //username.setText(messagereceivername);
        //Picasso.get().load(messagereceiverimage).placeholder(R.drawable.profile_image).into(userimage);

        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
        //DisplayLastSeen();
        sendfilesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] options = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "MS Word Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Image"), 111);
                        }
                        if (i == 1) {
                            checker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(Intent.createChooser(intent, "Select PDF file"), 111);
                        }
                        if (i == 2) {
                            checker = "docx";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(Intent.createChooser(intent, "Select Worf file"), 111);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void initialisecontrollers() {
        sendmessagebutton = findViewById(R.id.send_message_btn);
        sendfilesbutton = findViewById(R.id.send_files);
        messageinputtext = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        savecurrentdate = currentdate.format(calender.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
        savecurrenttime = currenttime.format(calender.getTime());
        onactivityresulty = false;
        comple = false;
    }

    @Override
    protected void onStart() {
        messagesList.clear();
        comple = false;
        rootref.child("Messages").child(messagesenderid).child(messagereceiverid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                        comple = true;
                        edit();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        super.onStart();
    }

    private void edit() {
        if (onactivityresulty && comple && ((ssss + 2) == userMessagesList.getAdapter().getItemCount())) {
            messagesList.remove(userMessagesList.getAdapter().getItemCount() - 1);
            messageAdapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog lb;
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            lb = new ProgressDialog(this);
            lb.setTitle("Sending...");
            lb.setMessage("Please Wait");
            lb.setCanceledOnTouchOutside(false);
            lb.show();

            fileUri = data.getData();
            if (!checker.equals("image")) {
                onactivityresulty = true;
                ssss = userMessagesList.getAdapter().getItemCount();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document files");
                final String messagesenderref = "Messages/" + messagesenderid + "/" + messagereceiverid;
                final String messagereceiverref = "Messages/" + messagereceiverid + "/" + messagesenderid;

                DatabaseReference usermessagekeyref = rootref.child("Messages")
                        .child(messagesenderid).child(messagereceiverid).push();

                final String messagepushid = usermessagekeyref.getKey();
                final StorageReference filePath = storageReference.child(messagepushid + "." + checker);

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
                            Map<String, String> messagetextbody1 = new HashMap<String, String>();
                            messagetextbody1.put("message", myfileurl);
                            messagetextbody1.put("name", fileUri.getLastPathSegment());
                            messagetextbody1.put("type", checker);
                            messagetextbody1.put("from", messagesenderid);
                            messagetextbody1.put("to", messagereceiverid);
                            messagetextbody1.put("messageID", messagepushid);
                            messagetextbody1.put("time", savecurrenttime);
                            messagetextbody1.put("date", savecurrentdate);

                            String sendermsg1 = messagesenderref + "/" + messagepushid;
                            String receivermsg1 = messagereceiverref + "/" + messagepushid;

                            FirebaseDatabase mdata = FirebaseDatabase.getInstance();

                            DatabaseReference ref = mdata.getReference(sendermsg1);
                            ref.setValue(messagetextbody1);

                            DatabaseReference ref1 = mdata.getReference(receivermsg1);
                            ref1.setValue(messagetextbody1);
                            lb.dismiss();
                        }
                    }
                });
                uploadfiletask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lb.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (checker.equals("image")) {
                onactivityresulty = true;
                ssss = userMessagesList.getAdapter().getItemCount();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image files");
                final String messagesenderref = "Messages/" + messagesenderid + "/" + messagereceiverid;
                final String messagereceiverref = "Messages/" + messagereceiverid + "/" + messagesenderid;

                DatabaseReference usermessagekeyref = rootref.child("Messages")
                        .child(messagesenderid).child(messagereceiverid).push();

                final String messagepushid = usermessagekeyref.getKey();
                final StorageReference filePath = storageReference.child(messagepushid + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadurl = task.getResult();
                            myurl = downloadurl.toString();

                            Map<String, String> messagetextbody2 = new HashMap<>();
                            messagetextbody2.put("message", myurl);
                            messagetextbody2.put("name", fileUri.getLastPathSegment());
                            messagetextbody2.put("type", checker);
                            messagetextbody2.put("from", messagesenderid);
                            messagetextbody2.put("to", messagereceiverid);
                            messagetextbody2.put("messageID", messagepushid);
                            messagetextbody2.put("time", savecurrenttime);
                            messagetextbody2.put("date", savecurrentdate);

                            Map<String, Object> messagebodydetails = new HashMap<>();
                            messagebodydetails.put(messagesenderref + "/" + messagepushid, messagetextbody2);
                            messagebodydetails.put(messagereceiverref + "/" + messagepushid, messagetextbody2);

                            rootref.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        //update_chat();
                                        lb.dismiss();
                                    } else {
                                        lb.dismiss();
                                        String msg = task.getException().toString();
                                        Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    messageinputtext.setText("");
                                }
                            });
                        }
                    }
                });
            } else {
                onactivityresulty = false;
                ssss = userMessagesList.getAdapter().getItemCount();
                lb.dismiss();
                Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendmessage() {
        String messagetext = messageinputtext.getText().toString().trim();
        if (TextUtils.isEmpty(messagetext)) {
        } else {
            String messagesenderref = "Messages/" + messagesenderid + "/" + messagereceiverid;
            String messagereceiverref = "Messages/" + messagereceiverid + "/" + messagesenderid;

            DatabaseReference usermessagekeyref = rootref.child("Messages")
                    .child(messagesenderid).child(messagereceiverid).push();

            String messagepushid = usermessagekeyref.getKey();
            Map<String, String> messagetextbody = new HashMap<>();
            messagetextbody.put("message", messagetext);
            messagetextbody.put("type", "text");
            messagetextbody.put("from", messagesenderid);
            messagetextbody.put("to", messagereceiverid);
            messagetextbody.put("messageID", messagepushid);
            messagetextbody.put("time", savecurrenttime);
            messagetextbody.put("date", savecurrentdate);

            Map<String, Object> messagebodydetails = new HashMap<>();
            messagebodydetails.put(messagesenderref + "/" + messagepushid, messagetextbody);
            messagebodydetails.put(messagereceiverref + "/" + messagepushid, messagetextbody);

            rootref.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                    } else {
                        String msg = task.getException().toString();
                        Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    messageinputtext.setText("");
                }
            });
        }
    }
}