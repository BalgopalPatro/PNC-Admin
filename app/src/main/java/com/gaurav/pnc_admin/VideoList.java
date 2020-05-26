package com.gaurav.pnc_admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.gaurav.pnc_admin.Models.Video;
import com.gaurav.pnc_admin.config.YouTubeConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoList extends AppCompatActivity {


    private String CourseName,subject,chapter;
    private DatabaseReference rootref;
    private DatabaseReference vdoListref;
    private String chapterSl ;

    public FirebaseRecyclerAdapter adapter;
    private RecyclerView videoList;

private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        CourseName = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");
        chapter = getIntent().getStringExtra("Chapter");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setTitle("Videos");
        videoList = findViewById(R.id.videoList);

        videoList.setLayoutManager(new LinearLayoutManager(this));
        chapterSl = getIntent().getStringExtra("code");

        rootref = FirebaseDatabase.getInstance().getReference();
        vdoListref = rootref.child("Cources").child(CourseName).child(subject).child("Chapters").child(chapterSl).child("video");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadVideos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addvideo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_video_option:
                addVideo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoList.this);
        builder.setTitle("Add new Video").setCancelable(false) ;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_video_add_input, null);
        builder.setView(dialogView);

        final EditText  Vtitle = dialogView.findViewById(R.id.Vtitle);
        final EditText  Vlink = dialogView.findViewById(R.id.Vlink);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!(Vlink.getText().toString().isEmpty()) && !(Vtitle.getText().toString().isEmpty())  ){
                    vdoListref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            vdoListref.child(String.valueOf(count+1)).child("name").setValue(Vtitle.getText().toString());
                            vdoListref.child(String.valueOf(count+1)).child("code").setValue(getYouTubeId(Vlink.getText().toString()));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Give valid inputfields",Toast.LENGTH_SHORT).show();
                }

            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Vlink.setText("");
                        Vtitle.setText("");
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog =builder.create();
        ColorDrawable back = new ColorDrawable(Color.WHITE);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);
        alertDialog.show();
    }

    private void loadVideos() {
        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading...");
        loadingBar.setMessage("Please Wait");


        Query query = vdoListref;

        vdoListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    loadingBar.show();
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Video> options =
                new FirebaseRecyclerOptions.Builder<Video>()
                        .setQuery(query, new SnapshotParser<Video>() {
                            @Override
                            public Video parseSnapshot(DataSnapshot snapshot) {
                                loadingBar.dismiss();
                                return new Video(snapshot.child("code").getValue().toString(),snapshot.child("name").getValue().toString(),Integer.parseInt(snapshot.getKey()));
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Video,MyVideoViewHolder>(options) {
            @NonNull
            @Override
            public MyVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_video_card, parent, false);
                return new MyVideoViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyVideoViewHolder myVideoViewHolder, int i, @NonNull final Video video) {
                myVideoViewHolder.name.setText(video.getSlno() + "."+video.getName());
                Log.d("Image Tag","https://img.youtube.com/vi/"+video.getCode()+"/mqdefault.jpg");
                Picasso.get().load("https://img.youtube.com/vi/"+video.getCode()+"/mqdefault.jpg")
                        .into(myVideoViewHolder.img);
                try {
                    myVideoViewHolder.time.setText(" "+getDuration(video.getCode()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myVideoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),PlayVideo.class);
                        i.putExtra("code",video.getCode());
                        startActivity(i);
                    }
                });
            }
        };
        videoList.setAdapter(adapter);
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

    public String getDuration(String code) throws IOException {

        final String USER_AGENT = "Mozilla/5.0";
        final String GET_URL = "https://www.googleapis.com/youtube/v3/videos?id="+code+"&part=contentDetails&key="+ new YouTubeConfig().getAPI_KEY();

        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());

            try {

                JSONObject o = new JSONObject(response.toString());

                JSONArray items = new JSONArray(o.get("items").toString());

                JSONObject contentDetails = (JSONObject) items.getJSONObject(0).get("contentDetails");

                Log.d("My App", contentDetails.get("duration").toString());

                String string2 = contentDetails.get("duration").toString();
                String t = string2.replaceAll("[PT]","");
                String t2 = t.replace("M"," : ").replace("H"," : ").replace("S","");
                return t2;

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");
            }

        } else {
            System.out.println("GET request not worked");
        }

        return "0";
    }

    public String getYouTubeId(String youTubeUrl) {
        String pattern = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


}

class MyVideoViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    ImageView img;
    TextView time;

    public MyVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.course_head);
        img = itemView.findViewById(R.id.thumb);
        time = itemView.findViewById(R.id.time);
    }
}

