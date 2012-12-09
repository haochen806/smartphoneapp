package com.example.termproject;

import java.io.ByteArrayOutputStream;

import com.example.termproject.FriendsDatabase.FriendsCursor;
import com.example.termproject.FriendsDatabase.IconCursor;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddFriendActivity extends Activity {
	FriendsDatabase db;
	EditText name;
	Button addButton;
	Button deleteButton;
	String id;
	FriendsCursor cursor;
	private static final String TAG = "AddFriendActivity";
	Button takePicture;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private Bitmap iconBitmap;
	ImageView icon;
	ByteArrayOutputStream stream;
	byte[] iconByte;
	IconCursor iconCursor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"in onCreate");
        db = new FriendsDatabase(this);
        stream = new ByteArrayOutputStream();
        setContentView(R.layout.activity_add_friend);
        
        Bundle bundle = getIntent().getExtras();
        takePicture = (Button)findViewById(R.id.TakeIcon);
        icon = (ImageView)findViewById(R.id.icon);
        takePicture.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_S);
			}
		});
                
        if(bundle != null) {
        	id = bundle.getString("id");
           	Log.d(TAG, "id is !!" +  id);
        	
           	cursor = db.getAFriend(id);
			((EditText)findViewById(R.id.AddFriendLastName)).setText(cursor.getColLastName());
			((EditText) findViewById(R.id.AddFriendMobilePhone)).setText(cursor.fetColPhoneNum());
			((EditText) findViewById(R.id.AddFriendAddress)).setText(cursor.getColAddr());
			((EditText) findViewById(R.id.AddFriendFristName)).setText(cursor.getColFirstName());
			((EditText) findViewById(R.id.AddFriendEmail)).setText(cursor.getColEmail());
			addButton = (Button)findViewById(R.id.AddFriendButton);
			addButton.setText("Edit");
			
			if(db.hasIcon(Integer.parseInt(id))) {
				iconCursor = db.getIcon(Integer.parseInt(id));
				iconByte = iconCursor.getIconBytes();
				iconBitmap = BitmapFactory.decodeByteArray(iconByte,0,iconByte.length);
		    	icon.setImageBitmap(iconBitmap);
			} else {
				icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_icon));
			}
        }
        else {
        	Log.d(TAG, "bundle is null");
        	addButton = (Button)findViewById(R.id.AddFriendButton);
        	icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_icon));
        }
        
    	addButton.setOnClickListener(new addFriendOnClickListener());
    	
               
    }
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK) {
    		Log.d(TAG, "Icon Ok");
    		handleIcon(data);
    		iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    		iconByte = stream.toByteArray();
    	}
	}

    
    private void handleIcon(Intent data) {
    	Bundle extras = data.getExtras();
    	iconBitmap = (Bitmap)extras.get("data");
    	icon.setImageBitmap(iconBitmap);
    	icon.setVisibility(View.VISIBLE);
    }

	private class addFriendOnClickListener implements View.OnClickListener{
    	@Override
    	public void onClick(View v){
    		String firstName = ((EditText) findViewById(R.id.AddFriendFristName)).getText().toString();
    		if (!firstName.equals("")){
    			String lastName = ((EditText) findViewById(R.id.AddFriendLastName)).getText().toString();
    			String phoneNumber = ((EditText) findViewById(R.id.AddFriendMobilePhone)).getText().toString();
    			String address = ((EditText) findViewById(R.id.AddFriendAddress)).getText().toString();
    			String email = ((EditText) findViewById(R.id.AddFriendEmail)).getText().toString();
    			AFriend newFriend = new AFriend(firstName,lastName,phoneNumber,email,address);
    			if(id == null) {
    				db.addFriend(newFriend);
    				int newId = db.getNewFriendId();
    				if(iconByte != null) {
    					db.addIcon(newId, iconByte);
    				}
    			}
    			else {
    				db.editFriend(Long.parseLong(id), newFriend);
    				if(iconByte != null) {
    					Log.d(TAG, "byte is not null");
    					if(db.hasIcon(Integer.parseInt(id)))
    						db.editIcon(Integer.parseInt(id), iconByte);
    					else
    						db.addIcon(Integer.parseInt(id), iconByte);
    				}
    			}
    			
        		/*if(iconByte != null) {
        			Log.d(TAG, "byte is not null");
        		} else {
        			Log.d(TAG, "byte is null");
        		}*/
    			
        		//startActivity(new Intent("android.intent.action.ALLFRIENDS"));
    			setResult(RESULT_OK);
    			finish();
    		}
    		else{
    			 	AlertDialog.Builder alert = new AlertDialog.Builder(AddFriendActivity.this);
    			 	alert.setTitle(R.string.errorTitle); 
	                alert.setMessage(R.string.noFirstName);
	                alert.setPositiveButton(R.string.errorButton, null); 
	                alert.show();
    		}
    		
    	}
    }
}
