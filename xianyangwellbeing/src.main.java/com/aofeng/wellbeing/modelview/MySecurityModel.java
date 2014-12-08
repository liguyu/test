package com.aofeng.wellbeing.modelview;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;

import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.model.FloorRoomRowModel;
import com.aofeng.wellbeing.model.SecurityRowModel;
import com.aofeng.wellbeing.model.UploadRowModel;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

public class MySecurityModel {
		public Activity mContext;

		public Activity getActivity() {
			return mContext;
		}

		public int idx;
		
		public MySecurityModel(Activity context) {
			this.mContext = context;
		}
		/**
		 * 加亮当前选择
		 * @param imgId
		 */
		private void HilightChosenImg(int imgId) {

			allImgId.set(R.drawable.all_btn);
			weiImgId.set(R.drawable.weijian_btn);
			yiImgId.set(R.drawable.yijian_btn);
			juImgId.set(R.drawable.jujian_btn);
			wuImgId.set(R.drawable.wuren_btn);
			if(imgId == R.drawable.all_btn_hover_small)
			{
				idx=0;
				allImgId.set(imgId);
			}
			else if(imgId == R.drawable.weijian_btn_hover)
			{
				idx=1;
				weiImgId.set(imgId);
			}
			else if(imgId == R.drawable.yijian_btn_hover)
			{
				idx=2;
				yiImgId.set(imgId);
			}			
			else if(imgId == R.drawable.jujian_btn_hover)
			{
				idx=4;
				juImgId.set(imgId);
			}		
			else if(imgId == R.drawable.wuren_btn_hover)
			{
				idx=5;
				wuImgId.set(imgId);
			}
		}
/**
 * 根据选择显示列表
 */
		public void listBySelection()
		{
			if(idx==0)
				listByExample(0, false);
			else if(idx==1)
				listByExample(Vault.INSPECT_FLAG, false);
			else if(idx==2)
				listByExample(Vault.INSPECT_FLAG, true);
			else if(idx==4)
				listByExample(Vault.DENIED_FLAG, true);
			else if(idx==5)
				listByExample(Vault.NOANSWER_FLAG, true);
		}
		
		public IntegerObservable allImgId = new IntegerObservable(R.drawable.all_btn_hover_small);
		public Command AllClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.all_btn_hover_small);
				listByExample(0, false);
			}
		};
		
		public IntegerObservable weiImgId = new IntegerObservable(R.drawable.weijian_btn);
		public Command WeiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.weijian_btn_hover);
				listByExample(Vault.INSPECT_FLAG, false);
			}
		};

		public IntegerObservable yiImgId = new IntegerObservable(R.drawable.yijian_btn);
		public Command YiImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.yijian_btn_hover);
				listByExample(Vault.INSPECT_FLAG, true);
			}
		};
		
		public IntegerObservable juImgId = new IntegerObservable(R.drawable.jujian_btn);
		public Command JuImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.jujian_btn_hover);
				listByExample(Vault.DENIED_FLAG, true);
			}
		};
		
		public IntegerObservable wuImgId = new IntegerObservable(R.drawable.wuren_btn);
		public Command WuImgClicked = new Command(){
			public void Invoke(View view, Object... args) {
				HilightChosenImg(R.drawable.wuren_btn_hover);
				listByExample(Vault.NOANSWER_FLAG, true);
			}
		};
		//显示列表
		public ArrayListObservable<SecurityRowModel> plainList = new ArrayListObservable<SecurityRowModel>(
				SecurityRowModel.class);
		
		protected void listByExample(int mask, boolean IsSet) {
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
			String sql = "SELECT id, DISTRICTNAME, ADDRESS, CONDITION, CHECKPLAN_ID" +
					"  FROM T_IC_SAFECHECK_PAPAER " +
					" where   (CAST(CONDITION as INTEGER) & " 
					+  (Vault.INSPECT_FLAG + Vault.DENIED_FLAG + Vault.NOANSWER_FLAG+Vault.REPAIR_FLAG) + ")>0 " +
							" and (CAST(CONDITION as INTEGER) & " +  mask + ")" + (IsSet?">0":"=0") +
							" order by  (CAST(CONDITION as INTEGER) & " +  Vault.REPAIR_FLAG + "),"
					+ " DISTRICTNAME, ADDRESS";
			// 从安检单里获取所有街道名
			Cursor c = db.rawQuery(sql, new String[]{}); 
			plainList.clear();
			while (c.moveToNext()) {
				String address = c.getString(1) + " " + c.getString(2); 
				SecurityRowModel row = new SecurityRowModel(this, 
						c.getString(0), address, c.getString(c.getColumnIndex("CONDITION")));
				row.DISTRICTNAME = c.getString(1);
				row.ADDRESS = c.getString(2);
				row.CHECKPLAN_ID = c.getString(4);
				plainList.add(row);
			}
			db.close();
			
		}
}
