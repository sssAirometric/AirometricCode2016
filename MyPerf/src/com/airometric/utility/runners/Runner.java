package com.airometric.utility.runners;

import java.io.Serializable;

public abstract class Runner implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected abstract void cancelTest();
}
