package com.to;

import java.io.Serializable;

public class STGDevice implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2361038173497947334L;
	private String VQuadTimestamp;
	private String CallTimestamp;
	private String VQuadLocation;
	private String VQuadPhoneID;
	private String VQuadLatLong;
	private String DegradedFilename;
	private String Rating;
	private String PESQ;
	private String PESQLQ;
	private String PESQLQO;
	private String PESQWB;
	private String PSQMPLUS;
	private String PESQAverageOffset;
	private String PESQMaxOffset;
	private String PESQMinOffset;
	private String NumberAllClipping;
	private String DurationALLClipping;
	private String MeanDurationALLClipping;
	private String DurationHangover;
	private String NumberOfHangover;
	private String AverageJitter;
	private String VQuadCallID;
	private String testName;
	private String deviceImei;
	private String signalStrengthGSM;
	private double lattitude;
	private double longitude;
	private double vquadLattitude;
	private String marketName;
	private String minCallTimestamp;
	private String maxCallTimestamp;
	
	private String polqa;
	private String snr;
	private String emodel;
	private String chartType;
	private String fileSource;
	private String rsrp;
	

	
	public String getRsrp() {
		return rsrp;
	}
	public void setRsrp(String rsrp) {
		this.rsrp = rsrp;
	}
	public String getFileSource() {
		return fileSource;
	}
	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	public String getEmodel() {
		return emodel;
	}
	public void setEmodel(String emodel) {
		this.emodel = emodel;
	}
	public String getPolqa() {
		return polqa;
	}
	public void setPolqa(String polqa) {
		this.polqa = polqa;
	}
	public String getSnr() {
		return snr;
	}
	public void setSnr(String snr) {
		this.snr = snr;
	}
	public String getMinCallTimestamp() {
		return minCallTimestamp;
	}
	public void setMinCallTimestamp(String minCallTimestamp) {
		this.minCallTimestamp = minCallTimestamp;
	}
	public String getMaxCallTimestamp() {
		return maxCallTimestamp;
	}
	public void setMaxCallTimestamp(String maxCallTimestamp) {
		this.maxCallTimestamp = maxCallTimestamp;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public double getVquadLattitude() {
		return vquadLattitude;
	}
	public void setVquadLattitude(double vquadLattitude) {
		this.vquadLattitude = vquadLattitude;
	}
	public double getLattitude() {
		return lattitude;
	}
	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getSignalStrengthGSM() {
		return signalStrengthGSM;
	}
	public void setSignalStrengthGSM(String signalStrengthGSM) {
		this.signalStrengthGSM = signalStrengthGSM;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getDeviceImei() {
		return deviceImei;
	}
	public void setDeviceImei(String deviceImei) {
		this.deviceImei = deviceImei;
	}
	public String getVQuadTimestamp() {
		return VQuadTimestamp;
	}
	public void setVQuadTimestamp(String vQuadTimestamp) {
		VQuadTimestamp = vQuadTimestamp;
	}
	public String getCallTimestamp() {
		return CallTimestamp;
	}
	public void setCallTimestamp(String callTimestamp) {
		CallTimestamp = callTimestamp;
	}
	public String getVQuadLocation() {
		return VQuadLocation;
	}
	public void setVQuadLocation(String vQuadLocation) {
		VQuadLocation = vQuadLocation;
	}
	public String getVQuadPhoneID() {
		return VQuadPhoneID;
	}
	public void setVQuadPhoneID(String vQuadPhoneID) {
		VQuadPhoneID = vQuadPhoneID;
	}
	public String getVQuadLatLong() {
		return VQuadLatLong;
	}
	public void setVQuadLatLong(String vQuadLatLong) {
		VQuadLatLong = vQuadLatLong;
	}
	public String getDegradedFilename() {
		return DegradedFilename;
	}
	public void setDegradedFilename(String degradedFilename) {
		DegradedFilename = degradedFilename;
	}
	public String getRating() {
		return Rating;
	}
	public void setRating(String rating) {
		Rating = rating;
	}
	public String getPESQ() {
		return PESQ;
	}
	public void setPESQ(String pESQ) {
		PESQ = pESQ;
	}
	public String getPESQLQ() {
		return PESQLQ;
	}
	public void setPESQLQ(String pESQLQ) {
		PESQLQ = pESQLQ;
	}
	public String getPESQLQO() {
		return PESQLQO;
	}
	public void setPESQLQO(String pESQLQO) {
		PESQLQO = pESQLQO;
	}
	public String getPESQWB() {
		return PESQWB;
	}
	public void setPESQWB(String pESQWB) {
		PESQWB = pESQWB;
	}
	public String getPSQMPLUS() {
		return PSQMPLUS;
	}
	public void setPSQMPLUS(String pSQMPLUS) {
		PSQMPLUS = pSQMPLUS;
	}
	public String getPESQAverageOffset() {
		return PESQAverageOffset;
	}
	public void setPESQAverageOffset(String pESQAverageOffset) {
		PESQAverageOffset = pESQAverageOffset;
	}
	public String getPESQMaxOffset() {
		return PESQMaxOffset;
	}
	public void setPESQMaxOffset(String pESQMaxOffset) {
		PESQMaxOffset = pESQMaxOffset;
	}
	public String getPESQMinOffset() {
		return PESQMinOffset;
	}
	public void setPESQMinOffset(String pESQMinOffset) {
		PESQMinOffset = pESQMinOffset;
	}
	public String getNumberAllClipping() {
		return NumberAllClipping;
	}
	public void setNumberAllClipping(String numberAllClipping) {
		NumberAllClipping = numberAllClipping;
	}
	public String getDurationALLClipping() {
		return DurationALLClipping;
	}
	public void setDurationALLClipping(String durationALLClipping) {
		DurationALLClipping = durationALLClipping;
	}
	public String getMeanDurationALLClipping() {
		return MeanDurationALLClipping;
	}
	public void setMeanDurationALLClipping(String meanDurationALLClipping) {
		MeanDurationALLClipping = meanDurationALLClipping;
	}
	public String getDurationHangover() {
		return DurationHangover;
	}
	public void setDurationHangover(String durationHangover) {
		DurationHangover = durationHangover;
	}
	public String getNumberOfHangover() {
		return NumberOfHangover;
	}
	public void setNumberOfHangover(String numberOfHangover) {
		NumberOfHangover = numberOfHangover;
	}
	public String getAverageJitter() {
		return AverageJitter;
	}
	public void setAverageJitter(String averageJitter) {
		AverageJitter = averageJitter;
	}
	public String getVQuadCallID() {
		return VQuadCallID;
	}
	public void setVQuadCallID(String vQuadCallID) {
		VQuadCallID = vQuadCallID;
	}

}
