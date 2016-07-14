package com.to;

import java.io.Serializable;

public class MarketInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6096843941758118584L;
	private String  market_id;
	private String  market_name;
	private String  country_id;
	private String  state_id;
	private String  city_id;
	public String getMarket_id() {
		return market_id;
	}
	public void setMarket_id(String marketId) {
		market_id = marketId;
	}
	public String getMarket_name() {
		return market_name;
	}
	public void setMarket_name(String marketName) {
		market_name = marketName;
	}
	public String getCountry_id() {
		return country_id;
	}
	public void setCountry_id(String countryId) {
		country_id = countryId;
	}
	public String getState_id() {
		return state_id;
	}
	public void setState_id(String stateId) {
		state_id = stateId;
	}
	public String getCity_id() {
		return city_id;
	}
	public void setCity_id(String cityId) {
		city_id = cityId;
	}
	
}