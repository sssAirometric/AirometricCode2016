package com.util;
import java.util.ArrayList;
import java.util.List;

import au.id.davidcox.utils.latlong.Converter;

public class MapUtil {
	public static List<String> getlatitudeLangitude(String latiLongStr){
		List<String> list = new ArrayList<String>();
		List<List<String>> mapList = getMapFormattedList(latiLongStr);
		for(int a=0;a<mapList.size();a++){
			String dir = mapList.get(a).get(0);
			String deg = mapList.get(a).get(1);
			String min = mapList.get(a).get(2);
			String sec = mapList.get(a).get(3);
			Converter sampleconversion1 = new Converter();
			sampleconversion1.setDegrees(deg);
			sampleconversion1.setMinutes(min);
			sampleconversion1.setSeconds(sec);
			sampleconversion1.setDirection(dir);
			list.add(sampleconversion1.toDecimalDegrees());
		}
		return list;
	}
	public static List<List<String>> getMapFormattedList(String latiLongStr){
		List<List<String>> mapList = new ArrayList<List<String>>();
		String[] latiLongStrArray = latiLongStr.split(" ");
		if(latiLongStr.contains("?")){
			for(int i=0;i<latiLongStrArray.length;i++){
				List<String> list = formatStr(latiLongStrArray[i]);
				mapList.add(list);
			}
		}else if(latiLongStr.contains("º")){
			latiLongStr = latiLongStr.replace("º", "?");
			String[] latiLongStrAray = latiLongStr.split(" ");
			for(int i=0;i<latiLongStrAray.length;i++){
				List<String> list = formatStr(latiLongStrAray[i]);
				mapList.add(list);
			}
		}
		return mapList;
	}
	public static List<String> formatStr(String latString){
		List<String> list = new ArrayList<String>();
		list.add(latString.substring(0,1));
		list.add(latString.substring(1,latString.indexOf("?")));
		list.add(latString.substring(latString.indexOf("?")+1,latString.indexOf("'")));
		list.add(latString.substring(latString.indexOf("'")+1,latString.lastIndexOf("'")-1));
		return list;
	}
	public static void main(String[] args) {
		 getlatitudeLangitude("N39?08'36'' W077?12'57''");
	}
}
