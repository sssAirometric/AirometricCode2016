package com.report.to;

public class ThrougputDataTO {

	String signalStrengthLTT;
	String lattitudes;
	String longitudes;
	String signalStrength;
	String signalStrengthRating;
	String networkType;
	String timeStampForEachSample;
	String throughput;
	String cellLocationCID;
	String testName;
	String eventName;

	
	
	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getSignalStrengthLTT() {
		return signalStrengthLTT;
	}

	public void setSignalStrengthLTT(String signalStrengthLTT) {
		this.signalStrengthLTT = signalStrengthLTT;
	}

	public String getLattitudes() {
		return lattitudes;
	}

	public void setLattitudes(String lattitudes) {
		this.lattitudes = lattitudes;
	}

	public String getLongitudes() {
		return longitudes;
	}

	public void setLongitudes(String longitudes) {
		this.longitudes = longitudes;
	}

	public String getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(String signalStrength) {
		this.signalStrength = signalStrength;
	}

	public String getSignalStrengthRating() {
		return signalStrengthRating;
	}

	public void setSignalStrengthRating(String signalStrengthRating) {
		this.signalStrengthRating = signalStrengthRating;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getTimeStampForEachSample() {
		return timeStampForEachSample;
	}

	public void setTimeStampForEachSample(String timeStampForEachSample) {
		this.timeStampForEachSample = timeStampForEachSample;
	}

	public String getThroughput() {
		return throughput;
	}

	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}

	public String getCellLocationCID() {
		return cellLocationCID;
	}

	public void setCellLocationCID(String cellLocationCID) {
		this.cellLocationCID = cellLocationCID;
	}

}
