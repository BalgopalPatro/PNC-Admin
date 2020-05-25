package com.gaurav.pnc_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class VideoList extends AppCompatActivity {

    private TextView fullTitle ;
    private String CourseName,subject,chapter;
private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        CourseName = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");
        chapter = getIntent().getStringExtra("Chapter");

        fullTitle = findViewById(R.id.fullTitle);

        fullTitle.setText("Display the video list of "+CourseName+", "+subject+", "+chapter);


        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlayVideo.class);
                startActivity(i);
            }
        });
        getSupportActionBar().setTitle("Videos");
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
                Toast.makeText(getApplicationContext(),"Add Video",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
