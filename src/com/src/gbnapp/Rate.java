package com.src.gbnapp;

/* It stores internally rate as double in order to perform lossless arithmetic operations.
Once we get a result and we want to show or retrieve it, it is used BigDecimal format 
rounded by ROUND_HALF_EVEN method. */

public class Rate {
	
	private String to;
	private double rate;
	
	public Rate(String to, double rate) {
		this.to = to;
		this.rate = rate;
	}
	
	public String getTo() {
		return this.to;
	}
	
	public double getRate() {
		return this.rate;
	}
		
	public void setTo(String to) {
		this.to = to;
	}
	
	public void setRate(double rate) {
		this.rate = rate;
	}
}
