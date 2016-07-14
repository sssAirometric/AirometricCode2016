package com.airometric.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryChangeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		int maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
		int batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,
				BatteryManager.BATTERY_HEALTH_UNKNOWN);
		float batteryPercentage = ((float) batteryLevel / (float) maxLevel) * 100;
		onBatterLevelChanged((int) batteryPercentage);
	}

	public void onBatterLevelChanged(int batterylevel) {
	}
}
