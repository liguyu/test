package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;

import com.aofeng.utils.Util;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.MySecurityModel;

import android.app.Activity;
import android.os.Bundle;

public class MySecurityActivity extends Activity{
	MySecurityModel model;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new MySecurityModel(this);
		Binder.setAndBindContentView(this, R.layout.mysecurity, model);		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Util.DBExists(this))
			model.listBySelection();
	}

	
}