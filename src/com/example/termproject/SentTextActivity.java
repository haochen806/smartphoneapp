package com.example.termproject;

import java.util.ArrayList;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


// leave text message
public class SentTextActivity extends Activity {
	Button addText;
	FriendsDatabase db;
	int friendId;
	String intentId;
	EditText editMessage;
	String TAG = "SENT TEXT";
	byte[] data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_text);
        db = new FriendsDatabase(this);
        Bundle bundle = getIntent().getExtras();
        intentId = bundle.getString("id");
        friendId = Integer.parseInt(intentId);
        
        editMessage = (EditText)findViewById(R.id.editMessage);
        
        addText =(Button)findViewById(R.id.addText);
        addText.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//db.addMessage(friendId, editMessage.getText().toString());
				data = editMessage.getText().toString().getBytes();
				db.addTmpData(friendId, "0", data);
				UploadMessage uploader = new UploadMessage();
				uploader.execute();
				Intent intent = new Intent("android.intent.action.VIEWFRIEND");
				intent.putExtra("DBid", intentId);
				setResult(1, intent);        
				finish();
			}
		});
 
    }
    
    
    // upload text image to webdataserver

	private class UploadMessage extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String response = null;
			try {
				Log.d(TAG, "before upload message");
				response = Cloud.uploadMessage(ApplicationConstant.user, friendId, data, ApplicationConstant.meassgeType, db);
				Log.d(TAG, "after upload message");
			} catch (Exception e) {
				Log.d("upload", "message exception");
			}
			
			if(response.equals(ApplicationConstant.messageUploadResponseOk)) {
				Log.d(TAG, "upload message Ok!");
				return "Ok";
			}
			else {
				Log.d(TAG, "upload message error!");
				return "error";
			}
		}

		
		
		
	}
    
}
