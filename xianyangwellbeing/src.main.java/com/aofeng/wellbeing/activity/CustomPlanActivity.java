package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.utils.Util;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.CustomPlanModel;

public class CustomPlanActivity extends Activity {
	CustomPlanModel model;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		model = new CustomPlanModel(this);
		Binder.setAndBindContentView(this, R.layout.custom_plan, model);
		super.onCreate(savedInstanceState);
	}
}
