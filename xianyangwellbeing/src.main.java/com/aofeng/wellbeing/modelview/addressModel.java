package com.aofeng.wellbeing.modelview;

import com.aofeng.wellbeing.activity.IndoorInspectActivity;
import com.aofeng.wellbeing.activity.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import gueei.binding.Command;
import gueei.binding.observables.StringObservable;

public class addressModel {
	public Activity mContext;
	public addressModel(Activity Context){
		this.mContext = Context;
	}
	
	public StringObservable userName = new StringObservable("");
	
//	public Command Listaddress = new Command() {
//		@Override
//		public void Invoke(View arg0, Object... arg1) {
//			Intent intent = new Intent(mContext,MainActivity.class);
//			mContext.startActivity(intent);
//		}
//	};
}
