package com.example.termproject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

import com.example.termproject.FriendsDatabase.FriendsCursor;
import com.example.termproject.FriendsDatabase.IconCursor;
import com.example.termproject.FriendsDatabase.TextMessageCursor;
import com.example.termproject.FriendsDatabase.TmpCursor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ViewFriendActivity extends Activity {
	private static final String TAG = "ViewFriendActivity";
	
	FriendsDatabase db;
	FriendsCursor cursor;
	//private Cursor elementCursor;
	//TextView firstName;
	//TextView lastName;
	TextView userName;
	TextView emailText;
	TextView addressText;
	TextView phoneNumText;
	String _id;
	TextMessageCursor textCursor;
	//ListView messages; 
	IconCursor iconCursor;
	byte[] iconData;
	Bitmap iconBitmap;
	Bitmap imageBitmap;
	String clickId;
	ByteArrayOutputStream stream;
	ArrayList<String> key;
	ArrayList<String> type;
	ImageView newImgView;


	private LayoutInflater  layoutInflater;
	private LinearLayout mainLayout;
	private LinearLayout addLayout;
	private LinearLayout imageLayout;
	private LinearLayout textLayout;

	
	TmpCursor tmpcursor;

	
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;
	
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	Bitmap mImageBitmap;
	byte[] pictureData;
	
	Map<String, byte[]> map;
	
	ImageView tryPicView;
	TextView tryText;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("finalbugcreate", "in on create!");
    	super.onCreate(savedInstanceState);
	    
        Bundle extras = getIntent().getExtras();
	    _id = extras.getString("DBid");
        Cloud.setFriendId(Integer.parseInt(_id));
        
                
        setContentView(R.layout.activity_view_friend);
        
        //tryPicView = (ImageView)findViewById(R.id.imageTry);

        mainLayout = (LinearLayout)findViewById(R.id.layout01);
        addLayout = (LinearLayout)findViewById(R.id.layout02);
        imageLayout = (LinearLayout)findViewById(R.id.layoutImage);
        textLayout = (LinearLayout)findViewById(R.id.layoutText);
        
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageBitmap = null;
        
        db = new FriendsDatabase(this);
        db.getWritableDatabase().execSQL(this.getString(R.string.drop_tmp_table));
        db.getWritableDatabase().execSQL(this.getString(R.string.create_tmp_table));
        
        ////////////////////
        key = new ArrayList<String>();
        type = new ArrayList<String>();
        Cloud.getMessage(ApplicationConstant.user, Integer.parseInt(_id), key, type);
        Log.e("TAG","downloading the data");
        map = Cloud.getMessageData(key, type, db);
        tmpcursor = db.getAllTmpMessage(_id);
        Log.d(TAG, "TMPcount is" + tmpcursor.getCount());

	    
	    Log.d(TAG, "in view creat" + _id);
	    
	    cursor = db.getAFriend(_id);
	    
	    
	   // textCursor = db.getMessage(Integer.parseInt(_id));
	   
	    Log.d(TAG, "?????" + db.hasIcon(Integer.parseInt(_id)));
	    
	    if(db.hasIcon(Integer.parseInt(_id))){
	    	Log.d(TAG,"has icon" + _id);
	    	iconCursor = db.getIcon(Integer.parseInt(_id));
	    	iconData = iconCursor.getIconBytes();
	    	iconBitmap = BitmapFactory.decodeByteArray(iconData,0,iconData.length);
	    	mImageView.setImageBitmap(iconBitmap);
	    } else {
	    	mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_icon));
	    }
	      
        Button sentText = (Button) findViewById(R.id.senttext);
        Button takePicture = (Button) findViewById(R.id.takepicture);
        
        
        //firstName = (TextView)findViewById(R.id.firstNameView);
        //firstName.setText(cursor.getColFirstName());
        
        //lastName = (TextView)findViewById(R.id.lastNameView);
        //lastName.setText(cursor.getColLastName());
        userName = (TextView)findViewById(R.id.userNameView);
        userName.setText(cursor.getColFirstName()+" "+cursor.getColLastName());
        
        emailText = (TextView)findViewById(R.id.EmailText);
        emailText.setText(cursor.getColEmail());
        
        addressText = (TextView)findViewById(R.id.AddressText);
        addressText.setText(cursor.getColAddr());
        
        phoneNumText = (TextView)findViewById(R.id.telephoneNumText);
        phoneNumText.setText(cursor.fetColPhoneNum());
        
        String[] columns  = new String[]{"message"};
        int[] to = new int[]{R.id.single_message};
       // SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.singlemessage, textCursor, columns, to);
       // messages = (ListView)findViewById(R.id.messagelistview);
        //messages.setAdapter(mAdapter);
       // registerForContextMenu(messages);
        
        /*leaveMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent leaveMessage = new Intent("android.intent.action.RECORDER");
				startActivity(leaveMessage);
			}
		});*/
        sentText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sentText = new Intent("android.intent.action.SENTTEXT");
				sentText.putExtra("id", _id);
				startActivityForResult(sentText,5);
			}
		});
        
        takePicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
				
			}
		});
        

        
        /*edit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent edit = new Intent("android.intent.action.ADDFRIEND");
				edit.putExtra("id", _id );
				startActivityForResult(edit, 7);
			}
		});*/
        

    }

    @Override
    protected void onResume() {
    	Log.d("finalbugResume", "in on Resume!");
    	super.onResume();
    	tmpcursor = db.getAllTmpMessage(_id);
        
        if(tmpcursor!=null && tmpcursor.getCount()!=0){  
            Log.d("Tag", "post on Resume!");

        	addLayout.removeAllViews();
        	inflateElement(tmpcursor);
        }   
    }
    
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		/*tmpcursor = db.getAllTmpMessage(_id);
		if(tmpcursor!=null && tmpcursor.getCount()!=0){  
            Log.d("finalbugs", "post on Resume!");

        	addLayout.removeAllViews();
        	//inflateElement(tmpcursor);
        }  
		Log.d("finalbugRestart", "in on Restart!");*/
		super.onRestart();
		/*
		
        tmpcursor = db.getAllTmpMessage();
        
        if(tmpcursor!=null && tmpcursor.getCount()!=0){       	
        	addLayout.removeAllViews();
        	inflateElement(tmpcursor);
        } */     
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.d("finalbugstart", "in on start!");
		super.onStart();
		/*
		 tmpcursor = db.getAllTmpMessage();
	        
	        if(tmpcursor!=null && tmpcursor.getCount()!=0){       	
	        	addLayout.removeAllViews();
	        	inflateElement(tmpcursor);
	        }
	        */      
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		//Intent intent = data;
		//Bundle extras = intent.getExtras();
		//Bitmap mImageBitmap = (Bitmap) extras.get("data");
		Log.d("finalbugsActivityResult", "in on activityResult!");
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_S: {
			if (resultCode == RESULT_OK) {
				
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				Log.d("picture", "picture OK");
				handleSmallCameraPhoto(data);
			}
			else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	        } else {
	            // Video capture failed, advise user
	        }
			break;
		} // ACTION_TAKE_PHOTO_S

		case ACTION_TAKE_VIDEO: {
			if (resultCode == RESULT_OK) {
				
			}
			break;
		} // ACTION_TAKE_VIDEO
		} // switch
	}
	
	private void handleSmallCameraPhoto(Intent intent) {
		Bundle extras = intent.getExtras();
		mImageBitmap = (Bitmap) extras.get("data");
		//mImageView.setImageBitmap(mImageBitmap);
		//mVideoUri = null;
		//mImageView.setVisibility(View.VISIBLE);
		//mVideoView.setVisibility(View.INVISIBLE);
		if (mImageBitmap == null){
			Log.d("picture","WRONG");
		}
		else{
			Log.d("picture","RIGHT");
		}
		stream = new ByteArrayOutputStream();
		mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		pictureData = stream.toByteArray();
		db.addTmpData(Integer.parseInt(_id), "1", pictureData);
		new UploadMessage().execute();
	}

	

	private class UploadMessage extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String response = null;
			try {
				Log.d(TAG, "before upload message");
				response = Cloud.uploadMessage(ApplicationConstant.user, Integer.parseInt(_id), pictureData, ApplicationConstant.imageType, db);
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
		
		@Override
	     protected void onPostExecute(String result) {
	         key = new ArrayList<String>();
	         type = new ArrayList<String>();
	         Cloud.getMessage(ApplicationConstant.user, Integer.parseInt(_id), key, type);
	     
	         



	     }
		
	}

	
	private void inflateElement(TmpCursor tmpcursor){
		Log.d("inflate","ENTERING ONCE"+tmpcursor.getCount());
		//ViewFriendActivity.this.setContentView(R.layout.activity_view_friend);
		//onCreate(null);
		for(int i=tmpcursor.getCount()-1; i>=0;i--){
			tmpcursor.moveToPosition(i);
			layoutInflater = getLayoutInflater();

			if(tmpcursor.getType().equals(Integer.toString(ApplicationConstant.meassgeType))){
				byte[] Data = tmpcursor.getData();
				String value = new String(Data);
				TextView newTextView = (TextView)layoutInflater.inflate(R.layout.text, null);
				newTextView.setText(value);
				//mainLayout.addView(newTextView);
				
		       
				textLayout.addView(newTextView);
				
				
			}
			else if(tmpcursor.getType().equals(Integer.toString(ApplicationConstant.imageType))){

				
				byte[] Data = tmpcursor.getData();
				Log.d("finalbugs", "in inflate image !!!!!!!!!!!"+Data.length);
				imageBitmap = BitmapFactory.decodeByteArray(Data,0,Data.length);
				newImgView = (ImageView)layoutInflater.inflate(R.layout.image,null);
				newImgView.setImageBitmap(imageBitmap);
				
				Log.d("HASHCODE",Integer.toString(newImgView.hashCode()));
				Log.d("imageBitmap",Integer.toString(imageBitmap.hashCode()));

				newImgView.invalidate();
				
				//mainLayout.addView(newImgView);
				addLayout.addView(newImgView);
				imageLayout.addView(newImgView);				

			}
			else if(tmpcursor.getType().equals(Integer.toString(ApplicationConstant.audioType))){
				
			}
		}
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("finalbugs", "in on destroy");
        db.getWritableDatabase().execSQL(this.getString(R.string.drop_tmp_table));
		super.onDestroy();
	}	
	
	
	

}
