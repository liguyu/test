package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.utils.Util;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.BigAddressModel;

public class BigAddressActivity extends Activity {
	BigAddressModel model;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		model = new BigAddressModel(this);
		Binder.setAndBindContentView(this, R.layout.bigaddress, model);
		super.onCreate(savedInstanceState);
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		if(Util.DBExists(this))
//			model.total();
//	}
	
	
}
