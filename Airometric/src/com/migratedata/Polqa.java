package com.migratedata;

import java.util.HashMap;
import java.util.Map;

public class Polqa {

	public static Map<String, String> mappingPolqaColumns = new HashMap<String, String>();
	public static void mappCols(){
		mappingPolqaColumns.put("VQT_Timestamp", "VQuad_Timestamp");
		mappingPolqaColumns.put("VQuad_Timestamp", "Call_Timestamp");
		mappingPolqaColumns.put("GPS_Position", "GPS_Position");
		mappingPolqaColumns.put("GPS_Lat", "GEOLOCATION_LATITUDE");
		mappingPolqaColumns.put("GPS_Long", "GEOLOCATION_LANGITUDE");
		mappingPolqaColumns.put("VQuad_CallID", "VQuad_CallID");
		mappingPolqaColumns.put("VQuad_Location", "VQuad_Location");
		mappingPolqaColumns.put("VQuad_PhoneID", "VQuad_PhoneID");
//		mappingPolqaColumns.put("Degraded", "Degraded");
		mappingPolqaColumns.put("Deg_File_Name", "Deg_File_Name");
		mappingPolqaColumns.put("POLQA_Score", "POLQA_Score");
		mappingPolqaColumns.put("Pass_Or_Fail", "Pass_Or_Fail");
		mappingPolqaColumns.put("Jitter_Min", "Jitter_Min");
		mappingPolqaColumns.put("Jitter_Max", "Jitter_Max");
		mappingPolqaColumns.put("Jitter_Ave", "Jitter_Ave");
		mappingPolqaColumns.put("EModel_Polqa", "EModel_Polqa");
		mappingPolqaColumns.put("Speech_Level_Diff", "Speech_Level_Diff");
		mappingPolqaColumns.put("SNR_Diff", "SNR_Diff");
	}
}
