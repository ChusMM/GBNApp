package com.src.gbnapp;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

public class ProductActivity extends Activity {

	private TextView txProduct;
	private TextView txSum;
	private ListView listView;
	private String product;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		this.txProduct = (TextView)findViewById(R.id.txProduct);
		this.txSum = (TextView)findViewById(R.id.txSum);
		this.listView = (ListView)findViewById(R.id.listViewProd);
		
		Intent intent = getIntent();
		this.product = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	
		this.txProduct.setText(this.product);
		ArrayList<Transaction> trans = AppDataSet.getInstance().getTransMap().get(product);
	
		ProductAdapter prodAd = new ProductAdapter(this, trans);
		this.listView.setFastScrollEnabled(true);
		listView.setAdapter(prodAd);
		
		txSum.setText(this.sum(trans).toString());
	}
	
	private BigDecimal sum(ArrayList<Transaction> trans) {
		
		double sum = 0.0;
		HashMap<String, Double> eurConv = AppDataSet.getInstance().getEuroMap();
		
		for(int i = 0; i < trans.size(); i++) {
			
			if(trans.get(i).getCurrency().equals("EUR")) {
				sum = sum + trans.get(i).getAmount();
			}
			else if(trans.get(i).getCurrency().equals("USD")) {;
				
				sum = sum + (trans.get(i).getAmount() * eurConv.get("USD"));
			}
			else if(trans.get(i).getCurrency().equals("CAD")) {
				sum = sum + (trans.get(i).getAmount() * eurConv.get("CAD"));
			}
			else if(trans.get(i).getCurrency().equals("AUD")) {
				sum = sum + (trans.get(i).getAmount() * eurConv.get("AUD"));;
			}
			else {
				throw new Error("Invalid currency");
			}
		}
		BigDecimal rounded = new BigDecimal(sum);
		return rounded.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}
	
	public void goBack(View view) {
		this.back();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	this.back();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private void back() {
		ProductActivity.this.finish();
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { } 
		else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { }
    }
}
