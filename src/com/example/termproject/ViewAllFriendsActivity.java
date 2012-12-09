package com.example.termproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.termproject.FriendsDatabase.FriendsCursor;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.*;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewAllFriendsActivity extends FragmentActivity {

	private static final String TAG = "ViewAllFriendActivity";
	
	static final String[] people = { "Lily", "Lucy", "HanMeimei", "LiLei" };
	ArrayList<String> array = new ArrayList<String>();
	ArrayList<Integer> index = new ArrayList<Integer>();
	ArrayList<String> tmpArray;
	FriendsDatabase db;
	FriendsCursor cursor;
	String _id;
	ListView listV;

	//private TextView mLatLng;
	private TextView mAddress;
	//private Button mFineProviderButton;
	//private Button mBothProviderButton;
	private ImageView imageLocation;
	private LocationManager mLocationManager;
	private Handler mHandler;
	private boolean mGeocoderAvailable;
	private boolean mUseFine;
	private boolean mUseBoth;
	private ImageView imageAdd;

	// Keys for maintaining UI states after rotation.
	private static final String KEY_FINE = "use_fine";
	private static final String KEY_BOTH = "use_both";
	// UI handler codes.
	private static final int UPDATE_ADDRESS = 1;
	private static final int UPDATE_LATLNG = 2;

	private static final int TEN_SECONDS = 10000;
	private static final int TEN_METERS = 10;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new FriendsDatabase(this);

		cursor = db.getFriends();

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			array.add(cursor.getColFirstName() + " " + cursor.getColLastName());
			index.add(i);
		}

		setContentView(R.layout.activity_view_all_friends);

		/*Button addFriend = (Button) findViewById(R.id.addFriendButton);
		addFriend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.intent.action.ADDFRIEND"));
			}
		});*/
		
		imageAdd = (ImageView)findViewById(R.id.imageAddFriend);
		imageAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.intent.action.ADDFRIEND"));
				
			}
		});

		listV = (ListView) findViewById(R.id.list);
		tmpArray = new ArrayList<String>(array);
		listV.setAdapter(new ArrayAdapter<String>(this, R.layout.singlefriend,
				tmpArray));

		EditText searchBar = (EditText) findViewById(R.id.SearchFriendBar);
		searchBar.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				tmpArray.clear();
				index.clear();
				String s = arg0.toString().toLowerCase();
				for (int i = 0; i < array.size(); i++) {
					if (array.get(i).toLowerCase().indexOf((s)) != -1) {
						tmpArray.add(array.get(i));
						index.add(i);
					}
				}
				Log.d("arraysize", "the size is " + tmpArray.size());
				((ArrayAdapter<String>) listV.getAdapter())
						.notifyDataSetChanged();
			}

		});
		// cursor.close();
		/*
		 * setListAdapter(new ArrayAdapter<String>(this, R.layout.singlefriend,
		 * array)); ListView listV = getListView();
		 * listV.setTextFilterEnabled(true);
		 */
		listV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				cursor.moveToPosition(index.get((int) arg3));
				String id = cursor.getColId();
				Intent anIntent = new Intent("android.intent.action.VIEWFRIEND");
				anIntent.putExtra("DBid", id);
				cursor.close();
				startActivity(anIntent);
			}

		});
		registerForContextMenu(listV);

		// Restore apps state (if exists) after rotation.
		if (savedInstanceState != null) {
			mUseFine = savedInstanceState.getBoolean(KEY_FINE);
			mUseBoth = savedInstanceState.getBoolean(KEY_BOTH);
		} else {
			mUseFine = false;
			mUseBoth = false;
		}
		
		//mLatLng = (TextView) findViewById(R.id.latlng);
		mAddress = (TextView) findViewById(R.id.address);
		// Receive location updates from the fine location provider (gps) only.
		
		//mFineProviderButton = (Button) findViewById(R.id.provider_fine);
		
		// Receive location updates from both the fine (gps) and coarse
		// (network) location
		// providers.
		
		//mBothProviderButton = (Button) findViewById(R.id.provider_both);
		imageLocation = (ImageView)findViewById(R.id.imageLocation);

		// The isPresent() helper method is only available on Gingerbread or
		// above.
		mGeocoderAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Geocoder.isPresent();

		// Handler for updating text fields on the UI like the lat/long and
		// address.
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_ADDRESS:
					mAddress.setText((String) msg.obj);
					break;
				case UPDATE_LATLNG:
					//mLatLng.setText((String) msg.obj);
					break;
				}
			}
		};
		// Get a reference to the LocationManager object.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.add(Menu.NONE, 0, 0, "Edit");
			menu.add(Menu.NONE, 1, 1, "Delete");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
				.getMenuInfo();
		cursor.moveToPosition(index.get(menuInfo.position));
		_id = cursor.getColId();
		Log.d("menuId", _id);
		switch (aItem.getItemId()) {
		case 0:
			Intent anIntent = new Intent("android.intent.action.ADDFRIEND");
			anIntent.putExtra("id", _id);
			startActivity(anIntent);
			return true; /* true means: Òwe handled the event */
		case 1:
			/*
			 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 * 
			 * builder.setMessage(R.string.delete_friend)
			 * .setPositiveButton(R.string.confirm, new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int id) {
			 * db.deleteFriend(Long.parseLong(_id)); } })
			 * .setNegativeButton(R.string.cancel, new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int id) { // User cancelled the
			 * dialog } }); builder.create().show();
			 */
			confirmDialog();
			// db.deleteFriend(Long.parseLong(_id));

			return true;
		}
		return true;
	}

	private void confirmDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.delete_friend)
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {

							}
						})
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								db.deleteFriend(Long.parseLong(_id));
								finish();
								startActivity(getIntent());
							}
						}).show();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		startActivity(getIntent());
	}

	/*
	 * class USure extends DialogFragment { private long _id; public USure(long
	 * id){ this._id = id; }
	 * 
	 * @Override public Dialog onCreateDialog(Bundle savedInstanceState) { //
	 * Use the Builder class for convenient dialog construction
	 * AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	 * 
	 * // Create the AlertDialog object and return it return builder.create(); }
	 * }
	 */
	
	// Restores UI states after rotation.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FINE, mUseFine);
        outState.putBoolean(KEY_BOTH, mUseBoth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setup();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the GPS setting is currently enabled on the device.
        // This verification should be done during onStart() because the system calls this method
        // when the user returns to the activity, which ensures the desired location provider is
        // enabled each time the activity resumes from the stopped state.
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
        	new EnableGpsDialogFragment().show(getSupportFragmentManager(), "enableGpsDialog");
        }
    }

    // Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    // Stop receiving location updates whenever the Activity becomes invisible.
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(listener);
    }

    // Set up fine and/or coarse location providers depending on whether the fine provider or
    // both providers button is pressed.
    private void setup() {
        Location gpsLocation = null;
        Location networkLocation = null;
        mLocationManager.removeUpdates(listener);
        //mLatLng.setText(R.string.unknown);
        mAddress.setText(R.string.unknown);
        // Get fine location updates only.
        if (mUseFine) {
            //mFineProviderButton.setBackgroundResource(R.drawable.button_active);
            //mBothProviderButton.setBackgroundResource(R.drawable.button_inactive);
            // Request updates from just the fine (gps) provider.
            gpsLocation = requestUpdatesFromProvider(
                    LocationManager.GPS_PROVIDER, R.string.not_support_gps);
            // Update the UI immediately if a location is obtained.
            if (gpsLocation != null) updateUILocation(gpsLocation);
        } else if (mUseBoth) {
            // Get coarse and fine location updates.
            //mFineProviderButton.setBackgroundResource(R.drawable.button_inactive);
            //mBothProviderButton.setBackgroundResource(R.drawable.button_active);
            imageLocation.setBackgroundResource(R.drawable.button_active);
            // Request updates from both fine (gps) and coarse (network) providers.
        	Log.d(TAG, "start location Ok!");
        	
        	gpsLocation = requestUpdatesFromProvider(
                    LocationManager.GPS_PROVIDER, R.string.not_support_gps);
            networkLocation = requestUpdatesFromProvider(
                    LocationManager.NETWORK_PROVIDER, R.string.not_support_network);

            // If both providers return last known locations, compare the two and use the better
            // one to update the UI.  If only one provider returns a location, use it.
            if (gpsLocation != null && networkLocation != null) {
                updateUILocation(getBetterLocation(gpsLocation, networkLocation));
            } else if (gpsLocation != null) {
                updateUILocation(gpsLocation);
            } else if (networkLocation != null) {
                updateUILocation(networkLocation);
            }
        }
    }

    /**
     * Method to register location updates with a desired location provider.  If the requested
     * provider is not available on the device, the app displays a Toast with a message referenced
     * by a resource id.
     *
     * @param provider Name of the requested provider.
     * @param errorResId Resource id for the string message to be displayed if the provider does
     *                   not exist on the device.
     * @return A previously returned {@link android.location.Location} from the requested provider,
     *         if exists.
     */
    private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            mLocationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, listener);
            location = mLocationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
        }
        return location;
    }

    // Callback method for the "fine provider" button.
    public void useFineProvider(View v) {
        mUseFine = true;
        mUseBoth = false;
        setup();
    }

    // Callback method for the "both providers" button.
    public void useCoarseFineProviders(View v) {
        mUseFine = false;
        mUseBoth = true;
        setup();
    }

    private void doReverseGeocoding(Location location) {
        // Since the geocoding API is synchronous and may take a while.  You don't want to lock
        // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
        (new ReverseGeocodingTask(this)).execute(new Location[] {location});
    }

    private void updateUILocation(Location location) {
        // We're sending the update to a handler which then updates the UI with the new
        // location.
        Message.obtain(mHandler,
                UPDATE_LATLNG,
                location.getLatitude() + ", " + location.getLongitude()).sendToTarget();

        // Bypass reverse-geocoding only if the Geocoder service is available on the device.
        if (mGeocoderAvailable) doReverseGeocoding(location);
    }

    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  Update the UI with
            // the location update.
            updateUILocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /** Determines whether one Location reading is better than the current Location fix.
      * Code taken from
      * http://developer.android.com/guide/topics/location/obtaining-user-location.html
      *
      * @param newLocation  The new Location that you want to evaluate
      * @param currentBestLocation  The current Location fix, to which you want to compare the new
      *        one
      * @return The better Location object based on recency and accuracy.
      */
    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    // AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
    // we do not want to invoke it from the UI thread.
    private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected Void doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            Location loc = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                // Update address field with the exception.
                Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Format the first line of address (if available), city, and country name.
                String addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
                // Update address field on UI.
                Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
            }
            return null;
        }
    }

    /**
     * Dialog to prompt users to enable GPS on the device.
     */
    private class EnableGpsDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.enable_gps_dialog)
                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enableLocationSettings();
                        }
                    })
                    .create();
        }
    }
}
