package com.report.dao;

import java.util.List;

public interface KpiDao {
    public List<Double> getLongitude_Lattitude(String cityName); 
    public List<String> getGeoLocationParameters(String device,String testname,String market);
    public List<String>getGeoLocationParametersForMarket(String market,String testName);
}
