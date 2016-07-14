package com.airoremote;

import static com.airoremote.pushnotifications.CommonUtilities.SENDER_ID;
import static com.airoremote.pushnotifications.CommonUtilities.SERVER_START_MESSAGE;
import static com.airoremote.pushnotifications.CommonUtilities.SERVER_STOP_MESSAGE;
import static com.airoremote.pushnotifications.CommonUtilities.displayMessage;

import org.apache.commons.lang3.StringUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.Log;
import com.airoremote.R;
import com.airoremote.AppActivity;
import com.airoremote.api.APIManager;
import com.airoremote.config.Constants;
import com.airoremote.pushnotifications.CommonUtilities;
import com.airoremote.pushnotifications.MainActivity;
import com.airoremote.pushnotifications.ServerUtilities;
import com.airoremote.storage.Preferences;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.runners.TestUtil;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	//private Preferences pref;
	private SharedPreferences prefsPrivate;
	private SharedPreferences.Editor prefsPrivateEdit;
    public GCMIntentService() {
    	 super(SENDER_ID);
     }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        //Log.d("NAME", MainActivity.name);
        prefsPrivate= context.getSharedPreferences(Preferences.PREFS_PRIVATE, Context.MODE_PRIVATE);
        String UserName = prefsPrivate.getString(Preferences.KEY_LOGGED_USERNAME, "");
        String Password = prefsPrivate.getString(Preferences.KEY_LOGGED_PASSWORD, "");
        String sIMEI = new DeviceUtil(context).getIMEI();
        ServerUtilities.register(context, UserName, Password, registrationId, sIMEI);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        prefsPrivate= context.getSharedPreferences(Preferences.PREFS_PRIVATE, Context.MODE_PRIVATE);
        prefsPrivateEdit = prefsPrivate.edit();
        displayMessage(context, message);
        //On receiving start test message from server Test will start automatically.
        if(prefsPrivate.getBoolean(Preferences.KEY_LOGGED_IN, false)){
        	System.out.println("KEY_EXT_TEST_RUNNIING "+prefsPrivate.getBoolean(Preferences.KEY_EXT_TEST_RUNNIING, false));
        if(message!=null)
        	{
        	String a[]= new String[message.length()];
        	a = message.split(":");
        	//System.out.println("Constants.API_SERVER_URL "+a[0]+"----"+Constants.API_SERVER_URL.contains(a[0]));
        	if(Constants.API_SERVER_URL.contains(a[0]))
        	{
		        if(a[1].equals(SERVER_START_MESSAGE)){
		        prefsPrivateEdit.putString(Preferences.KEY_IS_TEST_AUTO_START, SERVER_START_MESSAGE);
		        prefsPrivateEdit.commit();
		        
		        // notifies user
		        generateNotification(context, "Test Started By Airometric!!!");
		       } else //On receiving stop test message from server Test will stop automatically and results uploaded on server. 
		        if(a[1].equals(SERVER_STOP_MESSAGE))
		        {
		        	prefsPrivateEdit.putString(Preferences.KEY_IS_TEST_AUTO_START, SERVER_STOP_MESSAGE);
		     	    prefsPrivateEdit.commit();
		        	generateNotification(context, "Test Stopped By Airometric!!!");
		    	}
		        
		        if(prefsPrivate.getBoolean(Preferences.KEY_EXT_TEST_RUNNIING, false)){
		        Intent i = new Intent(context,ExternalTestActivity.class); 
		        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		        //i.putExtra("ank", "AnkitCheck");
		        startActivity(i); 
		        }else{
		        Intent i = new Intent(context,TestTypeActivity.class); 
			        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			        //i.putExtra("ank", "AnkitCheck");
			        startActivity(i); 
		        }
	        	}
        	}
       }
        
    }
        
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.logo;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        //@SuppressWarnings("deprecation")
		//Notification notification = new Notification(icon, message, when);
		
        Notification.Builder builder = new Notification.Builder(context);
        
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, TestTypeActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //notification.setLatestEventInfo(context, title, message, intent);
        //notification.contentIntent
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setContentIntent(intent);
        builder.setSmallIcon(icon);
        builder.setAutoCancel(true);
        //builder.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        //builder.defaults |= Notification.DEFAULT_SOUND;
        builder.setDefaults(Notification.DEFAULT_SOUND);
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        // notification.defaults |= Notification.DEFAULT_VIBRATE;
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationManager.notify(0, builder.build()); 
        /*TestTypeActivity tta = new TestTypeActivity();
        tta.autoStartDownloadConfig(context);*/
      
   }

}
