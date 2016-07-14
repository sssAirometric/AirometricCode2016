package com.airometric.classes;

import java.io.Serializable;

public class BrowserTestConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5664986537590829266L;
	public String sPageURL, sCycles;

	public BrowserTestConfig(String sPageURL, String sCycles) {
		this.sPageURL = sPageURL;
		this.sCycles = sCycles;
	}

	@Override
	public String toString() {
		String toString = "BrowserTestConfig() :: Page URL = " + sPageURL
				+ ", Cycles = " + sCycles;
		return toString;
	}
}
