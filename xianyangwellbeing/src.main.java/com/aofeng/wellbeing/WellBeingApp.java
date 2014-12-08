package com.aofeng.wellbeing;

import java.io.File;

import com.aofeng.utils.Util;
import com.aofeng.wellbeing.activity.ShootActivity;

import gueei.binding.Binder;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

public class WellBeingApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
		GetPath();
	}

	private void GetPath() {
		ContextWrapper cw = new ContextWrapper(this);
		File directory = cw.getFilesDir();
		Util.setSharedPreference(this, "FileDir", directory.getAbsolutePath() +"/");
	}
	
	//��ЩActivity��Ҫ�����Ƿ��һ�ν������ĳЩ����
	//����android���������ڻ��Ƶ���
	public boolean IsRepairFirstEntry = false;
	
	//��ǰ��¼�����ݿ���
	public String DBFileName = "";
}
