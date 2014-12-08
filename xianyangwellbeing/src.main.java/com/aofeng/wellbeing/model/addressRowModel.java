package com.aofeng.wellbeing.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aofeng.wellbeing.activity.CheckActivity;
import com.aofeng.wellbeing.modelview.BigAddressModel;

import gueei.binding.Command;
import gueei.binding.observables.StringObservable;

public class addressRowModel {
	public Activity mContext;
	
	public StringObservable districtname = new StringObservable("");
	public StringObservable address = new StringObservable("");
	public String ID = "";
	public boolean CONDITION = true;
	public String CHECKPLAN_ID = "";
	public String DISTRICTNAME = "";
	public String ADDRESS = "";
	
	public BigAddressModel model;
	
	public addressRowModel(Activity Context){
		this.mContext = Context;
	}
	
	private boolean GetInfoBySQLite(){
		DISTRICTNAME = districtname.get();
		ADDRESS = address.get();
		
		SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
		String sql = "select id, CONDITION, CHECKPLAN_ID from T_IC_SAFECHECK_PAPAER " + 
					 "where DISTRICTNAME = ? and ADDRESS = ?";
		Cursor c = db.rawQuery(sql, new String[] {DISTRICTNAME, ADDRESS});
		
		if(c.getCount() != 1)
		{
			return false;
		}
		
		c.moveToNext();
		
		ID = c.getString(c.getColumnIndex("id"));
		if(!c.getString(c.getColumnIndex("CONDITION")).equals("0"))
		{
			CONDITION = false;
		}
		CHECKPLAN_ID = c.getString(c.getColumnIndex("CHECKPLAN_ID"));
		
		return true;
	}
	
	public Command Listaddress = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			if(!GetInfoBySQLite())
			{
				Toast.makeText(mContext, "用户信息有误，请联系管理员排查此用户档案", Toast.LENGTH_LONG).show();
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("ID", ID);
			bundle.putString("CHECKPLAN_ID", CHECKPLAN_ID);
			bundle.putBoolean("INSPECTED", !CONDITION);
			bundle.putString("DISTRICTNAME", DISTRICTNAME);
			bundle.putString("ADDRESS", ADDRESS);
			Intent intent = new Intent(mContext,CheckActivity.class);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}
	};
}
