package com.example.termproject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

/* provide helper functions for our database */
//Three tables: friends textmessage icon
//TABLE friends (_id INTEGER PRIMARY KEY AUTOINCREMENT, firstName TEXT, lastName TEXT, phoneNumber TEXT,email TEXT, address TEXT, username TEXT);
//TABLE textmessage (_id INTEGER PRIMARY KEY AUTOINCREMENT, friendId INTEGER, message TEXT);
//TABLE icon (_id INTEGER PRIMARY KEY AUTOINCREMENT, friendId INTEGER, icon BLOB);
public class FriendsDatabase extends SQLiteOpenHelper {
	private static final String TABLE_NAME = "friends";
	private static final int DATABASE_VERSION = 9;
	private static final String COLUMN_FIRSTNAME = "firstName";
	private static final String COLUMN_LASTNAME = "lastName";
	private static final String COLUMN_PHONENUMBER = "phoneNumber";
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_ADDRESS = "address";
	private final Context mContext;
	
	private static final String TAG = "FriendsDatabase";
	
	
	
	public FriendsDatabase(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
		this.mContext = context;
	}

	public static ContentValues makeEle(AFriend aFriend){
		ContentValues ele = new ContentValues();
		ele.put(COLUMN_FIRSTNAME, aFriend.getFirstName());
		ele.put(COLUMN_LASTNAME, aFriend.getLastName());
		ele.put(COLUMN_PHONENUMBER, aFriend.getPhoneNumber());
		ele.put(COLUMN_EMAIL, aFriend.getEmail());
		ele.put(COLUMN_ADDRESS, aFriend.getAddress());
		return ele;
		
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] sql = mContext.getString(R.string.create_friends_table).split("\n");
		db.beginTransaction();
		try {
			execMultipleSQL(db, sql);
			db.setTransactionSuccessful();
		}catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
            db.endTransaction();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "OnUpdate!!!!!!");
		String[] updateSQL = mContext.getString(R.string.update_friends_table).split("\n");
		db.beginTransaction();
		try {
			execMultipleSQL(db, updateSQL);
			db.setTransactionSuccessful();
		}catch(SQLException e) {
			Log.e("Error creating tables and debug data", e.toString());
		}finally {
			db.endTransaction();
		}
		onCreate(db);
	}
	
	//FriendCursor for accessing friends table
	public static class FriendsCursor extends SQLiteCursor {
		private static final String QUERY = "SELECT friends._id, friends.firstName, friends.lastName "  +
				"FROM friends " +
				"WHERE username = ";
		
		private static final String QUERYAFRIEND = "SELECT * "  +
				"FROM friends " +
				"WHERE friends._id = ";
       
		@SuppressWarnings("deprecation")
		private FriendsCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }
		
		 private static class Factory implements SQLiteDatabase.CursorFactory{
	            @Override
	            public Cursor newCursor(SQLiteDatabase db,
	                    SQLiteCursorDriver driver, String editTable,
	                    SQLiteQuery query) {
	                return new FriendsCursor(db, driver, editTable, query);
	            }
	     }
		 
	      public long getColFriendId() {
	            return getLong(getColumnIndexOrThrow("_id"));
	      }
	      
	      public String getColFirstName() {
	    	  return getString(getColumnIndexOrThrow("firstName"));
	      }
	      
	      public String getColLastName() {
	    	  return getString(getColumnIndexOrThrow("lastName"));
	      }
	      
	      public String getColId() {
	    	  return getString(getColumnIndexOrThrow("_id"));
	      }
	      
	      public String getColEmail() {
	    	  return getString(getColumnIndexOrThrow("email"));
	      }
	      
	      public String getColAddr() {
	    	  return getString(getColumnIndexOrThrow("address"));
	      }
	      
	      public String fetColPhoneNum() {
	    	  return getString(getColumnIndexOrThrow("phoneNumber"));
	      }

	}
	
	

	// cursor accessing tmptable
	public static class TmpCursor extends SQLiteCursor {

		private static final String getTmp = "SELECT * " +
										"FROM tmptable " +
										"WHERE tmptable.usrname = " + "'" + ApplicationConstant.user + "'" + " AND friendId = ";
		private TmpCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
				String editTable, SQLiteQuery query) {
			super(db, driver, editTable, query);
		}
		
		private static class Factory implements SQLiteDatabase.CursorFactory {

			@Override
			public Cursor newCursor(SQLiteDatabase db,
					SQLiteCursorDriver masterQuery, String editTable,
					SQLiteQuery query) {
				// TODO Auto-generated method stub
				return new TmpCursor(db, masterQuery, editTable, query);
			}
			
		}
		
		public String getId() {
			 return getString(getColumnIndexOrThrow("_id"));
		}
		
		public String getUserName() {
	    	  return getString(getColumnIndexOrThrow("usrname"));
	    }
		
		public String getFriendId() {
	    	  return getString(getColumnIndexOrThrow("friendId"));
	    }
		
		public String getType() {
	    	  return getString(getColumnIndexOrThrow("type"));
	    }
		
		public byte[] getData() {
	    	  return getBlob(getColumnIndexOrThrow("data"));
	    }
		
	}
	
	
	public void addTmpData(int friendId, String type, byte[] data) {
		ContentValues values = new ContentValues();
		values.put("usrname", ApplicationConstant.user);
		values.put("friendId", friendId);
		values.put("type", type);
		values.put("data", data);
				
		try {
			Log.d(TAG, "in try");
			if(getWritableDatabase() == null) {
				Log.d(TAG, "db is null");
			} else {
				Log.d(TAG, "db is not null");
			}
			Log.d(TAG, "out try");
			getWritableDatabase().insert("tmptable", null, values);
		} catch(Exception e) {
			Log.e("Error writing new tmp message", e.toString());
		}
	}
	
	
	 public TmpCursor getAllTmpMessage(String friendId) {
		 String sql = TmpCursor.getTmp + friendId;
		 SQLiteDatabase d = getReadableDatabase();
	     TmpCursor c = (TmpCursor) d.rawQueryWithFactory(
	            new TmpCursor.Factory(),
	            sql,
	            null,
	            null);
	        c.moveToFirst();
	        return c;
	 }
	
	
	
	
	//icon cursor accessing icon table
	public static class IconCursor extends SQLiteCursor {
		private static String getIcon = "SELECT * " +
									"FROM icon "+
									"WHERE friendId = ";
		private IconCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
				String editTable, SQLiteQuery query) {
			super(db, driver, editTable, query);
		}
		
		private static class Factory implements SQLiteDatabase.CursorFactory {

			@Override
			public Cursor newCursor(SQLiteDatabase db,
					SQLiteCursorDriver masterQuery, String editTable,
					SQLiteQuery query) {
				// TODO Auto-generated method stub
				return new IconCursor(db, masterQuery, editTable, query);
			}
			
		}
		
		public byte[] getIconBytes() {
			return getBlob(getColumnIndexOrThrow("icon"));
		}
		
	}
	
	//icon table helper functions
	public void addIcon(int friendId, byte[] iconBytes) {
		ContentValues values = new ContentValues();
		values.put("friendId", friendId);
		values.put("icon", iconBytes);
		try{
			getWritableDatabase().insert("icon", null, values);
		} catch(SQLException e) {
			Log.e("Error writing new icon", e.toString());
		}
	}
	
	public void editIcon(int friendId, byte[] icon) {
		ContentValues value = new ContentValues();
		String[] whereArgs = new String[]{Integer.toString(friendId)};
		value.put("icon", icon);
		try {
			getWritableDatabase().update("icon", value, "friendId=?", whereArgs);
		} catch (SQLException e) {
            Log.e("Error edit icon", e.toString());
        }
		
	}
	
	public boolean hasIcon(int friendId) {
		String sql = IconCursor.getIcon + friendId;
		SQLiteDatabase d = getReadableDatabase();
		IconCursor c = (IconCursor) d.rawQueryWithFactory(
            new IconCursor.Factory(),
            sql,
            null,
            null);
		Log.d(TAG, "database has Icon" + c.getCount());
		if(c.getCount() > 0)
			return true;
		else
			return false;
	}
	
	public IconCursor getIcon(int friendId) {
		String sql = IconCursor.getIcon + friendId;
		SQLiteDatabase d = getReadableDatabase();
		IconCursor c = (IconCursor) d.rawQueryWithFactory(
            new IconCursor.Factory(),
            sql,
            null,
            null);
		c.moveToFirst();
		return c;
	}
	
	
	//firends table helper functions
	public void addFriend(AFriend aFriend) {
		ContentValues ele = makeEle(aFriend);
		ele.put("username", ApplicationConstant.user);
		
		try{
			getWritableDatabase().insert(TABLE_NAME, null, ele);
		} catch (SQLException e) {
            Log.e("Error writing new friend", e.toString());
        }
	}
	
	public void editFriend(long friendID, AFriend aFriend) {
		ContentValues ele = makeEle(aFriend);
		String[] whereArgs = new String[]{Long.toString(friendID)};
        
		try{
            getWritableDatabase().update("friends", ele, "_id=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error edit friend", e.toString());
        }
	}
	
	 public void deleteFriend(long friendID) {
		 String[] whereArgs = new String[]{Long.toString(friendID)};
	     try{
	          getWritableDatabase().delete("friends", "_id=?", whereArgs);
	     } catch (SQLException e) {
	         Log.e("Error deleteing friend", e.toString());
	     }
	 }
	 
	 public int getFriendCount() {
		 Cursor c = null;
		 try {
	       c = getReadableDatabase().rawQuery(
	            "SELECT count(*) FROM friends",
	           null);
	       if (c.getCount() <= 0) { return 0; }
	        c.moveToFirst();
	        return c.getCount();
	     }
		 finally {
	            if (c != null) {
	                try { c.close(); }
	                catch (SQLException e) { }
	            }
	        }
	 }
	 
	 public FriendsCursor getFriends() {
	        String sql = FriendsCursor.QUERY + "'"+ApplicationConstant.user+"'";
	        SQLiteDatabase d = getReadableDatabase();
	        FriendsCursor c = (FriendsCursor) d.rawQueryWithFactory(
	            new FriendsCursor.Factory(),
	            sql,
	            null,
	            null);
	        c.moveToFirst();
	        return c;
	    }
	 
	 public FriendsCursor getAFriend(String id) {
		 String sql = FriendsCursor.QUERYAFRIEND + " " + id;
		 SQLiteDatabase d = getReadableDatabase();
	        FriendsCursor c = (FriendsCursor) d.rawQueryWithFactory(
	            new FriendsCursor.Factory(),
	            sql,
	            null,
	            null);
	        c.moveToFirst();
	        return c;
	 }
	 
	 
	 public int getNewFriendId() {
		 FriendsCursor cursor = getFriends();
		 cursor.moveToLast();
		 long id = cursor.getColFriendId();
		 return Integer.parseInt(Long.toString(id));
	 }
	 

	
	 //helper function for creating tables
	 public void execMultipleSQL(SQLiteDatabase db, String[] sql) {
		 for(String s : sql) {
			 if(s.trim().length() > 0) {
				 db.execSQL(s);
			 }
		 }
	 }
	 
	 
}