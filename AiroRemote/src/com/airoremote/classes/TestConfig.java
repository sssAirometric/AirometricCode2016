package com.airoremote.classes;

import java.io.Serializable;

public class TestConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2713109140519753878L;
	/**
	 * 
	 */
	private MOTestConfig MOConfigTestObj;
	private MTTestConfig MTConfigTestObj;
	private FTPTestConfig FTPTestConfigObj;
	private UDPTestConfig UDPTestConfigObj;
	private PingTestConfig PingTestConfigObj;
	private BrowserTestConfig BrowserTestConfigObj;
	private VOIPTestConfig VOIPTestConfigObj;

	private String sTestName;
	private String sMarketId;
	private int iCurrCycle;
	
	private boolean IsExternalTest;

	public boolean getIsExternalTest() {
		return IsExternalTest;
	}

	public void setIsExternalTest(boolean isExternalTest) {
		IsExternalTest = isExternalTest;
	}

	public TestConfig() {
	}

	public TestConfig(String sTestName) {
		this.sTestName = sTestName;
	}

	public TestConfig(MOTestConfig MOConfigTestObj) {
		this.MOConfigTestObj = MOConfigTestObj;
	}

	public void setMOTestConfig(MOTestConfig MOConfigTestObj) {
		this.MOConfigTestObj = MOConfigTestObj;
	}

	public MOTestConfig getMOTestConfig() {
		return this.MOConfigTestObj;
	}

	public void setMTTestConfig(MTTestConfig MTConfigTestObj) {
		this.MTConfigTestObj = MTConfigTestObj;
	}

	public MTTestConfig getMTTestConfig() {
		return this.MTConfigTestObj;
	}

	public void setFTPTestConfig(FTPTestConfig FTPTestConfigObj) {
		this.FTPTestConfigObj = FTPTestConfigObj;
	}

	public FTPTestConfig getFTPTestConfig() {
		return this.FTPTestConfigObj;
	}

	public void setUDPTestConfig(UDPTestConfig UDPTestConfigObj) {
		this.UDPTestConfigObj = UDPTestConfigObj;
	}

	public UDPTestConfig getUDPTestConfig() {
		return this.UDPTestConfigObj;
	}

	public void setPingTestConfig(PingTestConfig PingTestConfigObj) {
		this.PingTestConfigObj = PingTestConfigObj;
	}

	public PingTestConfig getPingTestConfig() {
		return this.PingTestConfigObj;
	}

	public void setBrowserTestConfig(BrowserTestConfig BrowserTestConfigObj) {
		this.BrowserTestConfigObj = BrowserTestConfigObj;
	}

	public BrowserTestConfig getBrowserTestConfig() {
		return this.BrowserTestConfigObj;
	}

	public void setVOIPTestConfig(VOIPTestConfig VOIPTestConfigObj) {
		this.VOIPTestConfigObj = VOIPTestConfigObj;
	}

	public VOIPTestConfig getVOIPTestConfig() {
		return this.VOIPTestConfigObj;
	}

	public String getTestName() {
		return this.sTestName;
	}

	public void setTestName(String name) {
		this.sTestName = name;
	}
	
	public String getMarketId() {
		return sMarketId;
	}
	
	public void setMarketId(String sMarketId) {
		this.sMarketId = sMarketId;
	}

	public int getTestCycle() {
		return this.iCurrCycle;
	}

	public void setTestCycle(int iCurrCycle) {
		this.iCurrCycle = iCurrCycle;
	}
}
