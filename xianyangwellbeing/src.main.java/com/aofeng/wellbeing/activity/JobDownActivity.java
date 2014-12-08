package com.aofeng.wellbeing.activity;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.JobDownModel;

public class JobDownActivity extends Activity {
	public boolean isBusy;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JobDownModel model = new JobDownModel(this);
		Binder.setAndBindContentView(this, R.layout.jobdown, model);		
	}
	
	@Override
	public void onBackPressed() {
		if(isBusy)
		{
			Toast.makeText(this, "��ȴ�������ɡ�", Toast.LENGTH_SHORT).show();
		}
		else
			super.onBackPressed();
	}
}
