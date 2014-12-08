package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.DetailAddressModel;
import com.aofeng.wellbeing.modelview.NoticeModel;

public class NoticeActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		NoticeModel model = new NoticeModel(this);
		Binder.setAndBindContentView(this, R.layout.notice, model);

	}
}
