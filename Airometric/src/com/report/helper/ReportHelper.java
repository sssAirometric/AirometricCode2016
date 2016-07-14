package com.report.helper;

import java.util.List;

public class ReportHelper {

	public Float getAverageList(List<Float> list) {
		Float averagevalue = new Float(0);
		Float listSum = new Float(0);
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				listSum = listSum + list.get(i);
			}
			averagevalue = listSum / list.size();
		} else {
			averagevalue = null;
		}
		return averagevalue;
	}
}
