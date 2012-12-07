package com.example.termproject;


import java.util.ArrayList;

import com.example.termproject.FriendsDatabase.FriendsCursor;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewAllFriendsActivity extends Activity {
	
	static final String[] people = {"Lily", "Lucy", "HanMeimei", "LiLei"};
	ArrayList<String> array = new ArrayList<String>();
	ArrayList<Integer> index = new ArrayList<Integer>();
    ArrayList<String> tmpArray;
	FriendsDatabase db;
	FriendsCursor cursor;
	String _id;
	ListView listV;
    
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new FriendsDatabase(this);
       
        cursor = db.getFriends();
        
        for(int i = 0; i < cursor.getCount(); i++) {
        	cursor.moveToPosition(i);
        	array.add(cursor.getColFirstName() + " " + cursor.getColLastName());
        	index.add(i);
        }
        
        
        setContentView(R.layout.activity_view_all_friends);
        
        
        
        Button addFriend = (Button) findViewById(R.id.addFriendButton);
    	addFriend.setOnClickListener(
    			new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					startActivity(new Intent("android.intent.action.ADDFRIEND"));
    				}
    			}
    		);

    	
    	
        listV = (ListView) findViewById(R.id.list);
        tmpArray = new ArrayList<String> (array);
        listV.setAdapter(new ArrayAdapter<String>(this, R.layout.singlefriend, tmpArray));
				
		
		EditText searchBar = (EditText) findViewById(R.id.SearchFriendBar);
		searchBar.addTextChangedListener(new TextWatcher()
	    {


	        @Override
	        public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3)
	        {
	            // TODO Auto-generated method stub

	        }



	        @Override
	        public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3)
	        {
	            // TODO Auto-generated method stub

	        }



	        @Override
	        public void afterTextChanged( Editable arg0)
	        {
	            // TODO Auto-generated method stub
	        	tmpArray.clear();
	        	index.clear();
	        	String s = arg0.toString().toLowerCase();
	        	for (int i = 0; i< array.size(); i++){
	        		 if(array.get(i).toLowerCase().indexOf((s)) != -1) {
	                        tmpArray.add(array.get(i));
	                        index.add(i);
	                    }
	        	}
	        	Log.d("arraysize","the size is "+tmpArray.size());
	        	((ArrayAdapter<String>) listV.getAdapter()).notifyDataSetChanged();
	        }

	   });
        //cursor.close();
        /*setListAdapter(new ArrayAdapter<String>(this, R.layout.singlefriend, array));
		ListView listV = getListView();
		listV.setTextFilterEnabled(true);*/
		listV.setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						
	    			 	cursor.moveToPosition( index.get((int) arg3));
	    			 	String id = cursor.getColId();
						Intent anIntent = new Intent("android.intent.action.VIEWFRIEND");
						anIntent.putExtra("DBid", id);
						cursor.close();
						startActivity(anIntent);
					}
					
				}
			);
		registerForContextMenu(listV);
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.list) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	      menu.add(Menu.NONE, 0, 0, "Edit");
	      menu.add(Menu.NONE, 1, 1, "Delete");
	    }
	 }
	@Override
	 public boolean onContextItemSelected(MenuItem aItem) {
	 AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
	 cursor.moveToPosition(index.get(menuInfo.position));
	 _id = cursor.getColId();
	 Log.d("menuId",_id);
	 switch (aItem.getItemId()) {
	 case 0:
		 Intent anIntent = new Intent("android.intent.action.ADDFRIEND");
		 anIntent.putExtra("id", _id);
		 startActivity(anIntent);
		 return true; /* true means: “we handled the event”. */
	 case 1:
		 /*
		 	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	        builder.setMessage(R.string.delete_friend)
            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
             	 db.deleteFriend(Long.parseLong(_id));
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
		 builder.create().show();
		 */
		 confirmDialog();
		 //db.deleteFriend(Long.parseLong(_id));
		 
		 return true;
	 }
	 return true;
	}
	private void confirmDialog()
	{
	new AlertDialog.Builder(this)
	.setTitle(R.string.delete_friend)
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
		db.deleteFriend(Long.parseLong(_id));
		finish();
		startActivity(getIntent());
	}
	})
	.show();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		startActivity(getIntent());
	}

	
	
/*
	class USure extends DialogFragment {
		private long _id;
		public USure(long id){
			this._id = id;
		}
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
*/	
}
