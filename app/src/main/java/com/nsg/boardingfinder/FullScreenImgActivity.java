package com.nsg.boardingfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenImgActivity extends AppCompatActivity {

    static String imageUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_img);

        ImageView imgFullSc = (ImageView) findViewById(R.id.imgFullSc);

        // Fetch data from Previous Intent ......................................
        Intent imgViewIntent = getIntent();
        if(imgViewIntent != null){
            imageUrl = imgViewIntent.getStringExtra("imageUrl");
            Picasso.get().load(imageUrl).into(imgFullSc);
        }
        //........................................................................
    }
}