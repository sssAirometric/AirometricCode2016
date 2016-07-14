package com.airometric.classes;

import java.io.Serializable;

public class VOIPTestConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5664986537590829266L;
	public String sTestDuration;

	public VOIPTestConfig(String sTestDuration) {
		this.sTestDuration = sTestDuration;
	}

	@Override
	public String toString() {
		String toString = "VOIPTestConfig(): Test Duration = " + sTestDuration;
		return toString;
	}
}
