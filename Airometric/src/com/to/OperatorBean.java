package com.to;

import java.io.Serializable;

public class OperatorBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String operatorID;
	private String operatorName;
	
	public String getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
