package com.to;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Stg_Log_Cat_TO implements Comparable<Stg_Log_Cat_TO> {

	private String time_stamp;
	private String event_value;
	private String event_name;
	private String ping_server_name;
	private String ping_server_ip;
	private String ping_packets_transmitted;
	private String ping_packets_received;
	private String ping_packets_loss;
	private String ping_transmission_time;
	private String ping_rtt_min;
	private String ping_rtt_avg;
	private String ping_rtt_max;
	private String ping_rtt_mdev;
	private String test_type;
	private String imei_number;
	private String test_name;
	private String snapShotId;
	
	private DeviceInfoTO deviceInfoTO;
	
	

	public DeviceInfoTO getDeviceInfoTO() {
		return deviceInfoTO;
	}

	public void setDeviceInfoTO(DeviceInfoTO deviceInfoTO) {
		this.deviceInfoTO = deviceInfoTO;
	}

	public String getSnapShotId() {
		return snapShotId;
	}

	public void setSnapShotId(String snapShotId) {
		this.snapShotId = snapShotId;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String eventName) {
		event_name = eventName;
	}

	public String getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(String timeStamp) {
		time_stamp = timeStamp;
	}

	public String getEvent_value() {
		return event_value;
	}

	public void setEvent_value(String eventValue) {
		event_value = eventValue;
	}

	public String getPing_server_name() {
		return ping_server_name;
	}

	public void setPing_server_name(String pingServerName) {
		ping_server_name = pingServerName;
	}

	public String getPing_server_ip() {
		return ping_server_ip;
	}

	public void setPing_server_ip(String pingServerIp) {
		ping_server_ip = pingServerIp;
	}

	public String getPing_packets_transmitted() {
		return ping_packets_transmitted;
	}

	public void setPing_packets_transmitted(String pingPacketsTransmitted) {
		ping_packets_transmitted = pingPacketsTransmitted;
	}

	public String getPing_packets_received() {
		return ping_packets_received;
	}

	public void setPing_packets_received(String pingPacketsReceived) {
		ping_packets_received = pingPacketsReceived;
	}

	public String getPing_packets_loss() {
		return ping_packets_loss;
	}

	public void setPing_packets_loss(String pingPacketsLoss) {
		ping_packets_loss = pingPacketsLoss;
	}

	public String getPing_transmission_time() {
		return ping_transmission_time;
	}

	public void setPing_transmission_time(String pingTransmissionTime) {
		ping_transmission_time = pingTransmissionTime;
	}

	public String getPing_rtt_min() {
		return ping_rtt_min;
	}

	public void setPing_rtt_min(String pingRttMin) {
		ping_rtt_min = pingRttMin;
	}

	public String getPing_rtt_avg() {
		return ping_rtt_avg;
	}

	public void setPing_rtt_avg(String pingRttAvg) {
		ping_rtt_avg = pingRttAvg;
	}

	public String getPing_rtt_max() {
		return ping_rtt_max;
	}

	public void setPing_rtt_max(String pingRttMax) {
		ping_rtt_max = pingRttMax;
	}

	public String getPing_rtt_mdev() {
		return ping_rtt_mdev;
	}

	public void setPing_rtt_mdev(String pingRttMdev) {
		ping_rtt_mdev = pingRttMdev;
	}

	public String getTest_type() {
		return test_type;
	}

	public void setTest_type(String testType) {
		test_type = testType;
	}

	public String getImei_number() {
		return imei_number;
	}

	public void setImei_number(String imeiNumber) {
		imei_number = imeiNumber;
	}

	public String getTest_name() {
		return test_name;
	}

	public void setTest_name(String testName) {
		test_name = testName;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
//		System.out.println("herrrrrrrrrr");
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public int compareTo(Stg_Log_Cat_TO arg0) {
		// TODO Auto-generated method stub
		SimpleDateFormat newSdf = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss.SSS");
		try{
			Date currentdate = newSdf.parse(arg0.getTime_stamp());
			Date dateToCompare = newSdf.parse(this.getTime_stamp());
//			System.out.println("herrrrrrrrrr");
//			System.out.println(currentdate);
//			System.out.println(dateToCompare);
			return dateToCompare.compareTo(currentdate);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	
	
	
	

}
