package com.dao;

import java.util.List;
import java.util.Map;

import com.to.MarketBean;
import com.to.MarketPlaceBean;


public interface MarketPlaceDao {

	List<MarketPlaceBean> getMarketPlaceList();
	String getMarketDetail(String marketId);
	public List<MarketPlaceBean> getMarketList();
	public List<MarketPlaceBean> getCountryList();
	public Map<String, String> getCountryMap();
	public Map<String, String> getStateIdsForCountry(String countryId);
	public Map<String, String> getCityIdsForState(String stateId);
	public boolean checkMarketExists(String marketName,String countryId);
	public String addMarket(String userId,String marketName,String countryId,String stateId,String cityId);
	public List<MarketPlaceBean> getMarketOperatorList();
	public Map<String, String> getMarketPlaceMap();
	public Map<String, String> getUploadMarketPlaceMap(String role,String userIdLogged);
	public Map<String, String> getOperatorMap();
	public String assignMarket(String userId,String marketId,String operatorId);
	public Map<String, String> getMarketPlaceMapForCountry(String countryId);
	public String  getOtherMarketId(String countryId);
	public Map<String,String> getMarketForUser(String userId);
	public List<Integer> getMarketIDListForUser(String userId);

}
