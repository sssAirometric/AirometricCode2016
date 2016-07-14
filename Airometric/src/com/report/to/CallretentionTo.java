package com.report.to;

public class CallretentionTo {

	private String marketName;
	private String testName;
	private float totalCalls;
	private float dropCalls;
	private float missedCalls;
	private float dcr;
	private float access_failure;
	private String parameter;
	private String timeStamp;

	
	
	public float getMissedCalls() {
		return missedCalls;
	}

	public void setMissedCalls(float missedCalls) {
		this.missedCalls = missedCalls;
	}

	public float getAccess_failure() {
		return access_failure;
	}

	public void setAccess_failure(float accessFailure) {
		access_failure = accessFailure;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public float getTotalCalls() {
		return totalCalls;
	}

	public void setTotalCalls(float totalCalls) {
		this.totalCalls = totalCalls;
	}

	public float getDropCalls() {
		return dropCalls;
	}

	public void setDropCalls(float dropCalls) {
		this.dropCalls = dropCalls;
	}

	public float getDcr() {
		return dcr;
	}

	public void setDcr(float dcr) {
		this.dcr = dcr;
	}
}
