package com.airometric.classes;

import java.io.Serializable;

public class UDPTestConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5664986537590829266L;
	public String sServerIP, sServerPort, sTestCycles, sFileToUpload;

	public UDPTestConfig(String sServerIP, String sServerPort,
			String sTestCycles, String sFileToUpload) {
		this.sServerIP = sServerIP;
		this.sServerPort = sServerPort;
		this.sTestCycles = sTestCycles;
		this.sFileToUpload = sFileToUpload;

	}

	@Override
	public String toString() {
		String toString = "UDPTestConfig() :: Server IP = " + sServerIP
				+ ",  Server Port = " + sServerPort + ",  Cycles = "
				+ sTestCycles + ",  File to upload = " + sFileToUpload;
		return toString;
	}
}
