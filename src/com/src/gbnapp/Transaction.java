package com.src.gbnapp;

/* It stores internally rate as double in order to perform lossless arithmetic operations.
   Once we get a result and we want to show or retrieve it, it is used BigDecimal format 
   rounded by ROUND_HALF_EVEN method. */

public class Transaction {
	
	private double amount;
	private String currency;
	
	public Transaction(double amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}
	
	public double getAmount() {
		return this.amount;
	}
	
	public String getCurrency() {
		return this.currency;
	}
		
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
