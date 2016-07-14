package com.airometric.classes;

import java.io.Serializable;

import android.content.Context;
import com.airometric.storage.Preferences;

public class SignalStrengh implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5125883050483169270L;
	public String CDMADbm, CDMAEcio, EvdoDbm, EvdoEcio, EvdoSnr,
			GSMBitRateError, GSMSignalStrength, LteSignalStrength, LteRsrp,
			LteRsrq, LteRssnr, LteCqi;
	public String strsignal_strength;
	public boolean blisGSM;

	Preferences pref;
	Context Objcontext;

	public SignalStrengh(String CDMADbm, String CDMAEcio, String EvdoDbm,
			String EvdoEcio, String EvdoSnr, String GSMBitRateError,
			String GSMSignalStrength, String signal_strength, boolean isGSM,
			String LteSignalStrength, String LteRsrp, String LteRsrq,
			String LteRssnr, String LteCqi) {
		this.CDMADbm = CDMADbm;
		this.CDMAEcio = CDMAEcio;
		this.EvdoDbm = EvdoDbm;
		this.EvdoEcio = EvdoEcio;
		this.EvdoSnr = EvdoSnr;
		this.GSMBitRateError = GSMBitRateError;
		this.GSMSignalStrength = GSMSignalStrength;
		this.strsignal_strength = signal_strength;
		this.blisGSM = isGSM;

		this.LteSignalStrength = LteSignalStrength;
		this.LteRsrp = LteRsrp;
		this.LteRsrq = LteRsrq;
		this.LteRssnr = LteRssnr;
		this.LteCqi = LteCqi;
	}

	@Override
	public String toString() {
		String string = "CDMADbm = " + CDMADbm + ", CDMAEcio = " + CDMAEcio
				+ ", EvdoDbm = " + EvdoDbm + ", EvdoEcio = " + EvdoEcio
				+ ", EvdoSnr = " + EvdoSnr + ", GSMBitRateError = "
				+ GSMBitRateError + ", GSMSignalStrength = "
				+ GSMSignalStrength + ", strsignal_strength = "
				+ strsignal_strength + ", blisGSM = " + blisGSM
				+ ", LteSignalStrength = " + LteSignalStrength + ", LteRsrp = "
				+ LteRsrp + ", LteRsrq = " + LteRsrq + ", LteRssnr = "
				+ LteRssnr + ", LteCqi = " + LteCqi;
		return string;
	}
}
