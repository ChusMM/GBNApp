package com.src.gbnapp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Transaction> {
	
	private final Context context;
    private final ArrayList<Transaction> transactions;
    
    public ProductAdapter(Context context, ArrayList<Transaction> transactions) {
    	 
        super(context, R.layout.row_product, transactions);

        this.context = context;
        this.transactions = transactions;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	View rowView = inflater.inflate(R.layout.row_product, parent, false);
    	
    	TextView txCurr = (TextView) rowView.findViewById(R.id.txCurr);
    	TextView txAmount = (TextView) rowView.findViewById(R.id.txAmount);
    	
    	txCurr.setText(transactions.get(position).getCurrency());
    	txAmount.setText(String.valueOf(transactions.get(position).getAmount()));
    	
    	return rowView;
    }
}
