package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.addressModel;

public class addressActivity extends Activity {
	addressModel model;
	@Override
	public void onCreate(Bundle savedInstanceState){
		model = new addressModel(this);
		super.onCreate(savedInstanceState);
		Binder.setAndBindContentView(this, R.layout.bigaddress, model);
	}
}
