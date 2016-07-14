package com.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import com.dao.UserDao;
import com.dao.impl.MarketPlaceDaoImpl;
import com.dao.impl.OperatorDaoImpl;
import com.dao.impl.UserDaoImpl;
import com.validator.TestCaseValidator;
import com.dao.MarketPlaceDao;
/*
 * 
 */
public class MarketBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int marktId;
	private String marketName;
	
	private List<UserMarketInfo> userMarketInfos=new ArrayList<UserMarketInfo>();
	
	
	public List<UserMarketInfo> getUserMarketInfos() {
		return userMarketInfos;
	}
	public void setUserMarketInfos(List<UserMarketInfo> userMarketInfos) {
		this.userMarketInfos = userMarketInfos;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public int getMarktId() {
		return marktId;
	}
	public void setMarktId(int marktId) {
		this.marktId = marktId;
	}
	
	
	
}
