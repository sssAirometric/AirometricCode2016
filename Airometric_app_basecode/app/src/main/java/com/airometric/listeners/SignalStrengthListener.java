package com.airometric.listeners;

import android.telephony.PhoneStateListener;

import com.airometric.classes.SignalStrengh;

public class SignalStrengthListener extends PhoneStateListener {

	String CDMADbm, CDMAEcio;
	String EvdoDbm, EvdoEcio;
	String EvdoSnr;
	String GSMBitRateError;
	String GSMSignalStrength, LteSignalStrength, LteRsrp, LteRsrq, LteRssnr,
			LteCqi;
	public String signal_strength;
	boolean isGSM = false;

	@Override
	public void onSignalStrengthChanged(int asu) {
		// TODO Auto-generated method stub
		super.onSignalStrengthChanged(asu);
	}

	@Override
	public void onSignalStrengthsChanged(
			android.telephony.SignalStrength signalStrength) {

		// System.out.println("SignalStrengthListener started!!");
		int strengthAmplitude = signalStrength.getGsmSignalStrength();
		signal_strength = String.valueOf(strengthAmplitude);

		CDMADbm = "" + signalStrength.getCdmaDbm();
		CDMAEcio = "" + signalStrength.getCdmaEcio();
		EvdoDbm = "" + signalStrength.getEvdoDbm();
		EvdoEcio = "" + signalStrength.getEvdoEcio();
		EvdoSnr = "" + signalStrength.getEvdoSnr();
		GSMBitRateError = "" + signalStrength.getGsmBitErrorRate();
		GSMSignalStrength = "" + signalStrength.getGsmSignalStrength();
		isGSM = signalStrength.isGsm();

		// L.debug("signalStrength --> " + signalStrength.toString());
		String ssignal = signalStrength.toString();

		String[] parts = ssignal.split(" ");

		// part[0] = "Signalstrength:" _ignore this, it's just the title_
		//
		// parts[1] = GsmSignalStrength
		//
		// parts[2] = GsmBitErrorRate
		//
		// parts[3] = CdmaDbm
		//
		// parts[4] = CdmaEcio
		//
		// parts[5] = EvdoDbm
		//
		// parts[6] = EvdoEcio
		//
		// parts[7] = EvdoSnr
		//
		// parts[8] = LteSignalStrength
		//
		// parts[9] = LteRsrp
		//
		// parts[10] = LteRsrq
		//
		// parts[11] = LteRssnr
		//
		// parts[12] = LteCqi
		//
		// parts[13] = gsm|lte
		//
		// parts[14] = _not reall sure what this number is_
		// TelephonyManager tm =
		// (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		//
		// int dbm = 0;
		//
		// if ( tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
		//
		// dbm = Integer.parseInt(parts[8])*2-113;
		//
		// }
		// else{
		//
		// if (signalStrength.getGsmSignalStrength() != 99) {
		// int intdbm = -113 + 2
		// * signalStrength.getGsmSignalStrength();
		// dbm = Integer.toString(intdbm);
		// }
		// }

		if (parts.length > 7)
			LteSignalStrength = parts[8];
		if (parts.length > 8)
			LteRsrp = parts[9];
		if (parts.length > 9)
			LteRsrq = parts[10];
		if (parts.length > 10) {
			LteRssnr = parts[11];
		
			//int abc = Integer.decode(parts[6]); 
		}
		
		
		if (parts.length > 1)
			LteCqi = parts[12];

		SignalStrengh sg = new SignalStrengh(CDMADbm, CDMAEcio, EvdoDbm,
				EvdoEcio, EvdoSnr, GSMBitRateError, GSMSignalStrength,
				signal_strength, isGSM, LteSignalStrength, LteRsrp, LteRsrq,
				LteRssnr, LteCqi);

		onSignalChanged(sg);

		super.onSignalStrengthsChanged(signalStrength);
	}

	public void onSignalChanged(SignalStrengh sg) {
	}
}
