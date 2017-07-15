package com.src.gbnapp;

import java.util.ArrayList;
import java.util.HashMap;

public class AppDataSet {
	
	private static AppDataSet instance = null;
	private HashMap<String, ArrayList<Rate>> rates;
	private HashMap<String, ArrayList<Transaction>> transMap;
	private HashMap<String, Double> euroConverter;
	
	private AppDataSet () {
        this.clean();
	}
	
	public synchronized static AppDataSet getInstance() {
		
		if(instance == null) {
			instance = new AppDataSet();
		}
		return instance;
	}
	
	public HashMap<String, ArrayList<Rate>> getRatesMap() {
		return this.rates;
	}
	
	public HashMap<String, ArrayList<Transaction>> getTransMap() {
		return this.transMap;
	}
	
	public HashMap<String, Double> getEuroMap() {
		return this.euroConverter;
	}
	
	public void clean() {
		this.rates = new HashMap<String, ArrayList<Rate>>();
        this.transMap = new HashMap<String, ArrayList<Transaction>>();
        this.euroConverter = new HashMap<String, Double>();
	}
}
