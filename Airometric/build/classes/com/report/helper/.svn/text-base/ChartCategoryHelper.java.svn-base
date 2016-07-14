package com.report.helper;

import net.sf.json.JSONArray;

public class ChartCategoryHelper {

	public net.sf.json.JSONArray getcallSetupCategory(){
		net.sf.json.JSONArray jsoArray = new net.sf.json.JSONArray();
		net.sf.json.JSONArray masterJsonArray = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callSetupJson = new net.sf.json.JSONObject();
		callSetupJson.put("name", "3G");
		jsoArray.add("MO");
		jsoArray.add("MT");
		callSetupJson.put("categories", jsoArray);
		masterJsonArray.add(callSetupJson);
		callSetupJson.put("name", "LTE");
		callSetupJson.put("categories", jsoArray);
		masterJsonArray.add(callSetupJson);
		return masterJsonArray;
	}
	
	public net.sf.json.JSONArray getcallSetupCategoryLte(){
		net.sf.json.JSONArray jsoArray = new net.sf.json.JSONArray();
		net.sf.json.JSONArray masterJsonArray = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callSetupJson = new net.sf.json.JSONObject();
		callSetupJson.put("name", "3G");
		jsoArray.add("MO");
		jsoArray.add("MT");
		callSetupJson.put("categories", jsoArray);
		masterJsonArray.add(callSetupJson);
		callSetupJson.put("name", "LTE");
		callSetupJson.put("categories", jsoArray);
		masterJsonArray.add(callSetupJson);
		return jsoArray;
	}
	
	public static void main(String[] args) {
		//System.out.println(new ChartCategoryHelper().getcallSetupCategory());
	}
}
