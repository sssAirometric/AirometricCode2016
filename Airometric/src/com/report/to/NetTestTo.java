package com.report.to;

import com.to.DeviceInfoTO;

public class NetTestTo {

	private String timeStamp;
	private String tcp_Download_Effective_Speed;
	private String tcp_Download_QoS;
	private String tcp_Download_Average_Delay;
	private String tcp_Download_Bytes_Retransmitted;
	private String tcp_Download_Duplicate_Acks;
	private String tcp_Upload_Effective_Speed;
	private String tcp_Upload_QoS;
	private String tcp_Upload_Average_Delay;
	private String tcp_Upload_Bytes_Retransmitted;
	private String tcp_Round_Trip_Time;
	
	
	private String UDPDownloadCapacity;
	private String UDPDownloadQOS;
	private String UDPDownloadPacketSize;
	private String UDPDownloadKiloPacketsPerSec;
	private String UDPUploadCapacity;
	private String UDPUploadQOS;
	private String UDPUploadPacketSize;
	private String UDPUploadKiloPacketsPerSec;
	
	private String imeiNumber;
	private DeviceInfoTO deviceInfoTO;

	
	
	
	
	
	public String getUDPDownloadCapacity() {
		return UDPDownloadCapacity;
	}

	public void setUDPDownloadCapacity(String uDPDownloadCapacity) {
		UDPDownloadCapacity = uDPDownloadCapacity;
	}

	public String getUDPDownloadQOS() {
		return UDPDownloadQOS;
	}

	public void setUDPDownloadQOS(String uDPDownloadQOS) {
		UDPDownloadQOS = uDPDownloadQOS;
	}

	public String getUDPDownloadPacketSize() {
		return UDPDownloadPacketSize;
	}

	public void setUDPDownloadPacketSize(String uDPDownloadPacketSize) {
		UDPDownloadPacketSize = uDPDownloadPacketSize;
	}

	public String getUDPDownloadKiloPacketsPerSec() {
		return UDPDownloadKiloPacketsPerSec;
	}

	public void setUDPDownloadKiloPacketsPerSec(String uDPDownloadKiloPacketsPerSec) {
		UDPDownloadKiloPacketsPerSec = uDPDownloadKiloPacketsPerSec;
	}

	public String getUDPUploadCapacity() {
		return UDPUploadCapacity;
	}

	public void setUDPUploadCapacity(String uDPUploadCapacity) {
		UDPUploadCapacity = uDPUploadCapacity;
	}

	public String getUDPUploadQOS() {
		return UDPUploadQOS;
	}

	public void setUDPUploadQOS(String uDPUploadQOS) {
		UDPUploadQOS = uDPUploadQOS;
	}

	public String getUDPUploadPacketSize() {
		return UDPUploadPacketSize;
	}

	public void setUDPUploadPacketSize(String uDPUploadPacketSize) {
		UDPUploadPacketSize = uDPUploadPacketSize;
	}

	public String getUDPUploadKiloPacketsPerSec() {
		return UDPUploadKiloPacketsPerSec;
	}

	public void setUDPUploadKiloPacketsPerSec(String uDPUploadKiloPacketsPerSec) {
		UDPUploadKiloPacketsPerSec = uDPUploadKiloPacketsPerSec;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public DeviceInfoTO getDeviceInfoTO() {
		return deviceInfoTO;
	}

	public void setDeviceInfoTO(DeviceInfoTO deviceInfoTO) {
		this.deviceInfoTO = deviceInfoTO;
	}

	public String getTcp_Download_Effective_Speed() {
		return tcp_Download_Effective_Speed;
	}

	public void setTcp_Download_Effective_Speed(String tcpDownloadEffectiveSpeed) {
		tcp_Download_Effective_Speed = tcpDownloadEffectiveSpeed;
	}

	public String getTcp_Download_QoS() {
		return tcp_Download_QoS;
	}

	public void setTcp_Download_QoS(String tcpDownloadQoS) {
		tcp_Download_QoS = tcpDownloadQoS;
	}

	public String getTcp_Download_Average_Delay() {
		return tcp_Download_Average_Delay;
	}

	public void setTcp_Download_Average_Delay(String tcpDownloadAverageDelay) {
		tcp_Download_Average_Delay = tcpDownloadAverageDelay;
	}

	public String getTcp_Download_Bytes_Retransmitted() {
		return tcp_Download_Bytes_Retransmitted;
	}

	public void setTcp_Download_Bytes_Retransmitted(
			String tcpDownloadBytesRetransmitted) {
		tcp_Download_Bytes_Retransmitted = tcpDownloadBytesRetransmitted;
	}

	public String getTcp_Download_Duplicate_Acks() {
		return tcp_Download_Duplicate_Acks;
	}

	public void setTcp_Download_Duplicate_Acks(String tcpDownloadDuplicateAcks) {
		tcp_Download_Duplicate_Acks = tcpDownloadDuplicateAcks;
	}

	public String getTcp_Upload_Effective_Speed() {
		return tcp_Upload_Effective_Speed;
	}

	public void setTcp_Upload_Effective_Speed(String tcpUploadEffectiveSpeed) {
		tcp_Upload_Effective_Speed = tcpUploadEffectiveSpeed;
	}

	public String getTcp_Upload_QoS() {
		return tcp_Upload_QoS;
	}

	public void setTcp_Upload_QoS(String tcpUploadQoS) {
		tcp_Upload_QoS = tcpUploadQoS;
	}

	public String getTcp_Upload_Average_Delay() {
		return tcp_Upload_Average_Delay;
	}

	public void setTcp_Upload_Average_Delay(String tcpUploadAverageDelay) {
		tcp_Upload_Average_Delay = tcpUploadAverageDelay;
	}

	public String getTcp_Upload_Bytes_Retransmitted() {
		return tcp_Upload_Bytes_Retransmitted;
	}

	public void setTcp_Upload_Bytes_Retransmitted(
			String tcpUploadBytesRetransmitted) {
		tcp_Upload_Bytes_Retransmitted = tcpUploadBytesRetransmitted;
	}

	public String getTcp_Round_Trip_Time() {
		return tcp_Round_Trip_Time;
	}

	public void setTcp_Round_Trip_Time(String tcpRoundTripTime) {
		tcp_Round_Trip_Time = tcpRoundTripTime;
	}

}
