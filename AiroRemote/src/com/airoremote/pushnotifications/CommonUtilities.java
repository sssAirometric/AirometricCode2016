package com.airoremote.pushnotifications;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
	public static final String SERVER_URL = "http://ssism.org/TestAndroid/gcm_server_php/register.php"; 

    // Google project id
    public static final String SENDER_ID = "297422558021"; //297422558021 ///369006527652

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Airoremote GCM";//AndroidHive GCM

    public static final String DISPLAY_MESSAGE_ACTION = "com.airometric.pushnotifications.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";
    
    //public static final String AUTOSTART = "AUTOSTART";
    
    public static final String SERVER_START_MESSAGE = "START_TEST";
    public static final String SERVER_STOP_MESSAGE = "STOP_TEST";
	
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
