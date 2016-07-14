package com.report.result.to;

import java.util.List;

public class TCPChartTO {

	private List<Float> TcpDownloadQOSList;
	private List<Float> TcpDownloadCapacityList;
	private List<Float> TcpUploadCapacityList;
	private List<Float> ressList;
	private List<Float> wifiDownloadList;
	private List<Float> wifiUploadList;
	private List<Float> wifiQosList;
	private List<Float> wifiRssiList;
	private List<String> networkTypes;

	public List<Float> getTcpDownloadQOSList() {
		return TcpDownloadQOSList;
	}

	public void setTcpDownloadQOSList(List<Float> TcpDownloadQOSList) {
		this.TcpDownloadQOSList = TcpDownloadQOSList;
	}

	public List<Float> getTcpDownloadCapacityList() {
		return TcpDownloadCapacityList;
	}

	public void setTcpDownloadCapacityList(List<Float> TcpDownloadCapacityList) {
		this.TcpDownloadCapacityList = TcpDownloadCapacityList;
	}

	public List<Float> getTcpUploadCapacityList() {
		return TcpUploadCapacityList;
	}

	public void setTcpUploadCapacityList(List<Float> TcpUploadCapacityList) {
		this.TcpUploadCapacityList = TcpUploadCapacityList;
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
