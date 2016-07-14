package com.report.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.to.DeviceInfoTO;

public class FTPReportHelper {

	public LinkedHashMap<String, List<DeviceInfoTO>> getCommonListForAllcycles(
			List<List<DeviceInfoTO>> ftpList) {
		LinkedHashMap<String, List<DeviceInfoTO>> qualityWiseMap = new LinkedHashMap<String, List<DeviceInfoTO>>();
		qualityWiseMap.put("Good", null);
		qualityWiseMap.put("Medium", null);
		qualityWiseMap.put("Poor", null);
		if (null != ftpList) {
			for (int i = 0; i < ftpList.size(); i++) {
				List<DeviceInfoTO> subList = ftpList.get(i);
				HashMap<String, List<DeviceInfoTO>> tempQualityMap = getThroughputForCycle(subList);
				Iterator<String> qualityItr = tempQualityMap.keySet().iterator();
				while(qualityItr.hasNext()){
					String qualityName = qualityItr.next();
					List<DeviceInfoTO> tempList = tempQualityMap.get(qualityName);
					List<DeviceInfoTO> mainList = qualityWiseMap.get(qualityName);
					if(null==mainList){
						mainList = new ArrayList<DeviceInfoTO>();
					}
					mainList.addAll(tempList);
					qualityWiseMap.put(qualityName, mainList);
				}
			}
		}
		return qualityWiseMap;
	}

	private HashMap<String, List<DeviceInfoTO>> getThroughputForCycle(
			List<DeviceInfoTO> subList) {
		List<Float> rssiList = new ArrayList<Float>();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		HashMap<String, List<DeviceInfoTO>> qualityWiseMap = new HashMap<String, List<DeviceInfoTO>>();
		//System.out.println("subList----------"+subList);
		try {
			for (int i = 0; i < subList.size(); i++) {
				if (i + 1 != subList.size()) {
					DeviceInfoTO deviceInfoTO = new DeviceInfoTO();
					DeviceInfoTO deviceInfoTo = subList.get(i);
					DeviceInfoTO nextDeviceInfo = subList.get(i + 1);
//					rssiList.add(new Float(deviceInfoTo.getSignalStrength()));

					Float currentThroughPut = new Float(deviceInfoTo
							.getEventValue());
					Float nextThroughPut = new Float(nextDeviceInfo
							.getEventValue());
					Date currentTimeStamp = newSdf.parse(deviceInfoTo
							.getTime_stamp());
					Date nextTimeStamp = newSdf.parse(nextDeviceInfo
							.getTime_stamp());

					c.setTime(currentTimeStamp);
					long currentSecs = (c.get(Calendar.SECOND)
							+ c.get(Calendar.MINUTE) * 60 + c
							.get(Calendar.HOUR) * 3600);

					c.setTime(nextTimeStamp);
					long nextSecs = (c.get(Calendar.SECOND)
							+ c.get(Calendar.MINUTE) * 60 + c
							.get(Calendar.HOUR) * 3600);
					if (nextSecs - currentSecs != 0) {
						Float tempThroughput = (nextThroughPut - currentThroughPut)
								/ (nextSecs - currentSecs);
						deviceInfoTO.setThroughput(tempThroughput.toString());
					}
					//System.out.println("before");
					Float signalStrength = (new Float(deviceInfoTo.getSignalStrength())+new Float(nextDeviceInfo.getSignalStrength()))/2;
					//System.out.println("signalStrength------"+signalStrength);
					String quality = getsignalQuality(signalStrength);
					deviceInfoTO.setSignalStrength(signalStrength.toString());
					
					List<DeviceInfoTO> tempListFinalCal = qualityWiseMap.get(quality);
					if(null == tempListFinalCal){
						tempListFinalCal = new ArrayList<DeviceInfoTO>();
					}
					tempListFinalCal.add(deviceInfoTO);
					qualityWiseMap.put(quality, tempListFinalCal);
				}
			}
			rssiList.add(new Float(subList.get(subList.size() - 1)
					.getSignalStrength()));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return qualityWiseMap;
	}

	public String getsignalQuality(Float rssiValue) {

		String quality = "";
		if(null!=rssiValue){
			if (rssiValue < -45 && rssiValue > -70) {
				quality = "Good";
			} else if (rssiValue < -71 && rssiValue > -89) {
				quality = "Medium";
			} else if (rssiValue < -91) {
				quality = "Poor";
			}
		}
	
		/*//System.out.println("rssiValue---------" + rssiValue
				+ "-----quality-------" + quality);*/
		return quality;
	}

	public Float getAverageList(List<Float> list) {
		Float averagevalue = new Float(0);
		Float listSum = new Float(0);
		try{
			if (null != list) {
				//System.out.println("list-----++-----"+list.size());
				for (int i = 0; i < list.size(); i++) {
					listSum = listSum + list.get(i);
				}
				if(list.size()>0){
					averagevalue = listSum / list.size();
				}
				else{
					averagevalue = null;
				}
			} else {
				averagevalue = null;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return averagevalue;
	}

	public List<Float> getListOfValuesToRenderFTPChart(
			HashMap<String, HashMap<String, Float>> tempHashMap) {
		List<Float> finalList = new ArrayList<Float>();
		Iterator<String> tempItr = tempHashMap.keySet().iterator();
		while (tempItr.hasNext()) {
			String networkType = tempItr.next();
			HashMap<String, Float> qualityWiseMap = tempHashMap
					.get(networkType);
			finalList.add(qualityWiseMap.get("Good"));
			finalList.add(qualityWiseMap.get("Medium"));
			finalList.add(qualityWiseMap.get("Poor"));
		}
		return finalList;
	}

	public List<Float> getListOfRx_TxValuesToRenderFTPChart(
			HashMap<String, HashMap<String, Float>> tempHashMap,
			HashMap<String, HashMap<String, Float>> tempRssiHashMap) {
		List<Float> finalList = new ArrayList<Float>(tempHashMap.size()*3);
		int index = 0;
		for(int i=0;i<tempHashMap.size()*3;i++){
			finalList.add(null);
		}
		
		Iterator<String> tempItr = tempHashMap.keySet().iterator();
		List<Float> tempList = new ArrayList<Float>();
		while (tempItr.hasNext()) {
			String networkType = tempItr.next();
			HashMap<String, Float> qualityWiseMap = tempHashMap
					.get(networkType);
			HashMap<String, Float> qualityRssiWiseMap = tempRssiHashMap
					.get(networkType);
			//System.out.println("----qualityWiseMap------"+qualityWiseMap);
			if (null != qualityWiseMap.get("Good")) {
				tempList.add(qualityWiseMap.get("Good"));
			}
			if (null != qualityWiseMap.get("Medium")) {
				tempList.add(qualityWiseMap.get("Medium"));
			}
			if (null != qualityWiseMap.get("Poor")) {
				tempList.add(qualityWiseMap.get("Poor"));
			}
			Float avg = getAverageList(tempList);
			//System.out.println("avg----------"+avg);
			if (null != qualityRssiWiseMap.get("Good")) {
				finalList.set(index, avg);
			}
			index++;
			if (null != qualityRssiWiseMap.get("Medium")) {
				finalList.set(index, avg);
			}
			index++;
			if (null != qualityRssiWiseMap.get("Poor")) {
				finalList.set(index, avg);
			}
			index++;
		}
		return finalList;
	}

	public HashMap<String, Float> getRssiValue(List<List<DeviceInfoTO>> ftpList) {
		List<Float> rssiList = new ArrayList<Float>();
		//System.out.println("srikanth----------" + ftpList);
		HashMap<String, List<Float>> qualityWiseMap = new HashMap<String, List<Float>>();
		LinkedHashMap<String, Float> avgqualityWiseMap = new LinkedHashMap<String, Float>();
		List<DeviceInfoTO> combinedMasterList = new ArrayList<DeviceInfoTO>();
		avgqualityWiseMap.put("Good", null);
		avgqualityWiseMap.put("Medium", null);
		avgqualityWiseMap.put("Poor", null);
		if (null != ftpList) {
			for (int i = 0; i < ftpList.size(); i++) {
				List<DeviceInfoTO> subList = ftpList.get(i);
				combinedMasterList.addAll(subList);
			}
		}

		for (int i = 0; i < combinedMasterList.size(); i++) {
			//System.out.println("combinedMasterList.get(i)---------"
//					+ combinedMasterList.get(i).getSignalStrength());
			rssiList.add(new Float(combinedMasterList.get(i)
					.getSignalStrength()));
		}
		//System.out.println("before average----------");
		Float avgRssiValue = getAverageList(rssiList);
		String quality = getsignalQuality(avgRssiValue);
		if(null!=quality&&!quality.trim().equalsIgnoreCase("")){
			avgqualityWiseMap.put(quality, avgRssiValue);
		}
		
		//System.out.println("qualityWiseMap-------++------" + qualityWiseMap);
		return avgqualityWiseMap;
	}
	
	public static void main(String[] args) {
		new FTPReportHelper().getsignalQuality(new Float(-55));
	}
}
