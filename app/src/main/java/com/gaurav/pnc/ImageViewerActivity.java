package com.gaurav.pnc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {
    private ImageView imageView;
    private String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        getSupportActionBar().setTitle("Image");

        imageView = findViewById(R.id.imageviewer);
        imageurl = getIntent().getStringExtra("url");
        Picasso.get().load(imageurl).into(imageView);
    }
}
