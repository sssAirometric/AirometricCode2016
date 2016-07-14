package com.report.result.to;

import java.util.List;

public class UDPChartTO {

	private List<Float> udpDownloadQOSList;
	private List<Float> udpDownloadCapacityList;
	private List<Float> udpUploadCapacityList;
	private List<Float> ressList;
	private List<Float> wifiDownloadList;
	private List<Float> wifiUploadList;
	private List<Float> wifiQosList;
	private List<Float> wifiRssiList;
	private List<String> networkTypes;

	public List<Float> getUdpDownloadQOSList() {
		return udpDownloadQOSList;
	}

	public void setUdpDownloadQOSList(List<Float> udpDownloadQOSList) {
		this.udpDownloadQOSList = udpDownloadQOSList;
	}

	public List<Float> getUdpDownloadCapacityList() {
		return udpDownloadCapacityList;
	}

	public void setUdpDownloadCapacityList(List<Float> udpDownloadCapacityList) {
		this.udpDownloadCapacityList = udpDownloadCapacityList;
	}

	public List<Float> getUdpUploadCapacityList() {
		return udpUploadCapacityList;
	}

	public void setUdpUploadCapacityList(List<Float> udpUploadCapacityList) {
		this.udpUploadCapacityList = udpUploadCapacityList;
	}

	public List<Float> getRessList() {
		return ressList;
	}

	public void setRessList(List<Float> ressList) {
		this.ressList = ressList;
	}

	public List<Float> getWifiDownloadList() {
		return wifiDownloadList;
	}

	public void setWifiDownloadList(List<Float> wifiDownloadList) {
		this.wifiDownloadList = wifiDownloadList;
	}

	public List<Float> getWifiUploadList() {
		return wifiUploadList;
	}

	public void setWifiUploadList(List<Float> wifiUploadList) {
		this.wifiUploadList = wifiUploadList;
	}

	public List<Float> getWifiQosList() {
		return wifiQosList;
	}

	public void setWifiQosList(List<Float> wifiQosList) {
		this.wifiQosList = wifiQosList;
	}

	public List<Float> getWifiRssiList() {
		return wifiRssiList;
	}

	public void setWifiRssiList(List<Float> wifiRssiList) {
		this.wifiRssiList = wifiRssiList;
	}

	public List<String> getNetworkTypes() {
		return networkTypes;
	}

	public void setNetworkTypes(List<String> networkTypes) {
		this.networkTypes = networkTypes;
	}

}
