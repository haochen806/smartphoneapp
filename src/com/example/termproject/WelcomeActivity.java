package com.example.termproject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
	private static final String Tag = "WelcomeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d(Tag,"in onCreate");
        Button resume = (Button) findViewById(R.id.welcombutton);
    
        
    	resume.setOnClickListener(
    			new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					startActivity(new Intent("android.intent.action.ALLFRIENDS"));
    				}
    			}
    		);

       /* View view = findViewById(R.id.welcomeView);
        view.setOnKeyListener( new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(KeyEvent.ACTION_DOWN == event.getAction()) {
					new Intent("android.intent.action.ALLFRIENDS");
				}
				return true;
			}
        }
        );*/
    }
}