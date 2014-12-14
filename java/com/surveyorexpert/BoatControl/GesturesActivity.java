package com.surveyorexpert.BoatControl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bornander.gestures.R;

public class GesturesActivity extends Activity {


    private TextView tvLoc;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.advert);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.boatmj);
    //    View view = new SandboxView(this, bitmap);
        View view = new SimpleView(this, bitmap);

        //   tvLoc = (TextView)findViewById(R.id.tvLocation);
     //   tvLoc.setText("Hello");
     //   setContentView(R.layout.main);

        setContentView(view);
    }
}