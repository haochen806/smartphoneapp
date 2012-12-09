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

public class WelcomeActivity extends Activity {
	private static final String Tag = "WelcomeActivity";
	ImageView pic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d(Tag,"in onCreate welcome");
        
       // pic = (ImageView)findViewById(R.id.startPic);
        
       /* Date date1 = new Date();
        Date date2 = new Date();
        while((date2.getTime() - date1.getTime()) < 1000) {
        	date2 = new Date();
        	Log.d(Tag, "in create data");
        }*/
        
        //pic.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.start));
        /*
        try {
        	Thread.currentThread().sleep(10000);
        } catch(Exception e){
        	Log.e(Tag, e.toString());
        }
        */
       /* date1 = new Date();
        date2 = new Date();
        while((date2.getTime() - date1.getTime()) < 5000) {
        	date2 = new Date();
        	Log.d(Tag, "in create data 222222");
        }*/
        
        //startActivity(new Intent("android.intent.action.SIGNIN"));
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