package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.PurchaseHistoryModel;

public class PurchaseHistoryActivity extends Activity{
	private String CARD_ID;
	PurchaseHistoryModel model;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		model = new PurchaseHistoryModel(this);
		model.COUNT.set(bundle.getString("COUNT"));
		model.SUM.set(bundle.getString("SUM"));
		CARD_ID=bundle.getString("CARD_ID");
		Binder.setAndBindContentView(this, R.layout.purchase_history, model);
	}

	@Override
	protected void onResume() {
		super.onResume();
		model.listPurchases(CARD_ID);
	}
	
	
}
