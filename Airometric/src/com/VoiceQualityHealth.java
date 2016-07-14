package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import com.report.dao.impl.DataConnectivityDaoImpl;
import com.report.dao.impl.Voice_DataDaoImpl;
import com.report.healthindex.HealthIndexDBHelper;
import com.reports.kpi.DataConnectivity;
import com.tfocus.common.utill.JSONObject;
import com.to.BrowserBean;
import com.to.BrowserDaoImpl;
import com.to.HealthIndexTo;

import net.sf.json.JSONArray;

public class VoiceQualityHealth {
	
	
	public List<String> voiceQualityHealthJson(List<Float> detailedDownLink){
		float numberofHealthyValues = 0;
		double healthValue =  3.5;
		float healthPercent;
		System.out.println("detailedDownLink---------------------"+detailedDownLink);
		List<String> vqHealthIndexList = new ArrayList<String>();
		if(null!=detailedDownLink){
			for(int k=0;k<detailedDownLink.size();k++){
				//System.out.println("allValueList---"+detailedDownLink.get(k));
				if(detailedDownLink.get(k)>=healthValue){
					numberofHealthyValues++;
				}
				//System.out.println("numberofHealthyValues------"+numberofHealthyValues);
			}
		}
			System.out.println("numberofHealthyValues---++---"+numberofHealthyValues+"allValueList.size()----"+detailedDownLink.size());
			if(null!=detailedDownLink&&detailedDownLink.size()>0){
			 healthPercent = ((numberofHealthyValues*100)/detailedDownLink.size());
			//System.out.println("healthPercent---------"+healthPercent);
			}else{
				healthPercent=0;
			}
			vqHealthIndexList.add(String.valueOf(healthPercent));
			vqHealthIndexList.add(String.valueOf(numberofHealthyValues));
		return vqHealthIndexList;
		
	}
	
	
	public HashMap<String, HealthIndexTo> getHealthIndexForAllKpi (HashMap<String, HashMap<String, List<HealthIndexTo>>> testWiseMarketMap){
		List<HealthIndexTo> averagehealthIndexToList = new ArrayList<HealthIndexTo>();
		HashMap<String,HealthIndexTo> avgHealthIndexMap = new HashMap<String, HealthIndexTo>();
		List<HealthIndexTo> allhealthIndexToList = new ArrayList<HealthIndexTo>();
		for (Entry<String, HashMap<String, List<HealthIndexTo>>> entry : testWiseMarketMap.entrySet()) {
		   // System.out.println("Key ====== " + entry.getKey() );
		   // System.out.println("Value ========= " + entry.getValue());
		    HashMap<String, List<HealthIndexTo>> mktidMap= entry.getValue();
		    String testName = entry.getKey();
		    allhealthIndexToList = new ArrayList<HealthIndexTo>();
		    
		    Iterator<String> marketIdsItr = mktidMap.keySet().iterator();
			while(marketIdsItr.hasNext()){
				String marktid = marketIdsItr.next();
				allhealthIndexToList.addAll((mktidMap.get(marktid)));
				avgHealthIndexMap.put(testName,getVQAvgHealthIndex(allhealthIndexToList));
				
			}
		    
		}
		
		
		
		return avgHealthIndexMap;
	}
	private HealthIndexTo getVQAvgHealthIndex(List<HealthIndexTo> allhealthIndexList){
		HealthIndexTo avgHealthIndexTo = new HealthIndexTo();
		float numberofVQFiles = 0;
		float totalVQHealthIndex = 0;
		float avgVQHealthIndex = 0;
		
		float numberofVCFiles = 0;
		float totalVCHealthIndex = 0;
		float avgVCHealthIndex = 0;
		
		float numberofDCFiles = 0;
		float totalDCHealthIndex = 0;
		float avgDCHealthIndex = 0;
		
		float numberofLTEFiles = 0;
		float totalLTEHealthIndex = 0;
		float avgLTEHealthIndex = 0;
		
		
		float numberofVoipFiles = 0;
		float totalVoipEHealthIndex = 0;
		float avgVoipHealthIndex = 0;
		
		float numberofVDFiles = 0;
		float totalVDEHealthIndex = 0;
		float avgVDHealthIndex = 0;
		
		float numberofBrwsrFiles = 0;
		float totalBrwsrEHealthIndex = 0;
		float avgBrwsrHealthIndex = 0;
		
		String marketIds = "";
		Set<String> marketIdset = new HashSet<String>();
		String vqAvg = "";
		String vcAvg = "";
		String dcAvg = "";
		String vdAvg = "";
		String ltAvg = "";
		String browserAvg = "";
		String voipAvg = "";
		String testName = "";
		String deviceName = "";
		
		
		for(int i =0;i<allhealthIndexList.size();i++){
			HealthIndexTo healthIndex = new HealthIndexTo();
			healthIndex=allhealthIndexList.get(i);
			//System.out.println("healthIndex.getVoicequalityHealthIndex()---+++--"+healthIndex.getVoicequalityHealthIndex());
			//System.out.println("getVoiceConnectivityHealthIndex()---+++--"+healthIndex.getVoiceConnectivityHealthIndex());
			testName = healthIndex.getTestName();
			deviceName = healthIndex.getDeviceName();
			if(null!=healthIndex.getVoicequalityHealthIndex()){
				totalVQHealthIndex = totalVQHealthIndex + healthIndex.getVoicequalityHealthIndex();
				numberofVQFiles++;
			}
			if(null!=healthIndex.getVoiceConnectivityHealthIndex()){
				totalVCHealthIndex = totalVCHealthIndex + healthIndex.getVoiceConnectivityHealthIndex();
				numberofVCFiles++;
			}
			if(null!=healthIndex.getDataConnectivityHealthIndex()){
				totalDCHealthIndex = totalDCHealthIndex + healthIndex.getDataConnectivityHealthIndex();
				numberofDCFiles++;
			}
			if(null!=healthIndex.getLteHealthIndex()){
				totalLTEHealthIndex = totalLTEHealthIndex + healthIndex.getLteHealthIndex();
				numberofLTEFiles++;
			}
			if(null!=healthIndex.getVoipHealthIndex()){
				totalVoipEHealthIndex = totalVoipEHealthIndex + healthIndex.getVoipHealthIndex();
				numberofVoipFiles++;
			}
			if(null!=healthIndex.getVoiceDataHealthIndex()){
				totalVDEHealthIndex = totalVDEHealthIndex + healthIndex.getVoiceDataHealthIndex();
				numberofVDFiles++;
			}
			if(null!=healthIndex.getBrowserHealthIndex()){
				totalBrwsrEHealthIndex = totalBrwsrEHealthIndex + healthIndex.getBrowserHealthIndex();
				numberofBrwsrFiles++;
			}
			marketIdset.add(healthIndex.getMarktId());
			
			
			if(null!=healthIndex.getVqAvgValue() && !healthIndex.getVqAvgValue().equalsIgnoreCase("NaN")){
				vqAvg = vqAvg + "," + healthIndex.getVqAvgValue();
			}else{
				vqAvg = vqAvg + "," + "0.0";
			}
			if(null!=healthIndex.getVcAvgValue() && !healthIndex.getVcAvgValue().equalsIgnoreCase("NaN")){
				vcAvg = vcAvg + "," + healthIndex.getVcAvgValue();
			}else{
				vcAvg = vcAvg + "," + "0.0";
			}			
			if(null!=healthIndex.getDcAvgValue() && !healthIndex.getDcAvgValue().equalsIgnoreCase("NaN")){
				dcAvg = dcAvg + "," + healthIndex.getDcAvgValue();
			}else{
				dcAvg = dcAvg + "," + "0.0";
			}			
			if(null!=healthIndex.getVdAvgValue() && !healthIndex.getVdAvgValue().equalsIgnoreCase("NaN")){
				vdAvg = vdAvg + "," + healthIndex.getVdAvgValue();
			}else{
				vdAvg = vdAvg + "," + "0.0";
			}			
			if(null!=healthIndex.getDcLteAvgValue() && !healthIndex.getDcLteAvgValue().equalsIgnoreCase("NaN")){
				ltAvg = ltAvg + "," + healthIndex.getDcLteAvgValue();
			}else{
				ltAvg = ltAvg + "," + "0.0";
			}			
			if(null!=healthIndex.getBrowserAvgValue() && !healthIndex.getBrowserAvgValue().equalsIgnoreCase("NaN")){
				browserAvg = browserAvg + "," + healthIndex.getBrowserAvgValue();
			}else{
				browserAvg = browserAvg + "," + "0.0";
			}			
			if(null!=healthIndex.getVoipAvgValue() && !healthIndex.getVoipAvgValue().equalsIgnoreCase("NaN")){
				voipAvg = voipAvg + "," + healthIndex.getVoipAvgValue();
			}else{
				voipAvg = voipAvg + "," + "0.0";
			}			
			
		}
		Iterator<String> marketItr = marketIdset.iterator();
		while(marketItr.hasNext()){
			String marketName = marketItr.next();
			marketIds = marketIds+","+marketName;
		}
		System.out.println("marketIds------health--------------------------------"+marketIds);
		
		avgVQHealthIndex = totalVQHealthIndex/numberofVQFiles;
		avgVCHealthIndex = totalVCHealthIndex/numberofVCFiles;
		avgDCHealthIndex = totalDCHealthIndex/numberofDCFiles;
		avgLTEHealthIndex = totalLTEHealthIndex/numberofLTEFiles;
		avgVoipHealthIndex = totalVoipEHealthIndex/numberofVoipFiles;
		avgVDHealthIndex = totalVDEHealthIndex/numberofVDFiles;
		avgBrwsrHealthIndex = totalBrwsrEHealthIndex/numberofBrwsrFiles;
		
		avgHealthIndexTo.setVoicequalityHealthIndex(avgVQHealthIndex);
		avgHealthIndexTo.setVoiceConnectivityHealthIndex(avgVCHealthIndex);
		avgHealthIndexTo.setDataConnectivityHealthIndex(avgDCHealthIndex);
		avgHealthIndexTo.setLteHealthIndex(avgLTEHealthIndex);
		avgHealthIndexTo.setVoipHealthIndex(avgVoipHealthIndex);
		avgHealthIndexTo.setVoiceDataHealthIndex(avgVDHealthIndex);
		avgHealthIndexTo.setBrowserHealthIndex(avgBrwsrHealthIndex);
		avgHealthIndexTo.setMarketName(marketIds);
		avgHealthIndexTo.setMarktId(marketIds);
		avgHealthIndexTo.setTestName(testName);
		System.out.println("vqAvg:"+vqAvg+":vcAvg:"+vcAvg+":dcAvg:"+dcAvg+":vdAvg:"+vdAvg+":ltAvg:"+ltAvg+":browserAvg:"+browserAvg+":voipAvg:"+voipAvg);
		avgHealthIndexTo.setVqAvgValue(vqAvg);
		avgHealthIndexTo.setVcAvgValue(vcAvg);
		avgHealthIndexTo.setDcAvgValue(dcAvg);
		avgHealthIndexTo.setVdAvgValue(vdAvg);
		avgHealthIndexTo.setDcLteAvgValue(ltAvg);
		avgHealthIndexTo.setBrowserAvgValue(browserAvg);
		avgHealthIndexTo.setVoipAvgValue(voipAvg);
		avgHealthIndexTo.setTestName(testName);
		avgHealthIndexTo.setDeviceName(deviceName);
		return avgHealthIndexTo ;
	}
	public List<String> voiceConnectivityHealthIndex(List<Float> voiceConnectValueList){
		
		List<String> vcHealthIndexList = new ArrayList<String>();
		  float voiceConnectHealth = 0;
		  float callDropValue = 0;
		  System.out.println("voiceConnectValueList--------------"+voiceConnectValueList);
		  if(null!=voiceConnectValueList &&voiceConnectValueList.size()>2 ){
		   //THE CALL DROP VALUE INDEX IN THE LIST IS 2.THE LIST HAS DETAILS TOTAL CALLS,DROPPED CALLS,DCR(%)
		    callDropValue = voiceConnectValueList.get(2);
		  }
		  System.out.println("callDropValue--------"+callDropValue);
		  if(callDropValue<0.5){
			  voiceConnectHealth=50;
		  }
		  if((callDropValue==0.5)||(callDropValue<1.0)){
			  voiceConnectHealth=40;
		  }
		  if((callDropValue==1)||(callDropValue<2.0)){
			  voiceConnectHealth=30;
		  }
		  if((callDropValue==2)||(callDropValue<3.0)){
			  voiceConnectHealth=20;
		  }
		  if((callDropValue==3)||(callDropValue<5.0)){
			  voiceConnectHealth=10;
		  }
		  if(callDropValue>5){
			  voiceConnectHealth=0;
		  }
		  //System.out.println("voiceConnectValueList--------"+voiceConnectValueList);
		  vcHealthIndexList.add(String.valueOf(voiceConnectHealth));
		  //vcHealthIndexList.add(new Float(callDropValue));
		  vcHealthIndexList.add(String.valueOf(callDropValue));
		  return vcHealthIndexList;
		 }

	public List<String> dataConnectivityHealthIndex(List<Float> tcpDownloadSpeedList,List<Float> udpDownloadSpeedList, List<Float> dnsAvgLatencyList,String networktype) {
		float dataConnectivityHealth = 0;
		
		List<String> dataConnectivityHealthList = new ArrayList<String>();
		float tcpHealth=0;
		float udpHealth=0;
		float dnsHealth=0;
		float ftpHealth=20;
		float lteVS3gHealth = 0;
		
		float tcpAvgValue=0;
		float udpAvgValue=0;
		float dnsAvgValue=0;
		
		float dcAvgValue=0;
		//{UDP Dwn Capacity Average >=3.5 = 20points; UDP DWn Capacity Average =2-3.5 =10; UDP Dwn Capacity<2 =1}
		
		//{TCP Dwn Speed Average >=3.5 = 20points; TCP DWn Speed Average =2-3.5 =10; TCP Dwn speed Average<2 =1}
		
		//Average Latency <60ms =20;>60<70=10;>70=0
		
		HealthIndexTo healthIndex = new HealthIndexTo();
		if(networktype.equals("LTE")){
			System.out.println("inside Lte");
			if(null!=tcpDownloadSpeedList){
				tcpAvgValue=getAverageList(tcpDownloadSpeedList);
				if(tcpAvgValue>=4.5){
					tcpHealth=20;
				}
				if((tcpAvgValue>=3)||(tcpAvgValue<4.5)){
					tcpHealth=10;
				}
				if(tcpAvgValue<3){
					tcpHealth=1;
				}
			}
			if(null!=udpDownloadSpeedList){
				udpAvgValue=getAverageList(udpDownloadSpeedList);
				if(udpAvgValue>=4.5){
					udpHealth=20;
				}
				if((udpAvgValue>=2)||(udpAvgValue<4.5)){
					udpHealth=10;
				}
				if(udpAvgValue<2){
					udpHealth=1;
				}
				
			}
			if(null!=dnsAvgLatencyList){
				dnsAvgValue=getAverageList(dnsAvgLatencyList);
				if(dnsAvgValue<50){
					dnsHealth=20;
				}
				if((dnsAvgValue>=50)||(dnsAvgValue<70)){
					dnsHealth=10;
				}
				if(dnsAvgValue>=70){
					dnsHealth=0;
				}
			}
			
		}else{
			if(null!=tcpDownloadSpeedList){
				tcpAvgValue=getAverageList(tcpDownloadSpeedList);
				if(tcpAvgValue>=3.5){
					tcpHealth=20;
				}
				if((tcpAvgValue>=2)||(tcpAvgValue<3.5)){
					tcpHealth=10;
				}
				if(tcpAvgValue<2){
					tcpHealth=1;
				}
			}
			if(null!=udpDownloadSpeedList){
				 udpAvgValue=getAverageList(udpDownloadSpeedList);
				if(udpAvgValue>=3.5){
					udpHealth=20;
				}
				if((udpAvgValue>=2)||(udpAvgValue<3.5)){
					udpHealth=10;
				}
				if(udpAvgValue<2){
					udpHealth=1;
				}
				
			}
			if(null!=dnsAvgLatencyList){
				dnsAvgValue=getAverageList(dnsAvgLatencyList);
				if(dnsAvgValue<60){
					dnsHealth=20;
				}
				if((dnsAvgValue>=60)||(dnsAvgValue<70)){
					dnsHealth=10;
				}
				if(dnsAvgValue>=70){
					dnsHealth=0;
				}
			}
			
		}

		float lteVS3G = new HealthIndexDBHelper().getDC3GvsLTEIndex();
		if(lteVS3G == 100){
			lteVS3gHealth = 20;
		}
		else if(lteVS3G > 80 && lteVS3G<99){
			lteVS3gHealth = 15;
		}
		else if(lteVS3G>70&&lteVS3G<80){
			lteVS3gHealth = 10;
		}
		else if(lteVS3G<70){
			lteVS3gHealth = 0;
		}
		dcAvgValue=tcpAvgValue+udpAvgValue+dnsAvgValue+lteVS3G;
		dataConnectivityHealth= tcpHealth+udpHealth+dnsHealth+ftpHealth+lteVS3gHealth;
		
		dataConnectivityHealthList.add(String.valueOf(dataConnectivityHealth));
		dataConnectivityHealthList.add(String.valueOf(dcAvgValue));
		
		
		return dataConnectivityHealthList;
	}
	
/*	public float voipHealthIndex(List<Float> voipSipRegisterList,List<Float> voipDownstreamList, List<Float> voipSipbyeList,
			List<Float> voipSipInviteList) {
		float voipHealth = 0;
		float regHealth=0;
		float downHealth=0;
		float sipByeHealth=0;
		float sipInviteHealth=0;
		
		
		if(null!=voipDownstreamList){
			float downAvgValue=getAverageList(voipDownstreamList);
			if(downAvgValue<3){
				downHealth=20;
			}
			if(downAvgValue>=3){
				downHealth=10;
			}
		}
		if(null!=voipSipRegisterList){
			float regAvgValue=getAverageList(voipDownstreamList);
			if(regAvgValue<200){
				regHealth=20;
			}
			if((regAvgValue>=200)||(regAvgValue<250)){
				regHealth=10;
			}
			if(regAvgValue>250){
				regHealth=0;
			}
			
		}
		if(null!=voipSipbyeList){
			float byeAvgValue=getAverageList(voipSipbyeList);
			if(byeAvgValue<325){
				sipByeHealth=20;
			}
			if((byeAvgValue>=325)||(byeAvgValue<400)){
				sipByeHealth=10;
			}
			if(byeAvgValue>=400){
				sipByeHealth=0;
			}
		}
		if(null!=voipSipInviteList){
			float invAvgValue=getAverageList(voipSipInviteList);
			if(invAvgValue<160){
				sipInviteHealth=20;
			}
			if((invAvgValue>=160)||(invAvgValue<200)){
				sipInviteHealth=10;
			}
			if(invAvgValue>=200){
				sipInviteHealth=0;
			}
		}
		
		voipHealth= downHealth+regHealth+sipByeHealth+sipInviteHealth;
		
		return voipHealth;
	}*/
	
	public  List<String> voipHealthIndex (HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapForVoipDc){
		
		List<String> voipHealthIndexList = new ArrayList<String>();
			List<Float> voipSipInviteList= new ArrayList<Float>();
			List<Float> voipSipbyeList= new ArrayList<Float>();
			List<Float> voipSipRegisterList=new ArrayList<Float>();
			List<Float> voipDownstreamList = new ArrayList<Float>();
			
			float voipHealth = 0;
			float regHealth=0;
			float downHealth=0;
			float sipByeHealth=0;
			float sipInviteHealth=0;
			
			float invAvgValue=0;
			float byeAvgValue=0;
			float regAvgValue=0;
			float downAvgValue=0;
			float voipAvgValue = 0;
		
			if(null!=detailsDataMapForVoipDc){
				for (Entry<String, HashMap<String, HashMap<String, List<Float>>>> entry : detailsDataMapForVoipDc.entrySet()) {
				    //System.out.println("testName ====++== " + entry.getKey());
				    //String entryLte =entry.getKey().equalsIgnoreCase("LTE");
				    HashMap<String, HashMap<String, List<Float>>> test = entry.getValue();
				    if(entry.getKey().equalsIgnoreCase("LTE")){
				    	for (Entry<String, HashMap<String, List<Float>>> Kentry : test.entrySet()) {
					    	 //System.out.println("mrktId ====^^== " + Kentry.getKey() +"----------"+Kentry.getValue());
					    	 HashMap<String, List<Float>> listmap= Kentry.getValue();
					    	 for (Entry<String, List<Float>> valMaP : listmap.entrySet()) {
					    		 if(Kentry.getKey().equalsIgnoreCase("SIPInvite")){
					    			 voipSipInviteList= valMaP.getValue();
					    			 if(null!=voipSipInviteList){
					    					invAvgValue=getAverageList(voipSipInviteList);
					    					if(invAvgValue<220){
					    						sipInviteHealth=20;
					    					}
					    					if((invAvgValue>=220)||(invAvgValue<300)){
					    						sipInviteHealth=10;
					    					}
					    					if(invAvgValue>=300){
					    						sipInviteHealth=0;
					    					}
					    				}
					    			 
									}
					    		 
					    		 if(Kentry.getKey().equalsIgnoreCase("SIPBye")){
					    			 voipSipbyeList= valMaP.getValue();
					    			 if(null!=voipSipbyeList){
					    					byeAvgValue=getAverageList(voipSipbyeList);
					    					if(byeAvgValue<160){
					    						sipByeHealth=20;
					    					}
					    					if((byeAvgValue>=160)||(byeAvgValue<200)){
					    						sipByeHealth=10;
					    					}
					    					if(byeAvgValue>=200){
					    						sipByeHealth=0;
					    					}
					    				}
									}
					    		 if(Kentry.getKey().equalsIgnoreCase("SIPReg")){
					    			 voipSipRegisterList= valMaP.getValue();
					    			  regAvgValue=getAverageList(voipSipRegisterList);
					    				if(regAvgValue<140){
					    					regHealth=20;
					    				}
					    				if((regAvgValue>=140)||(regAvgValue<200)){
					    					regHealth=10;
					    				}
					    				if(regAvgValue>200){
					    					regHealth=0;
					    				}
									}
					    		 
								}
				    		}
				    }else{
				    	for (Entry<String, HashMap<String, List<Float>>> Kentry : test.entrySet()) {
					    	 //System.out.println("mrktId ====^^== " + Kentry.getKey() +"-------++---"+Kentry.getValue());
					    	 HashMap<String, List<Float>> listmap= Kentry.getValue();
					    	 for (Entry<String, List<Float>> valMaP : listmap.entrySet()) {
					    		 if(Kentry.getKey().equalsIgnoreCase("SIPInvite")){
					    			 voipSipInviteList= valMaP.getValue();
					    			 if(null!=voipSipInviteList){
					    					invAvgValue=getAverageList(voipSipInviteList);
					    					if(invAvgValue<325){
					    						sipInviteHealth=20;
					    					}
					    					if((invAvgValue>=325)||(invAvgValue<400)){
					    						sipInviteHealth=10;
					    					}
					    					if(invAvgValue>=400){
					    						sipInviteHealth=0;
					    					}
					    				}
					    			 
									}
					    		 
					    		 if(Kentry.getKey().equalsIgnoreCase("SIPBye")){
					    			 voipSipbyeList= valMaP.getValue();
					    			 if(null!=voipSipbyeList){
					    					byeAvgValue=0;
					    					sipByeHealth=0;
					    				}
									}
					    		 if(Kentry.getKey().equalsIgnoreCase("SIPReg")){
					    			 voipSipRegisterList= valMaP.getValue();
					    			 regAvgValue=getAverageList(voipSipRegisterList);
					    				if(regAvgValue<200){
					    					regHealth=20;
					    				}
					    				if((regAvgValue>=200)||(regAvgValue<250)){
					    					regHealth=10;
					    				}
					    				if(regAvgValue>250){
					    					regHealth=0;
					    				}
									}
					    		 if(Kentry.getKey().equalsIgnoreCase("DownstreamMaxJitter")){
					    			 voipDownstreamList= valMaP.getValue();
					    			  downAvgValue=getAverageList(voipDownstreamList);
					    				if(downAvgValue<3){
					    					downHealth=20;
					    				}
					    				if(downAvgValue>=3){
					    					downHealth=10;
					    				}
									}
					    		 
								}
				    		}
				    	}
				}
			}

		voipAvgValue=invAvgValue+byeAvgValue+regAvgValue+downAvgValue;
		voipHealth=downHealth+regHealth+sipByeHealth+sipInviteHealth;
		 //System.out.println("voipHealth----voipHealth----"+voipHealth);
		voipHealthIndexList.add(String.valueOf(voipHealth));
		voipHealthIndexList.add(String.valueOf(voipAvgValue));
		return voipHealthIndexList;
	}

	public List<String> voiceBrowserHealthJson(List<BrowserBean> browserBeanList) {
		// TODO Auto-generated method stub
		
		List<String> voiceBrowserHealthIndexList = new ArrayList<String>();
		float browseHealth=0;
		float browseAvgHealth=0;
		float browserlistHealth=0;
		float browserlistLtehealth=0;
		float browserAvgValue=0;
		float browserLteAvgValue=0;
		List <Float> browserValueList= new ArrayList<Float>();
		List <Float> browserValueListLte= new ArrayList<Float>();
		browserValueList= getAverageListForBrowser(browserBeanList);
		if(null!=browserBeanList){
			for (int i = 0; i < browserValueList.size(); i++) {
				 browserAvgValue=browserValueList.get(0);
				if(browserAvgValue<700){
					browserlistHealth=50;
				}
				if((browserAvgValue>=700)||(browserAvgValue<800)){
					browserlistHealth=40;
				}
				if(browserAvgValue>=800){
					browserlistHealth=0;
				}
				
			}
			
		}
		browseAvgHealth=browserAvgValue+browserLteAvgValue;
		browseHealth= browserlistLtehealth+browserlistHealth;
		//System.out.println("browserlistLtehealth----------"+browserlistLtehealth);
		//System.out.println("browserlistHealth----------"+browserlistHealth);
		//System.out.println("browseHealth----------"+browseHealth);
		voiceBrowserHealthIndexList.add(String.valueOf(browseHealth));
		voiceBrowserHealthIndexList.add(String.valueOf(browseAvgHealth));
		return voiceBrowserHealthIndexList;
	}
	 
	public Float getAverageList(List<Float> list){
		   Float averagevalue = new Float(0);
		   Float listSum = new Float(0);
		   if(null!=list){
		    for(int i=0;i<list.size();i++){
		    	if(null!=list.get(i)){
		    		listSum = listSum+list.get(i);
		    	}
		    }
		    averagevalue = listSum/list.size();
		   }
		   else{
		    averagevalue = null;
		   }
		   return averagevalue;
		  }
	public List<Float> getAverageListForBrowser(List<BrowserBean> list){
		   Float avgReadlatency = new Float(0);
		   Float avgConctlatency = new Float(0);
		   Float readLatency = new Float(0);
		   Float connectLatency = new Float(0);
		   List<Float> brwsrList = new ArrayList<Float>();
		   if(null!=list){
			   //System.out.println("list.size()================="+list.size());
		    for(int i=0;i<list.size();i++){
		    	if(null!=list.get(i)){
		    		if(null!=list.get(i).getHttpAvgReadRate()&&null!=list.get(i).getHttpAvgConnct()){
		    			readLatency = readLatency+Float.parseFloat(list.get(i).getHttpAvgReadRate());
			    		connectLatency= connectLatency+Float.parseFloat(list.get(i).getHttpAvgConnct());
		    		}
		    		
		    	}
		    }
		    avgReadlatency = readLatency/list.size();
		    avgConctlatency = connectLatency/list.size();
		   }
		   else{
			   avgReadlatency = null;
			   avgConctlatency = null;
		   }
		   brwsrList.add(0, avgReadlatency);
		   brwsrList.add(1, avgConctlatency);
		   
		   return brwsrList;
		  }


	public List<String> voiceDataHealthIndex(String masterMarketId,String masterDeviceName, String masterTesName,List<Float> tcpDriveDownloadSpeedList) {
		// TODO Auto-generated method stub
		List<String> voiceDataHealthIndexList = new ArrayList<String>();
		float voipDataAvgValue = 0;
		float voipDataHealth = 0;
		float tcpDriveHealth=0;
		float vdhealth=0;
		float vdAvgValue=0;
		float tcpAvgValue=0;
		List<Float>FinalList=new ArrayList<Float>();
		FinalList= new Voice_DataDaoImpl().getVoice_DataKPI(masterTesName, masterMarketId);
		//vdAvgValue=
			 for(int i = 0; i < FinalList.size(); i++) {
		            System.out.println(FinalList.get(i));
		            vdAvgValue=((FinalList.get(1)*100)/FinalList.get(0));
		        }
		//System.out.println("FinalList------**----"+FinalList);
		//System.out.println("vdAvgValue------**----"+vdAvgValue);
		
		if(null!=tcpDriveDownloadSpeedList){
			tcpAvgValue=getAverageList(tcpDriveDownloadSpeedList);
			System.out.println("tcpAvgValue------**----"+tcpAvgValue);
			if(tcpAvgValue>=2.5){
				tcpDriveHealth=50;
			}
			if((tcpAvgValue<=2)||(tcpAvgValue<2.5)){
				tcpDriveHealth=40;
			}
			if((tcpAvgValue<=1)||(tcpAvgValue<2)){
				tcpDriveHealth=30;
			}
			if(tcpAvgValue<1){
				tcpDriveHealth=0;
			}
		}
		if(null!=FinalList){
			if(vdAvgValue<=2){
				vdhealth=50;
			}
			if((vdAvgValue>2)||(vdAvgValue<=3)){
				vdhealth=40;
			}
			if((vdAvgValue>3)||(vdAvgValue<=4)){
				vdhealth=30;
			}
			if(vdAvgValue>5){
				vdhealth=0;
			}
		}
		voipDataAvgValue=tcpAvgValue+vdAvgValue;
		 voipDataHealth= tcpDriveHealth+vdhealth;
		// System.out.println("tcpDriveHealth-----**------"+tcpDriveHealth);
		// System.out.println("vdhealth-----**------"+vdhealth);
		//System.out.println("voipDataHealth-----**------"+voipDataHealth);
		 voiceDataHealthIndexList.add(String.valueOf(voipDataHealth));
		 voiceDataHealthIndexList.add(String.valueOf(voipDataAvgValue));
		 
		return voiceDataHealthIndexList;
	}



}
