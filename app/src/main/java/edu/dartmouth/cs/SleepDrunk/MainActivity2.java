/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.dartmouth.cs.SleepDrunk; 

import java.io.IOException;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;


import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;



public class MainActivity2 extends Activity {

    
    private static final String TAG = "MainActivity2";
    
	public static final String PROPERTY_REG_ID = "Registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private String SENDER_ID = "730077575265";
	private GoogleCloudMessaging gcm;
	private Context context;
	public static String regid;
	private ReactHighScoreDatabase db = ReactHighScoreDatabase.getDatabase(this);
	private HighScoreEntry entry;
	
	private IntentFilter mMessageIntentFilter;
	private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			long id = intent.getLongExtra("id",-1);
			String regid_rec = intent.getStringExtra("regid");
			if (id != -1 && regid_rec!=null && regid_rec.equals(regid)) {
				
				entry = db.getEntry((int)id);
				db.deleteEntry(entry);
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		mMessageIntentFilter = new IntentFilter();
		mMessageIntentFilter.addAction("GCM_NOTIFY");
		context = getApplicationContext();
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		}

        final FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new StartFragment()).commit();
        }



        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew) //.setPosition(FloatingActionButton.POSITION_TOP_RIGHT)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));


        SubActionButton btn1 = rLSubBuilder.setContentView(rlIcon1).build();
        SubActionButton btn2 = rLSubBuilder.setContentView(rlIcon2).build();
        SubActionButton btn3 = rLSubBuilder.setContentView(rlIcon3).build();


        btn1.setClickable(true);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new StartFragment()).commit();
            }
        });
        btn2.setClickable(true);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
            }
        });
        btn3.setClickable(true);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new CurveFragment()).commit();
            }
        });



        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(btn1)
                .addSubActionView(btn2)
                .addSubActionView(btn3)
                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    
	@Override
	protected void onResume() {
		registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
		super.onResume();
	}

	@Override
	protected void onPause() {

		unregisterReceiver(mMessageUpdateReceiver);
		super.onPause();
	}
	
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity2.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}
	
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						//gcm = GoogleCloudMessaging.getInstance(context);
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.
					// The request to your server should be authenticated if
					// your app
					// is using accounts.
					ServerUtilities.sendRegistrationIdToBackend(context, regid);

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i(TAG, "gcm register msg: " + msg);
			}
		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				finish();
			}
			return false;
		}
		return true;
	}

	
	
}