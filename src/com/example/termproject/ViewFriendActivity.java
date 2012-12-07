package com.example.termproject;

import com.example.termproject.FriendsDatabase.FriendsCursor;
import com.example.termproject.FriendsDatabase.IconCursor;
import com.example.termproject.FriendsDatabase.TextMessageCursor;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ViewFriendActivity extends Activity {
	private static final String TAG = "ViewFriendActivity";
	
	FriendsDatabase db;
	FriendsCursor cursor;
	TextView firstName;
	TextView lastName;
	TextView emailText;
	TextView addressText;
	TextView phoneNumText;
	String _id;
	TextMessageCursor textCursor;
	ListView messages; 
	IconCursor iconCursor;
	byte[] iconData;
	Bitmap iconBitmap;
	String clickId;
	
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;
	
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageBitmap = null;
        
        db = new FriendsDatabase(this);
        
        
	    Bundle extras = getIntent().getExtras();
	    _id = extras.getString("DBid");
	    
	    Log.d(TAG, "in view creat" + _id);
	    
	    cursor = db.getAFriend(_id);
	    
	    
	    textCursor = db.getMessage(Integer.parseInt(_id));
	   
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
        Button leaveMessage = (Button) findViewById(R.id.leavemessage);
        Button edit = (Button) findViewById(R.id.edit);
        
        firstName = (TextView)findViewById(R.id.firstNameView);
        firstName.setText(cursor.getColFirstName());
        
        lastName = (TextView)findViewById(R.id.lastNameView);
        lastName.setText(cursor.getColLastName());
        
        emailText = (TextView)findViewById(R.id.EmailText);
        emailText.setText(cursor.getColEmail());
        
        addressText = (TextView)findViewById(R.id.AddressText);
        addressText.setText(cursor.getColAddr());
        
        phoneNumText = (TextView)findViewById(R.id.telephoneNumText);
        phoneNumText.setText(cursor.fetColPhoneNum());
        
        String[] columns  = new String[]{"message"};
        int[] to = new int[]{R.id.single_message};
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.singlemessage, textCursor, columns, to);
        messages = (ListView)findViewById(R.id.messagelistview);
        messages.setAdapter(mAdapter);
        registerForContextMenu(messages);
        
        sentText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sentText = new Intent("android.intent.action.SENTTEXT");
				sentText.putExtra("id", _id);
				startActivity(sentText);
			}
		});
        
        takePicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_S);
				
			}
		});
        
        
        
        leaveMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
        edit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent edit = new Intent("android.intent.action.ADDFRIEND");
				edit.putExtra("id", _id );
				startActivity(edit);
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		//Intent intent = data;
		//Bundle extras = intent.getExtras();
		//Bitmap mImageBitmap = (Bitmap) extras.get("data");
		if (mImageBitmap == null){
			Log.d("picture","WRONG");
		}
		else{
			Log.d("picture","RIGHT");
		}
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		case ACTION_TAKE_PHOTO_S: {
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
		mImageView.setImageBitmap(mImageBitmap);
		//mVideoUri = null;
		mImageView.setVisibility(View.VISIBLE);
		//mVideoView.setVisibility(View.INVISIBLE);
	}
	
	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		// outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mImageBitmap != null));
		// outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri !=
		// null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		// mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView
				.setVisibility(savedInstanceState
						.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
						: ImageView.INVISIBLE);
		// mVideoView.setVideoURI(mVideoUri);
		// mVideoView.setVisibility(
		// savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
		// ImageView.VISIBLE : ImageView.INVISIBLE
		// );
	}
	
	 @Override
		public void onCreateContextMenu(ContextMenu menu, View v,
		    ContextMenuInfo menuInfo) {
		  if (v.getId()==R.id.messagelistview) {
		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		      menu.add(Menu.NONE, 0, 0, "Delete");
		    }
		 }
		@Override
		 public boolean onContextItemSelected(MenuItem aItem) {
		 AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
		 textCursor.moveToPosition(menuInfo.position);
		 clickId = textCursor.getColId();
		 switch (aItem.getItemId()) {
		 case 0:
			 confirmDialog();		 
			 return true;
		 }
		 return true;
		}
		private void confirmDialog()
		{
		new AlertDialog.Builder(this)
		.setTitle(R.string.delete_message)
		.setNegativeButton(R.string.cancel,
		new DialogInterface.OnClickListener()
		{
		public void onClick(DialogInterface dialoginterface, int i)
		{
			
		}
		})
		.setPositiveButton(R.string.confirm,
		new DialogInterface.OnClickListener()
		{
		public void onClick(DialogInterface dialoginterface, int i)
		{
			db.deleteMessage(Integer.parseInt(clickId));
			textCursor.requery();
			((SimpleCursorAdapter) messages.getAdapter()).notifyDataSetChanged();
		}
		})
		.show();
		}


}