package com.src.gbnapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.src.gbnapp.MAIN";
	
	static final String RATES_URL = "http://quiet-stone-2094.herokuapp.com/rates.xml";
	static final String TRANS_URL = "http://quiet-stone-2094.herokuapp.com/transactions.xml";
	
	static final String KEY_RATES  = "rate";
	static final String ATTR_FROM  = "from";
	static final String ATTR_TO    = "to";
	static final String ATTR_RATE  = "rate";
	
	static final String KEY_TRANS     = "transaction";
	static final String ATTR_SKU      = "sku";
	static final String ATTR_AMOUNT   = "amount";
	static final String ATTR_CURRENCY = "currency";
	
	private ListView listView;
	private ArrayList<String> currencies;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.listView = (ListView)findViewById(R.id.listView);
        this.load();
    }
    
    @Override
	public void onResume() {
    	super.onResume();
    	this.load();
    }
    
    private void load() {
    	
    	this.listView.setEnabled(false);
    	this.listView.setVisibility(View.GONE);
    	this.currencies = new ArrayList<String>();
        
        // Every time that we refresh, web service data changes randomly
        AppDataSet.getInstance().clean();
                
        DownloadRatesXMLFile ratesAsyncTask = new DownloadRatesXMLFile();
        ratesAsyncTask.execute(new String[] {RATES_URL});
        
        DownloadTransXMLFile transAsyncTask = new DownloadTransXMLFile();
        transAsyncTask.execute(new String[] {TRANS_URL}); 
    }
    
    private class DownloadRatesXMLFile extends AsyncTask<String, Void, String> {    	
    	@Override
        protected String doInBackground(String ... urls) {
    		String xml = "";
    		
    		XMLParser parser = new XMLParser();
    		for (String url : urls) {
    			xml = parser.getXmlFromUrl(url); // getting XML
    		}
    		return xml;
    	}
    	
    	@Override
        protected void onPostExecute(String result) { 
    		parseRates(result);
    	}
    }
    
    private class DownloadTransXMLFile extends AsyncTask<String, Void, String> {    	
    	@Override
        protected String doInBackground(String ... urls) {
    		String xml = "";
    		
    		XMLParser parser = new XMLParser();
    		for (String url : urls) {
    			xml = parser.getXmlFromUrl(url); // getting XML
    		}
    		return xml;
    	}
    	
    	@Override
        protected void onPostExecute(String result) { 
    		parseTrans(result);
    	}
    }
        
    private void parseRates(String xml) {
    	
		XMLParser parser = new XMLParser();
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nodelist = doc.getElementsByTagName(KEY_RATES);
    
		for (int i = 0; i < nodelist.getLength(); i++) {
    	
			Element e = (Element)nodelist.item(i);
    			
			String from = e.getAttribute(ATTR_FROM);
			String to = e.getAttribute(ATTR_TO); 
			double rate = Double.parseDouble(e.getAttribute(ATTR_RATE));
			
			Rate r = new Rate(to, rate);
			
			if(!AppDataSet.getInstance().getRatesMap().containsKey(from)) {
				
				ArrayList<Rate> rates = new ArrayList<Rate>();
				rates.add(r);
				AppDataSet.getInstance().getRatesMap().put(from, rates);
				this.currencies.add(from);
			}
			else {
				AppDataSet.getInstance().getRatesMap().get(from).add(r);
			}
			
		}
		this.addMissedConversions();
	}
    
    private void parseTrans(String xml) {
		
    	XMLParser parser = new XMLParser();
    	
    	Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nodelist = doc.getElementsByTagName(KEY_TRANS);
    
		for (int i = 0; i < nodelist.getLength(); i++) {
    	
			Element e = (Element)nodelist.item(i);
    			
			String sku = e.getAttribute(ATTR_SKU);
			double amount = Double.parseDouble(e.getAttribute(ATTR_AMOUNT)); 
			String currency = e.getAttribute(ATTR_CURRENCY);
			
			Transaction t = new Transaction(amount, currency);
			
			if(!AppDataSet.getInstance().getTransMap().containsKey(sku)) {
				
				ArrayList<Transaction> transactions = new ArrayList<Transaction>();
				transactions.add(t);
				AppDataSet.getInstance().getTransMap().put(sku, transactions);
			}
			else {
				AppDataSet.getInstance().getTransMap().get(sku).add(t);
			}
			
		}
		this.fillProductsListView();	
    }
    
    private void fillProductsListView() {
    	
    	Set<String> keys = AppDataSet.getInstance().getTransMap().keySet();
    	String[] products = keys.toArray(new String[0]);
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.single_product, products);
	    
		this.listView.setFastScrollEnabled(true);
		this.listView.setAdapter(arrayAdapter);
		this.setListViewListener();
		this.listView.setVisibility(View.VISIBLE);
		this.listView.setEnabled(true);
    }
    
    private void setListViewListener() {
		
		this.listView.setOnItemClickListener(new OnItemClickListener () {
			@Override
	        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
	        	
	        	String selectedProduct = parent.getItemAtPosition(pos).toString();
	        	startProductActivity(selectedProduct);
	        }
		});
	}
    
    private void startProductActivity(String product) {
    	
    	Intent intent = new Intent(this, ProductActivity.class);
		intent.putExtra(EXTRA_MESSAGE, product);
    	startActivity(intent);
    	super.onPause();
    }
    
    private void addMissedConversions() {
    	
    	HashMap<String, Double> eurConvMap = AppDataSet.getInstance().getEuroMap();
    	
    	for(int i = 0; i < this.currencies.size(); i++) {
    		
    		if(!currencies.get(i).equals("EUR")) {
    			eurConvMap.put(currencies.get(i), inferEuroConversion(currencies.get(i)));
    		}
    	}
    	
    	for(int i = 0; i < this.currencies.size(); i++) {
    		
    		if(!currencies.get(i).equals("EUR")) {
    			String key = currencies.get(i);
    			
    			if(!eurConvMap.containsKey(key) || eurConvMap.get(key) == 0) {
    				double r = searchInverseEurRate(currencies.get(i));
    				eurConvMap.put(currencies.get(i), r);
    			}
    		}
    	}
    	
    	//HashMap<String, ArrayList<Rate>> rm = AppDataSet.getInstance().getRatesMap();
    	//rm.get("USD");
    	//eurConvMap.get("USD");
    }
        
    private double inferEuroConversion(String currency) {
    	
    	double value;
    	ArrayList<Rate> childRates = getChilds(currency);
    	
    	if((value = baseCase(childRates)) == -1.0) {
    	
    		for(int i = 0; i < childRates.size(); i++) {
    		
    			double inference = childRates.get(i).getRate() * inferEuroConversion(childRates.get(i).getTo());
    			if(inference != 0) {
    				return inference;
    			}
    		}
    		return 0;
    	} 
    	else {
    		return value;
    	}
    }

    private ArrayList<Rate> getChilds(String key) {
    	return AppDataSet.getInstance().getRatesMap().get(key);
    }
    
    private double baseCase(ArrayList<Rate> childRates) {
    	
    	for(int i = 0; i < childRates.size(); i++) {
    		
    		if(hasEuroConversion(childRates.get(i))) {
    			return getEuroRate(childRates.get(i));
    		}
    	}
    	return -1;
    }
    
    private boolean hasEuroConversion(Rate r) {
    	
    	if(r.getTo().equals("EUR")) {
    			return true;
    	}
    	else {
    		return false;
    	}
    }
    
    private double getEuroRate(Rate r) {
    	
    	if(r.getTo().equals("EUR")) {
			return r.getRate();
		} 
    	else {
			return -1;
		}
    }
    
    private double searchInverseEurRate(String currency) {
    	
    	ArrayList<Rate> eurRates = AppDataSet.getInstance().getRatesMap().get("EUR");
    	
    	for(int i = 0; i < eurRates.size(); i++) {
    		
    		if(eurRates.get(i).getTo().equals(currency)) {
    			
    			return 1 / eurRates.get(i).getRate();
    		}
    	}
    	return 0;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { } 
		else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { }
    }
}
