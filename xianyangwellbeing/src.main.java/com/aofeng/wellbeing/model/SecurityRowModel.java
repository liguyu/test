package com.aofeng.wellbeing.model;

import gueei.binding.Command;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;
import com.aofeng.wellbeing.activity.CheckActivity;
import com.aofeng.wellbeing.activity.IndoorInspectActivity;
import com.aofeng.wellbeing.modelview.MySecurityModel;

public class SecurityRowModel {
	public String DISTRICTNAME;
	public String ADDRESS;
	public String CHECKPLAN_ID ;
	
	MySecurityModel model;
	// 下发的安检单ID
	public StringObservable ID = new StringObservable("");
	// 地址
	public StringObservable Address = new StringObservable("");

	
	//上传成功、不成功、已检、未检、拒绝、维修、新增、删除
	public BooleanObservable UPLOADED = new BooleanObservable(false);
	public BooleanObservable UN_UPLOADED = new BooleanObservable(true);
	public BooleanObservable INSPECTED = new BooleanObservable(false);
	public BooleanObservable UN_INSPECTED = new BooleanObservable(true);
	public BooleanObservable DENIED = new BooleanObservable(false);
	public BooleanObservable NOANSWER = new BooleanObservable(false);
	public BooleanObservable REPAIR = new BooleanObservable(false);
	public BooleanObservable NEW = new BooleanObservable(false);
	public BooleanObservable DELETED = new BooleanObservable(false);

	public SecurityRowModel(MySecurityModel model, String id,	String address, String state) {
		this.model = model;
		Address.set(address);
		ID.set(id);
		if(state.length()!=0)
		{
			int flag = Integer.valueOf(state).intValue();
			this.INSPECTED.set((flag & Vault.INSPECT_FLAG)>0);
			this.UN_INSPECTED.set(flag==0);
			this.UPLOADED.set((flag & Vault.UPLOAD_FLAG)>0);
			this.UN_UPLOADED.set(!this.UPLOADED.get());
			this.DENIED.set((flag & Vault.DENIED_FLAG)>0);
			this.NOANSWER.set((flag & Vault.NOANSWER_FLAG)>0);
			this.DELETED.set((flag & Vault.DELETE_FLAG)>0);
			this.NEW.set((flag & Vault.NEW_FLAG)>0);
			this.REPAIR.set((flag & Vault.REPAIR_FLAG)>0);
		}
	}

	// 只读安检记录
	public Command ReadInspection = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Util.ClearCache(model.mContext, Util.getSharedPreference(model.mContext, Vault.USER_ID) + "_" + ID.get());
//			if(!INSPECTED.get())
//				Util.deleteFiles(model.mContext, Util.getSharedPreference(model.mContext, Vault.USER_ID) + "_" + ID.get());
			Intent intent = new Intent();
			// 利用包袱传递参数给Activity
			Bundle bundle = new Bundle();

			bundle.putString("ID", ID.get());
			bundle.putBoolean("INSPECTED", true);
			bundle.putString("CHECKPLAN_ID", CHECKPLAN_ID);
			bundle.putString("DISTRICTNAME", DISTRICTNAME);
			bundle.putString("ADDRESS", ADDRESS);
			//用户要求可修改
			//bundle.putBoolean("READONLY", true);

			intent.setClass(model.mContext, CheckActivity.class);
			intent.putExtras(bundle);
			model.mContext.startActivity(intent);		
		}
	};
}
