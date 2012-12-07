package com.example.termproject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SentTextActivity extends Activity {
	Button addText;
	FriendsDatabase db;
	int friendId;
	String intentId;
	EditText editMessage;
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
				db.addMessage(friendId, editMessage.getText().toString());
				Intent intent = new Intent("android.intent.action.VIEWFRIEND");
				intent.putExtra("DBid", intentId);
				startActivity(intent);
			}
		});
    }
}
