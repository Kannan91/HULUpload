package com.unilever.unilever.controller;

public class SelectionDetails {
	
	String Location;
	String Category;
	double Min_Capping;
	int Lead_Time_variability;
	String Bias_Correction_factor;
	String SDFE_Capping;
	int SDFE_Capping_Perc;
	String Target_ServiceLevel;
	double Category_ServiceLevel;
	int x_value;
	
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		Category = category;
	}
	public double getMin_Capping() {
		return Min_Capping;
	}
	public void setMin_Capping(double min_Capping) {
		Min_Capping = min_Capping;
	}
	public int getLead_Time_variability() {
		return Lead_Time_variability;
	}
	public void setLead_Time_variability(int lead_Time_variability) {
		Lead_Time_variability = lead_Time_variability;
	}
	public String getBias_Correction_factor() {
		return Bias_Correction_factor;
	}
	public void setBias_Correction_factor(String bias_Correction_factor) {
		Bias_Correction_factor = bias_Correction_factor;
	}
	public String getSDFE_Capping() {
		return SDFE_Capping;
	}
	public void setSDFE_Capping(String sDFE_Capping) {
		SDFE_Capping = sDFE_Capping;
	}
	public int getSDFE_Capping_Perc() {
		return SDFE_Capping_Perc;
	}
	public void setSDFE_Capping_Perc(int sDFE_Capping_Perc) {
		SDFE_Capping_Perc = sDFE_Capping_Perc;
	}
	public String getTarget_ServiceLevel() {
		return Target_ServiceLevel;
	}
	public void setTarget_ServiceLevel(String target_ServiceLevel) {
		Target_ServiceLevel = target_ServiceLevel;
	}
	public double getCategory_ServiceLevel() {
		return Category_ServiceLevel;
	}
	public void setCategory_ServiceLevel(double category_ServiceLevel) {
		Category_ServiceLevel = category_ServiceLevel;
	}
	public int getX_value() {
		return x_value;
	}
	public void setX_value(int x_value) {
		this.x_value = x_value;
	}

}
