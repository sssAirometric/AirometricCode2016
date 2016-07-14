package com.dao;

public interface TestConfigParamDao {
	public void addTestConfigParam(Object obj,Integer testConfigID,Integer testTypeID,String userId);
	public void updateTestConfigParam(Object obj,Integer testConfigID,Integer testTypeID,String userId);
}
