package com.example.termproject;

import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


// pops up a beautiful image and wait for several seconds and redirect to viewallfriendsactivity
public class WelcomeActivity extends Activity {
	private static final String Tag = "WelcomeActivity";
	ImageView pic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d(Tag,"in onCreate welcome");
        

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent("android.intent.action.SIGNIN");
                WelcomeActivity.this.startActivity(mainIntent);
                WelcomeActivity.this.finish();
            }
        }, 5000);
    }
    
    
}