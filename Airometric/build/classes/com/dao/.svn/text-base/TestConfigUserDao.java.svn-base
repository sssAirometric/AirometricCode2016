package com.dao;

import java.util.List;

import com.to.TestConfig;


public interface TestConfigUserDao {
	
	public void assignTestConfigToUser(String selTestConfigId,String selOperatorID,String selUserID,String seldeviceID,String userId);
	public boolean checkTestConfigalreadyAssigned(String selTestConfigId,
			String selOperatorID, String selUserID,String selImei);
	public boolean checkTestConfigAssignedToOperator(String selTestConfigId,
			String selOperatorID);
	public List<TestConfig>assignListTestConfig();
	public List<TestConfig> assignListTestConfigOperator(String operator,String userId);
}
