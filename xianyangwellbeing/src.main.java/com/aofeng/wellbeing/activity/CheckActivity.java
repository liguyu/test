package com.aofeng.wellbeing.activity;

import gueei.binding.app.BindingActivity;
import gueei.binding.listeners.OnCheckedChangeListenerMulticast;
import gueei.binding.validation.ModelValidator;
import gueei.binding.validation.ValidationResult;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.aofeng.utils.MyDigitalClock;
import com.aofeng.utils.Pair;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.modelview.CheckModel;
import com.aofeng.wellbeing.modelview.IndoorInspectModel;
import com.aofeng.wellbeing.modelview.RepairMan;

public class CheckActivity extends BindingActivity{
	
		private CheckModel model;
		//保存临时生成的UUID
		public String uuid;
		private boolean inspected;
		public String anjianprivious = "";
		public String yinhuan = "";
		public String guzhang = "";
		public String bcthrq1 = "";
		public String bcthrq2 = "";
		public String bcthrq3 = "";
		public String bcthrq4 = "";
		
		private Date entryDateTime;
		
		//保存当前窗体输入内容是否已经保存到本地
		public boolean localSaved;
		public String paperId = "test";
		public String planId="";
		public String districtname = "";
		public String address = "";
		
		// ------------------------拍照------------------------------------
		private Button shoot1;
		private ImageView img1;
		private Button shoot2;
		private ImageView img2;
		private Button shoot3;
		private ImageView img3;
		private Button shoot4;
		private ImageView img4;
		private Button shoot5;
		private ImageView img5;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			model = new CheckModel(this);
			this.setAndBindRootView(R.layout.check, model);
			model.muteOthers(R.id.checkMenuPane);
			Bundle bundle = getIntent().getExtras();
			boolean readonly = false;
			if (bundle != null) {
				paperId = bundle.getString("ID");
				planId = bundle.getString("CHECKPLAN_ID");
				inspected = bundle.getBoolean("INSPECTED");
				districtname = bundle.getString("DISTRICTNAME");
				address = bundle.getString("ADDRESS");
				if(bundle.containsKey("READONLY"))
					readonly = bundle.getBoolean("READONLY");
			}
			uuid = Util.getSharedPreference(this, Vault.USER_ID) + "_" + paperId;
			shoot1 = (Button) findViewById(R.id.shoot1);
			shoot1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					// 利用包袱传递参数给Activity
					Bundle bundle = new Bundle();
					bundle.putString("ID", uuid + "_1");
					intent.setClass(CheckActivity.this, ShootActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				}
			});
			img1 = (ImageView) findViewById(R.id.image1);
			shoot2 = (Button) findViewById(R.id.shoot2);
			shoot2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					// 利用包袱传递参数给Activity
					Bundle bundle = new Bundle();
					bundle.putString("ID", uuid + "_2");
					intent.setClass(CheckActivity.this, ShootActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				}
			});
			img2 = (ImageView) findViewById(R.id.image2);
			shoot3 = (Button) findViewById(R.id.shoot3);
			shoot3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					// 利用包袱传递参数给Activity
					Bundle bundle = new Bundle();
					bundle.putString("ID", uuid + "_3");
					intent.setClass(CheckActivity.this, ShootActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				}
			});
			img3 = (ImageView) findViewById(R.id.image3);
			shoot4 = (Button) findViewById(R.id.shoot4);
			shoot4.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					// 利用包袱传递参数给Activity
					Bundle bundle = new Bundle();
					bundle.putString("ID", uuid + "_4");
					intent.setClass(CheckActivity.this, ShootActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				}
			});
			img4 = (ImageView) findViewById(R.id.image4);
			shoot5 = (Button) findViewById(R.id.shoot5);
			shoot5.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					// 利用包袱传递参数给Activity
					Bundle bundle = new Bundle();
					bundle.putString("ID", uuid + "_5");
					intent.setClass(CheckActivity.this, ShootActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				}
			});
			img5 = (ImageView) findViewById(R.id.image5);
			Button clear1 = (Button) findViewById(R.id.clear1);
			clear1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					img1.setImageBitmap(null);
					Util.releaseBitmap(img1);
					if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid
							+ "_1.jpg"))
						new File(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_"
								+ "1.jpg").delete();
				}
			});
			Button clear2 = (Button) findViewById(R.id.clear2);
			clear2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					img2.setImageBitmap(null);
					Util.releaseBitmap(img2);
					if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid
							+ "_2.jpg"))
						new File(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_"
								+ "2.jpg").delete();
				}
			});
			Button clear3 = (Button) findViewById(R.id.clear3);
			clear3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					img3.setImageBitmap(null);
					Util.releaseBitmap(img3);
					if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid
							+ "_3.jpg"))
						new File(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_"
								+ "3.jpg").delete();
				}
			});
			Button clear4 = (Button) findViewById(R.id.clear4);
			clear4.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					img4.setImageBitmap(null);
					Util.releaseBitmap(img4);
					if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid
							+ "_4.jpg"))
						new File(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_"
								+ "4.jpg").delete();
				}
			});
			Button clear5 = (Button) findViewById(R.id.clear5);
			clear5.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					img5.setImageBitmap(null);
					Util.releaseBitmap(img5);
					if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid
							+ "_5.jpg"))
						new File(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_"
								+ "5.jpg").delete();
				}
			});
			
			OnClickListener imgZoom = new OnClickListener()
			{
				@Override
				public void onClick(View v) {		
					int vid = v.getId();
					if(vid == R.id.image1)
						showZoomDialog(1);
					else if(vid == R.id.image2)
						showZoomDialog(2);
					else if(vid == R.id.image3)
						showZoomDialog(3);
					else if(vid == R.id.image4)
						showZoomDialog(4);
					else if(vid == R.id.image5)
						showZoomDialog(5);
				}
			};
			img1.setOnClickListener(imgZoom);
			img2.setOnClickListener(imgZoom);
			img3.setOnClickListener(imgZoom);
			img4.setOnClickListener(imgZoom);
			img5.setOnClickListener(imgZoom);

			if(readonly)
				DisableLayouts();
			this.initUI();
			this.preDisplayUIWork();
		}

		//显示图片对话框
		private void showZoomDialog(int  vid)
		{
			if (!Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_" + vid + ".jpg"))
				return;
			final ImageView iv = new ImageView(this);
			iv.layout(0, 0, 600, 400);
			try
			{
				Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
						 + uuid + "_" + vid + ".jpg");
				iv.setImageBitmap(bmp);
				Dialog alertDialog = new AlertDialog.Builder(this).
						setView(iv).
						setTitle("").   
						setOnCancelListener(new OnCancelListener() {
							
							@Override
							public void onCancel(DialogInterface dialog) {
								Util.releaseBitmap(iv);
							}
						}).
						setIcon(android.R.drawable.ic_dialog_info).
						create();   
				WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();  
		        layoutParams.width = 600;
		        layoutParams.height= 400;
		        alertDialog.getWindow().setAttributes(layoutParams);
				alertDialog.show();
			}
			catch(Exception e)
			{
				Toast.makeText(this, "获取图片失败。错误： " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
		/**
		 * 设置右边布局为禁止使用
		 */
		private void DisableLayouts() {
			MyDigitalClock clock = (MyDigitalClock)findViewById(R.id.digitalClock);
			int[] panes = {R.id.basicPane, R.id.meterPane, R.id.plumPane, R.id.cookerPane, R.id.precautionPane, R.id.feedbackPane};
			for(int i=0; i<panes.length; i++)
			{
				ViewGroup vg = (ViewGroup)findViewById(panes[i]);
				disable(vg);
			}
		}
		
		/**
		 * disable every view in the layout recursively
		 * @param layout
		 */
		private void disable(ViewGroup layout) {
			layout.setEnabled(false);
			for (int i = 0; i < layout.getChildCount(); i++) {
				View child = layout.getChildAt(i);
				if (child instanceof ViewGroup) {
					disable((ViewGroup) child);
				} else {
					child.setEnabled(false);
				}
			}
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent intent) {
			super.onActivityResult(requestCode, resultCode, intent);
			if (intent == null)
				return;
			if(resultCode == 130)
			{
				model.f_userid.set(intent.getStringExtra("UserId"));
				GetUserInfo();
			}
			else
			{
				try
				{
					String result = intent.getStringExtra("result");
					Bitmap bmp;
					if(intent.hasExtra("signature"))
					{
						if(!Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + result + ".png"))
							return;
						ImageView signPad = ((ImageView)findViewById(R.id.signPad));
						Util.releaseBitmap(signPad);
						bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir") + result + ".png");
						((ImageView)findViewById(R.id.signPad)).setImageBitmap(bmp);
					}
					else
					{
						if(!Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + result + ".jpg"))
							return;
						bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir") + result + ".jpg");
						String idx = result.substring(result.length() - 1);
						if (idx.equals("1"))
						{
							Util.releaseBitmap(img1);
							img1.setImageBitmap(bmp);
						}
						else if (idx.equals("2"))
						{
							Util.releaseBitmap(img2);
							img2.setImageBitmap(bmp);
						}
						else if (idx.equals("3"))
						{
							Util.releaseBitmap(img3);
							img3.setImageBitmap(bmp);
						}
						else if (idx.equals("4"))
						{
							Util.releaseBitmap(img4);
							img4.setImageBitmap(bmp);
						}
						else if (idx.equals("5"))
						{
							Util.releaseBitmap(img5);
							img5.setImageBitmap(bmp);
						}
					}
				}
				catch(Exception e)
				{
					Toast.makeText(this, "获取图片失败。错误： " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		protected void GetUserInfo()
		{
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if(model.f_userid.get().length() != 0)
						{
							//Nothing to do.
						}
						else
						{
							Message msg = new Message();
							msg.what = 3;
							listHandler.sendMessage(msg);
							return;
						}
						
						String sql = "select f_username, f_phone, f_districtname, f_address, f_meternumber, f_gasmeterstyle, f_gasmetermanufacturers, f_aroundmeter from T_USERFILES where f_userid = '" + model.f_userid.get() + "'";
						String url = Vault.DB_URL + "sql/"
								+ URLEncoder
								.encode(sql, "UTF8")
										.replace("+", "%20");
						HttpGet getMethod = new HttpGet(url);
						HttpClient httpClient = new DefaultHttpClient();
						HttpResponse response = httpClient.execute(getMethod);

						int code = response.getStatusLine().getStatusCode();

						// 数据下载完成
						if (code == 200) {
							String strResult = EntityUtils.toString(
									response.getEntity(), "UTF8");
							Message msg = new Message();
							msg.obj = strResult;
							msg.what = 1;
							listHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 2;
							listHandler.sendMessage(msg);
						}
					}catch (Exception e) {
						Message msg = new Message();
						msg.what = 0;
						listHandler.sendMessage(msg);
					}
				}
			});
			th.start();
		}
		
		private final Handler listHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				if (1 == msg.what) {
					super.handleMessage(msg);
					try {
						JSONArray array = new JSONArray((String) msg.obj);
						for(int i=0; i<array.length(); i++)
						{
							JSONObject obj = array.getJSONObject(i);
							if(obj.has("col0"))
								model.f_username.set(obj.getString("col0"));
							if(obj.has("col1"))
								model.f_huifangtelephone.set(obj.getString("col1"));
							if(obj.has("col2"))
								model.f_districtname = obj.getString("col2");
							if(obj.has("col3"))
								model.f_addressDB = obj.getString("col3");
							if(obj.has("col4"))
								model.f_meternumber.set(obj.getString("col4"));
							if(obj.has("col5"))
								model.f_gasmeterstyle.set(obj.getString("col5"));
							if(obj.has("col6"))
								model.f_gaswatchbrand.set(obj.getString("col6"));
							if(obj.has("col7"))
								model.f_aroundmeter.set(obj.getString("col7"));
							model.f_address.set(model.f_districtname + "-" + model.f_addressDB);
						}
					} catch (Exception e) {
						e.printStackTrace();
						// 查不到此IC卡用户
						Toast.makeText(getApplicationContext(), "无此用户信息！", Toast.LENGTH_SHORT).show();
					}
				} else if (0 == msg.what) {
					Toast.makeText(getApplicationContext(), "请检查网络或者与管理员联系。", Toast.LENGTH_LONG).show();
				} else if (2 == msg.what) {
					Toast.makeText(getApplicationContext(), "无此用户。", Toast.LENGTH_LONG).show();
				} else if (3 == msg.what) {
					Toast.makeText(getApplicationContext(), "请输入用户姓名。", Toast.LENGTH_LONG).show();
				}
			}
		};

		/**
		 * 输入验证
		 * 
		 * @return
		 */
		public boolean validate() {

			return true;
		}

		// 本地保存安检记录
		public boolean Save(String objStr, String inspectionTable, boolean isTemp) {
			try {
				SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
						Context.MODE_PRIVATE, null);
				JSONObject row = new JSONObject(objStr);
				String paperId = row.getString("CHECKPAPER_ID");
				// 删安检除单
				db.execSQL("DELETE FROM " + inspectionTable + "  where CHECKPAPER_ID=" + paperId);
				String sql1 = "INSERT INTO " + inspectionTable + " (ID";
				String sql2 = ") VALUES(" + Util.quote(uuid) ;
				Iterator<String> itr = row.keys();
				while (itr.hasNext()) {
					String key = itr.next();
					if (key.equals("ID"))
						continue;
					sql1 += "," + key;
					sql2 += "," + row.getString(key);
				}
				sql1 += sql2 + ")";
				db.execSQL(sql1);
				db.close();
				if(!isTemp)
				{
					//更新安检状态		
					String state = row.getString("CONDITION");
					boolean needsRepair = false;
					SetInspectionState(paperId, state, needsRepair);
				}
				return true;

			} catch (Exception e) {
				Log.d("Check", e.getMessage());
				return false;
			}
		}

		/**
		 * 更新安检状态
		 * @param paperId
		 * @param state
		 * @param needsRepair 
		 */
		private void SetInspectionState(String paperId, String state, boolean needsRepair) {
			//TODO
			if(paperId.startsWith("'"))
				paperId= paperId.substring(1, paperId.length()-1);
			if(state.startsWith("'"))
				state= state.substring(1, state.length()-1);
			
			if(state.equals("拒入"))
			{
				Util.SetBit(this, Vault.NOANSWER_FLAG, paperId);
				Util.ClearBit(this, Vault.REPAIR_FLAG, paperId);
			}
			else if(state.equals("拒签"))
			{
				Util.SetBit(this, Vault.DENIED_FLAG , paperId);
				Util.ClearBit(this, Vault.REPAIR_FLAG, paperId);
			}
			else
			{
				Util.SetBit(this, Vault.INSPECT_FLAG, paperId);
				if(needsRepair)
					Util.SetBit(this, Vault.REPAIR_FLAG, paperId);
				else
					Util.ClearBit(this, Vault.REPAIR_FLAG, paperId);
			}
		}
		
		private void initUI()
		{
			((EditText)findViewById(R.id.kitchenbrand)).setEnabled(false);
			((Spinner)findViewById(R.id.kitchensize)).setEnabled(false);
			((EditText)findViewById(R.id.waterheaterbrand)).setEnabled(false);
			((EditText)findViewById(R.id.heatersize)).setEnabled(false);
			((EditText)findViewById(R.id.wallhangboilerbrand)).setEnabled(false);
			((EditText)findViewById(R.id.handboilersize)).setEnabled(false);
			((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(false);
			((CheckBox)findViewById(R.id.issqgg)).setEnabled(false);
			((CheckBox)findViewById(R.id.isktfk)).setEnabled(false);
			((CheckBox)findViewById(R.id.isbx)).setEnabled(false);
			((CheckBox)findViewById(R.id.islxwgs)).setEnabled(false);
			((CheckBox)findViewById(R.id.isghrg)).setEnabled(false);
			((CheckBox)findViewById(R.id.isydrqj)).setEnabled(false);
			((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(false);
			((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(false);
			((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(false);
			((CheckBox)findViewById(R.id.isccghrqj)).setEnabled(false);
			((CheckBox)findViewById(R.id.iszxzg2)).setEnabled(false);
			((CheckBox)findViewById(R.id.iszxzg3)).setEnabled(false);
			((CheckBox)findViewById(R.id.isycqthy)).setEnabled(false);
			((CheckBox)findViewById(R.id.isjiageduan)).setEnabled(false);
			((CheckBox)findViewById(R.id.iszxzg6)).setEnabled(false);
			((CheckBox)findViewById(R.id.ishfcfyt)).setEnabled(false);
			((CheckBox)findViewById(R.id.isdgsdtsq)).setEnabled(false);
			((CheckBox)findViewById(R.id.isqbzrtf)).setEnabled(false);
			((CheckBox)findViewById(R.id.iszxzg4)).setEnabled(false);
			((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(false);
			((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(false);
			((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
		}
		
		/**
		 * 界面相关操作
		 */
		private void preDisplayUIWork()
		{
			final EditText alarm = (EditText)this.findViewById(R.id.securitydate);
			alarm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String str = alarm.getText().toString();
					Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)");
					Matcher match = pattern.matcher(str);
					int year = 2014, month = 0;
					if(match.find())
					{
						year = Integer.valueOf(match.group(1));
						month = Integer.valueOf(match.group(2))-1;
					}
					DatePickerDialog Dt1 = new DatePickerDialog(CheckActivity.this,
							new OnDateSetListener() {	
									@Override
									public void onDateSet(DatePicker view, int year,	int monthOfYear, int dayOfMonth) {
										String month = "";
										String day = "";
										String date = "";
										if(monthOfYear < 10)
										{
											month = "0" + (monthOfYear + 1);
										}
										else
										{
											month = (monthOfYear + 1) + "";
										}
										if(dayOfMonth < 10)
										{
											day = "0" + dayOfMonth;
										}
										else
										{
											day = dayOfMonth + "";
										}
										date = year + month + day;
										model.f_securitydate.set(date);
									}
							}, year, month, 1);
							Dt1.show();
				}
			});
			
			((CheckBox)findViewById(R.id.ismanyi)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						model.f_isbumanyi.set(false);
					}
				}
			});
			
			((CheckBox)findViewById(R.id.isnotmanyi)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						model.f_ismanyi.set(false);
					}
				}
			});
			
			((CheckBox)findViewById(R.id.kitchen)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_kitchen.set(isChecked);
					if(!isChecked)
					{
						model.f_kitchenbrand.set("");
						((EditText)findViewById(R.id.kitchenbrand)).setEnabled(false);
						((Spinner)findViewById(R.id.kitchensize)).setEnabled(false);
						((Spinner)findViewById(R.id.kitchensize)).setSelection(0);
					}
					else
					{
						((EditText)findViewById(R.id.kitchenbrand)).setEnabled(true);
						((Spinner)findViewById(R.id.kitchensize)).setEnabled(true);
					}
				}
			});

			((CheckBox)findViewById(R.id.waterheater)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_waterheater.set(isChecked);
					if(!isChecked)
					{
						model.f_waterheaterbrand.set("");
						model.f_heatersize.set("");
						((EditText)findViewById(R.id.waterheaterbrand)).setEnabled(false);
						((EditText)findViewById(R.id.heatersize)).setEnabled(false);
					}
					else
					{
						((EditText)findViewById(R.id.waterheaterbrand)).setEnabled(true);
						((EditText)findViewById(R.id.heatersize)).setEnabled(true);
					}
				}
			});
			
			((CheckBox)findViewById(R.id.wallhangboiler)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_wallhangboiler.set(isChecked);
					if(!isChecked)
					{
						model.f_wallhangboilerbrand.set("");
						model.f_handboilersize.set("");
						((EditText)findViewById(R.id.wallhangboilerbrand)).setEnabled(false);
						((EditText)findViewById(R.id.handboilersize)).setEnabled(false);
					}
					else
					{
						((EditText)findViewById(R.id.wallhangboilerbrand)).setEnabled(true);
						((EditText)findViewById(R.id.handboilersize)).setEnabled(true);
					}
				}
			});
			
			OnCheckedChangeListener gasCheck = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
					model.f_isbtlq.set(isChecked);				
					if(((CheckBox)findViewById(R.id.isbtlq)).isChecked() || ((CheckBox)findViewById(R.id.isfmlq)).isChecked() || ((CheckBox)findViewById(R.id.isgdjkclq)).isChecked())
					{
						((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(true);
					}
					else
					{
						model.f_ishnczlqxx.set(false);
						((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbtlq)).setOnCheckedChangeListener(gasCheck);
			
			OnCheckedChangeListener gasCheck2 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
					model.f_isfmlq.set(isChecked);				
					if(((CheckBox)findViewById(R.id.isbtlq)).isChecked() || ((CheckBox)findViewById(R.id.isfmlq)).isChecked() || ((CheckBox)findViewById(R.id.isgdjkclq)).isChecked())
					{
						((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(true);
					}
					else
					{
						model.f_ishnczlqxx.set(false);
						((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isfmlq)).setOnCheckedChangeListener(gasCheck2);
			
			OnCheckedChangeListener gasCheck3 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
					model.f_isgdjkclq.set(isChecked);					
					if(((CheckBox)findViewById(R.id.isbtlq)).isChecked() || ((CheckBox)findViewById(R.id.isfmlq)).isChecked() || ((CheckBox)findViewById(R.id.isgdjkclq)).isChecked())
					{
						((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(true);
					}
					else
					{
						model.f_ishnczlqxx.set(false);
						((CheckBox)findViewById(R.id.ishnczlqxx)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isgdjkclq)).setOnCheckedChangeListener(gasCheck3);
			
			((CheckBox)findViewById(R.id.issghnrqgd)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issghnrqgd.set(isChecked);
					if(!isChecked)
					{
						model.f_issqgg.set(false);
						((CheckBox)findViewById(R.id.issqgg)).setEnabled(false);
					}
					else
					{
						((CheckBox)findViewById(R.id.issqgg)).setEnabled(true);
					}
				}
			});
			
			OnCheckedChangeListener bgmeter = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbb.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbb)).isChecked() || ((CheckBox)findViewById(R.id.isbg)).isChecked() || ((CheckBox)findViewById(R.id.isbf)).isChecked())
					{
						((CheckBox)findViewById(R.id.isktfk)).setEnabled(true);
					}
					else
					{
						model.f_isktfk.set(false);
						((CheckBox)findViewById(R.id.isktfk)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbb)).setOnCheckedChangeListener(bgmeter);
			
			OnCheckedChangeListener bgmeter2 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbg.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbb)).isChecked() || ((CheckBox)findViewById(R.id.isbg)).isChecked() || ((CheckBox)findViewById(R.id.isbf)).isChecked())
					{
						((CheckBox)findViewById(R.id.isktfk)).setEnabled(true);
					}
					else
					{
						model.f_isktfk.set(false);
						((CheckBox)findViewById(R.id.isktfk)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbg)).setOnCheckedChangeListener(bgmeter2);
			
			OnCheckedChangeListener bgmeter3 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbf.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbb)).isChecked() || ((CheckBox)findViewById(R.id.isbg)).isChecked() || ((CheckBox)findViewById(R.id.isbf)).isChecked())
					{
						((CheckBox)findViewById(R.id.isktfk)).setEnabled(true);
					}
					else
					{
						model.f_isktfk.set(false);
						((CheckBox)findViewById(R.id.isktfk)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbf)).setOnCheckedChangeListener(bgmeter3);
			
			OnCheckedChangeListener btmeter = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbtxs.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbtxs)).isChecked() || ((CheckBox)findViewById(R.id.isgdxs)).isChecked())
					{
						((CheckBox)findViewById(R.id.isbx)).setEnabled(true);
					}
					else
					{
						model.f_isbx.set(false);
						((CheckBox)findViewById(R.id.isbx)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbtxs)).setOnCheckedChangeListener(btmeter);
			
			OnCheckedChangeListener btmeter1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isgdxs.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbtxs)).isChecked() || ((CheckBox)findViewById(R.id.isgdxs)).isChecked())
					{
						((CheckBox)findViewById(R.id.isbx)).setEnabled(true);
					}
					else
					{
						model.f_isbx.set(false);
						((CheckBox)findViewById(R.id.isbx)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isgdxs)).setOnCheckedChangeListener(btmeter1);
			
			OnCheckedChangeListener line = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbqrlj.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbqrlj)).isChecked() || ((CheckBox)findViewById(R.id.isbhrlj)).isChecked())
					{
						((CheckBox)findViewById(R.id.islxwgs)).setEnabled(true);
					}
					else
					{
						model.f_islxwgs.set(false);
						((CheckBox)findViewById(R.id.islxwgs)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbqrlj)).setOnCheckedChangeListener(line);
			
			OnCheckedChangeListener line1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbhrlj.set(isChecked);
					if(((CheckBox)findViewById(R.id.isbqrlj)).isChecked() || ((CheckBox)findViewById(R.id.isbhrlj)).isChecked())
					{
						((CheckBox)findViewById(R.id.islxwgs)).setEnabled(true);
					}
					else
					{
						model.f_islxwgs.set(false);
						((CheckBox)findViewById(R.id.islxwgs)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbhrlj)).setOnCheckedChangeListener(line1);
			
			OnCheckedChangeListener guandao = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrglh.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrglh)).isChecked() || ((CheckBox)findViewById(R.id.isrgcc)).isChecked() || ((CheckBox)findViewById(R.id.isrgcqdmc)).isChecked() || ((CheckBox)findViewById(R.id.isrgwgk)).isChecked() || ((CheckBox)findViewById(R.id.islgwgk)).isChecked())
					{
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(true);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(true);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(true);
					}
					else
					{
						model.f_isghrg.set(false);
						model.f_isydrqj.set(false);
						model.f_iszxzg1.set(false);
						model.f_isrggkyff.set(false);
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(false);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(false);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrglh)).setOnCheckedChangeListener(guandao);
			
			OnCheckedChangeListener guandao1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrgcc.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrglh)).isChecked() || ((CheckBox)findViewById(R.id.isrgcc)).isChecked() || ((CheckBox)findViewById(R.id.isrgcqdmc)).isChecked() || ((CheckBox)findViewById(R.id.isrgwgk)).isChecked() || ((CheckBox)findViewById(R.id.islgwgk)).isChecked())
					{
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(true);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(true);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(true);
					}
					else
					{
						model.f_isghrg.set(false);
						model.f_isydrqj.set(false);
						model.f_iszxzg1.set(false);
						model.f_isrggkyff.set(false);
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(false);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(false);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrgcc)).setOnCheckedChangeListener(guandao1);
			
			OnCheckedChangeListener guandao2 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrgcqdmc.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrglh)).isChecked() || ((CheckBox)findViewById(R.id.isrgcc)).isChecked() || ((CheckBox)findViewById(R.id.isrgcqdmc)).isChecked() || ((CheckBox)findViewById(R.id.isrgwgk)).isChecked() || ((CheckBox)findViewById(R.id.islgwgk)).isChecked())
					{
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(true);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(true);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(true);
					}
					else
					{
						model.f_isghrg.set(false);
						model.f_isydrqj.set(false);
						model.f_iszxzg1.set(false);
						model.f_isrggkyff.set(false);
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(false);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(false);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrgcqdmc)).setOnCheckedChangeListener(guandao2);
			
			OnCheckedChangeListener guandao3 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrgwgk.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrglh)).isChecked() || ((CheckBox)findViewById(R.id.isrgcc)).isChecked() || ((CheckBox)findViewById(R.id.isrgcqdmc)).isChecked() || ((CheckBox)findViewById(R.id.isrgwgk)).isChecked() || ((CheckBox)findViewById(R.id.islgwgk)).isChecked())
					{
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(true);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(true);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(true);
					}
					else
					{
						model.f_isghrg.set(false);
						model.f_isydrqj.set(false);
						model.f_iszxzg1.set(false);
						model.f_isrggkyff.set(false);
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(false);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(false);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrgwgk)).setOnCheckedChangeListener(guandao3);
			
			OnCheckedChangeListener guandao4 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_islgwgk.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrglh)).isChecked() || ((CheckBox)findViewById(R.id.isrgcc)).isChecked() || ((CheckBox)findViewById(R.id.isrgcqdmc)).isChecked() || ((CheckBox)findViewById(R.id.isrgwgk)).isChecked() || ((CheckBox)findViewById(R.id.islgwgk)).isChecked())
					{
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(true);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(true);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(true);
					}
					else
					{
						model.f_isghrg.set(false);
						model.f_isydrqj.set(false);
						model.f_iszxzg1.set(false);
						model.f_isrggkyff.set(false);
						((CheckBox)findViewById(R.id.isghrg)).setEnabled(false);
						((CheckBox)findViewById(R.id.isydrqj)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg1)).setEnabled(false);
						((CheckBox)findViewById(R.id.isrggkyff)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.islgwgk)).setOnCheckedChangeListener(guandao4);
			
			OnCheckedChangeListener sijie = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issjrsq.set(isChecked);
					if(((CheckBox)findViewById(R.id.issjrsq)).isChecked() || ((CheckBox)findViewById(R.id.issjbgl)).isChecked() || ((CheckBox)findViewById(R.id.isyjf)).isChecked() || ((CheckBox)findViewById(R.id.issjrst)).isChecked())
					{
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(true);
					}
					else
					{
						model.f_islxwgs1.set(false);
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.issjrsq)).setOnCheckedChangeListener(sijie);
			
			OnCheckedChangeListener sijie1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issjbgl.set(isChecked);
					if(((CheckBox)findViewById(R.id.issjrsq)).isChecked() || ((CheckBox)findViewById(R.id.issjbgl)).isChecked() || ((CheckBox)findViewById(R.id.isyjf)).isChecked() || ((CheckBox)findViewById(R.id.issjrst)).isChecked())
					{
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(true);
					}
					else
					{
						model.f_islxwgs1.set(false);
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.issjbgl)).setOnCheckedChangeListener(sijie1);
			
			OnCheckedChangeListener sijie2 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isyjf.set(isChecked);
					if(((CheckBox)findViewById(R.id.issjrsq)).isChecked() || ((CheckBox)findViewById(R.id.issjbgl)).isChecked() || ((CheckBox)findViewById(R.id.isyjf)).isChecked() || ((CheckBox)findViewById(R.id.issjrst)).isChecked())
					{
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(true);
					}
					else
					{
						model.f_islxwgs1.set(false);
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isyjf)).setOnCheckedChangeListener(sijie2);
			
			OnCheckedChangeListener sijie3 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issjrst.set(isChecked);
					if(((CheckBox)findViewById(R.id.issjrsq)).isChecked() || ((CheckBox)findViewById(R.id.issjbgl)).isChecked() || ((CheckBox)findViewById(R.id.isyjf)).isChecked() || ((CheckBox)findViewById(R.id.issjrst)).isChecked())
					{
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(true);
					}
					else
					{
						model.f_islxwgs1.set(false);
						((CheckBox)findViewById(R.id.islxwgs1)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.issjrst)).setOnCheckedChangeListener(sijie3);
			
			OnCheckedChangeListener gaizhuang = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isgzzj.set(isChecked);
					if(((CheckBox)findViewById(R.id.isgzzj)).isChecked() || ((CheckBox)findViewById(R.id.isgzrsq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isccghrqj)).setEnabled(true);
					}
					else
					{
						model.f_isccghrqj.set(false);
						((CheckBox)findViewById(R.id.isccghrqj)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isgzzj)).setOnCheckedChangeListener(gaizhuang);
			
			OnCheckedChangeListener gaizhuang1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isgzrsq.set(isChecked);
					if(((CheckBox)findViewById(R.id.isgzzj)).isChecked() || ((CheckBox)findViewById(R.id.isgzrsq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isccghrqj)).setEnabled(true);
					}
					else
					{
						model.f_isccghrqj.set(false);
						((CheckBox)findViewById(R.id.isccghrqj)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isgzrsq)).setOnCheckedChangeListener(gaizhuang1);
			
			OnCheckedChangeListener yandao = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrsqwyd.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrsqwyd)).isChecked() || ((CheckBox)findViewById(R.id.isbglwyd)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg2)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg2.set(false);
						((CheckBox)findViewById(R.id.iszxzg2)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrsqwyd)).setOnCheckedChangeListener(yandao);
			
			OnCheckedChangeListener yandao1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbglwyd.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrsqwyd)).isChecked() || ((CheckBox)findViewById(R.id.isbglwyd)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg2)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg2.set(false);
						((CheckBox)findViewById(R.id.iszxzg2)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbglwyd)).setOnCheckedChangeListener(yandao1);
			
			OnCheckedChangeListener gyyandao = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrsqgyyd.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrsqgyyd)).isChecked() || ((CheckBox)findViewById(R.id.isbglgyyd)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg3)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg3.set(false);
						((CheckBox)findViewById(R.id.iszxzg3)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrsqgyyd)).setOnCheckedChangeListener(gyyandao);
			
			OnCheckedChangeListener gyyandao1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isbglgyyd.set(isChecked);
					if(((CheckBox)findViewById(R.id.isrsqgyyd)).isChecked() || ((CheckBox)findViewById(R.id.isbglgyyd)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg3)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg3.set(false);
						((CheckBox)findViewById(R.id.iszxzg3)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isbglgyyd)).setOnCheckedChangeListener(gyyandao1);
			
			((CheckBox)findViewById(R.id.isczlzyshyhy)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isczlzyshyhy.set(isChecked);
					if(!isChecked)
					{
						model.f_isycqthy.set(false);
						((CheckBox)findViewById(R.id.isycqthy)).setEnabled(false);
					}
					else
					{
						((CheckBox)findViewById(R.id.isycqthy)).setEnabled(true);
					}
				}
			});
			
			OnCheckedChangeListener chufang = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issykfscf.set(isChecked);
					if(((CheckBox)findViewById(R.id.issykfscf)).isChecked() || ((CheckBox)findViewById(R.id.iscfdfzw)).isChecked())
					{
						((CheckBox)findViewById(R.id.isjiageduan)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg6)).setEnabled(true);
					}
					else
					{
						model.f_isjiageduan.set(false);
						model.f_iszxzg6.set(false);
						((CheckBox)findViewById(R.id.isjiageduan)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg6)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.issykfscf)).setOnCheckedChangeListener(chufang);
			
			OnCheckedChangeListener chufang1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_iscfdfzw.set(isChecked);
					if(((CheckBox)findViewById(R.id.issykfscf)).isChecked() || ((CheckBox)findViewById(R.id.iscfdfzw)).isChecked())
					{
						((CheckBox)findViewById(R.id.isjiageduan)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg6)).setEnabled(true);
					}
					else
					{
						model.f_isjiageduan.set(false);
						model.f_iszxzg6.set(false);
						((CheckBox)findViewById(R.id.isjiageduan)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg6)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.iscfdfzw)).setOnCheckedChangeListener(chufang1);
			
			OnCheckedChangeListener gaibian = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isgbcfyt.set(isChecked);
					if(((CheckBox)findViewById(R.id.isgbcfyt)).isChecked() || ((CheckBox)findViewById(R.id.isgbyqxz)).isChecked())
					{
						((CheckBox)findViewById(R.id.ishfcfyt)).setEnabled(true);
						((CheckBox)findViewById(R.id.isdgsdtsq)).setEnabled(true);
					}
					else
					{
						model.f_ishfcfyt.set(false);
						model.f_isdgsdtsq.set(false);
						((CheckBox)findViewById(R.id.ishfcfyt)).setEnabled(false);
						((CheckBox)findViewById(R.id.isdgsdtsq)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isgbcfyt)).setOnCheckedChangeListener(gaibian);
			
			OnCheckedChangeListener gaibian1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isgbyqxz.set(isChecked);
					if(((CheckBox)findViewById(R.id.isgbcfyt)).isChecked() || ((CheckBox)findViewById(R.id.isgbyqxz)).isChecked())
					{
						((CheckBox)findViewById(R.id.ishfcfyt)).setEnabled(true);
						((CheckBox)findViewById(R.id.isdgsdtsq)).setEnabled(true);
					}
					else
					{
						model.f_ishfcfyt.set(false);
						model.f_isdgsdtsq.set(false);
						((CheckBox)findViewById(R.id.ishfcfyt)).setEnabled(false);
						((CheckBox)findViewById(R.id.isdgsdtsq)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isgbyqxz)).setOnCheckedChangeListener(gaibian1);
			
			((CheckBox)findViewById(R.id.issymbkj)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issymbkj.set(isChecked);
					if(!isChecked)
					{
						model.f_isqbzrtf.set(false);
						model.f_iszxzg4.set(false);
						((CheckBox)findViewById(R.id.isqbzrtf)).setEnabled(false);
						((CheckBox)findViewById(R.id.iszxzg4)).setEnabled(false);
					}
					else
					{
						((CheckBox)findViewById(R.id.isqbzrtf)).setEnabled(true);
						((CheckBox)findViewById(R.id.iszxzg4)).setEnabled(true);
					}
				}
			});
			
			OnCheckedChangeListener huanjing = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isxyswlm.set(isChecked);
					if(((CheckBox)findViewById(R.id.isxyswlm)).isChecked() || ((CheckBox)findViewById(R.id.isczdxcr)).isChecked() || ((CheckBox)findViewById(R.id.iszjxysslm)).isChecked() || ((CheckBox)findViewById(R.id.isrsqxysslm)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(true);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg5.set(false);
						model.f_islxwgs2.set(false);
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(false);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isxyswlm)).setOnCheckedChangeListener(huanjing);
			
			OnCheckedChangeListener huanjing1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isczdxcr.set(isChecked);
					if(((CheckBox)findViewById(R.id.isxyswlm)).isChecked() || ((CheckBox)findViewById(R.id.isczdxcr)).isChecked() || ((CheckBox)findViewById(R.id.iszjxysslm)).isChecked() || ((CheckBox)findViewById(R.id.isrsqxysslm)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(true);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg5.set(false);
						model.f_islxwgs2.set(false);
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(false);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isczdxcr)).setOnCheckedChangeListener(huanjing1);
			
			OnCheckedChangeListener huanjing2 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_iszjxysslm.set(isChecked);
					if(((CheckBox)findViewById(R.id.isxyswlm)).isChecked() || ((CheckBox)findViewById(R.id.isczdxcr)).isChecked() || ((CheckBox)findViewById(R.id.iszjxysslm)).isChecked() || ((CheckBox)findViewById(R.id.isrsqxysslm)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(true);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg5.set(false);
						model.f_islxwgs2.set(false);
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(false);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.iszjxysslm)).setOnCheckedChangeListener(huanjing2);
			
			OnCheckedChangeListener huanjing3 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isrsqxysslm.set(isChecked);
					if(((CheckBox)findViewById(R.id.isxyswlm)).isChecked() || ((CheckBox)findViewById(R.id.isczdxcr)).isChecked() || ((CheckBox)findViewById(R.id.iszjxysslm)).isChecked() || ((CheckBox)findViewById(R.id.isrsqxysslm)).isChecked())
					{
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(true);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(true);
					}
					else
					{
						model.f_iszxzg5.set(false);
						model.f_islxwgs2.set(false);
						((CheckBox)findViewById(R.id.iszxzg5)).setEnabled(false);
						((CheckBox)findViewById(R.id.islxwgs2)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isrsqxysslm)).setOnCheckedChangeListener(huanjing3);
			
			OnCheckedChangeListener jiliang = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isjxsb.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isjxsb)).setOnCheckedChangeListener(jiliang);
			
			OnCheckedChangeListener jiliang1 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_issqbj.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.issqbj)).setOnCheckedChangeListener(jiliang1);
			
			OnCheckedChangeListener jiliang2 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isqlbf.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isqlbf)).setOnCheckedChangeListener(jiliang2);
			
			OnCheckedChangeListener jiliang3 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_iscfck.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.iscfck)).setOnCheckedChangeListener(jiliang3);
			
			OnCheckedChangeListener jiliang4 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isddbgf.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isddbgf)).setOnCheckedChangeListener(jiliang4);
			
			OnCheckedChangeListener jiliang5 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_iszndq.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.iszndq)).setOnCheckedChangeListener(jiliang5);
			
			OnCheckedChangeListener jiliang6 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isyjxsgz.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isyjxsgz)).setOnCheckedChangeListener(jiliang6);
			
			OnCheckedChangeListener jiliang7 = new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					model.f_isznbjq.set(isChecked);
					if(((CheckBox)findViewById(R.id.isjxsb)).isChecked() || ((CheckBox)findViewById(R.id.issqbj)).isChecked() || ((CheckBox)findViewById(R.id.isqlbf)).isChecked() || ((CheckBox)findViewById(R.id.iscfck)).isChecked() || ((CheckBox)findViewById(R.id.isddbgf)).isChecked() || ((CheckBox)findViewById(R.id.iszndq)).isChecked() || ((CheckBox)findViewById(R.id.isyjxsgz)).isChecked() || ((CheckBox)findViewById(R.id.isznbjq)).isChecked())
					{
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(true);
					}
					else
					{
						model.f_isqbczgz.set(false);
						((CheckBox)findViewById(R.id.isqbczgz)).setEnabled(false);
					}
				}
				
			};
			
			((CheckBox)findViewById(R.id.isznbjq)).setOnCheckedChangeListener(jiliang7);
		}
		
		/**
		 * 确定安检等级、故障代码、隐患代码
		 */
		private void DetermineLevel()
		{
 			if(model.f_kitchen.get())
			{
				this.anjianprivious = "B";
			}
			if(model.f_waterheater.get() || model.f_wallhangboiler.get())
			{
				this.anjianprivious = "A";
			}
			if(model.f_issghnrqgd.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C1";
			}
			if(model.f_isbb.get() || model.f_isbg.get() || model.f_isbf.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C2";
			}
			if(model.f_isbtxs.get() || model.f_isgdxs.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C3";
			}
			if(model.f_isbqrlj.get() || model.f_isbhrlj.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C4";
			}
			if(model.f_isrglh.get() || model.f_isrgcc.get() || model.f_isrgcqdmc.get() || model.f_isrgwgk.get() || model.f_islgwgk.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C5";
			}
			if(model.f_isczlzyshyhy.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C6";
			}
			if(model.f_issykfscf.get() || model.f_iscfdfzw.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C7";
			}
			if(model.f_isgbcfyt.get() || model.f_isgbyqxz.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C8";
			}
			if(model.f_issymbkj.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C9";
			}
			if(model.f_isczdxcr.get() || model.f_isxyswlm.get() || model.f_iszjxysslm.get() || model.f_isrsqxysslm.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C10";
			}
			if(model.f_issjrsq.get() || model.f_issjbgl.get() || model.f_isyjf.get() || model.f_issjrst.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C11";
			}
			if(model.f_isgzzj.get() || model.f_isgzrsq.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C12";
			}
			if(model.f_isrsqwyd.get() || model.f_isbglwyd.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C13";
			}
			if(model.f_isrsqgyyd.get() || model.f_isbglgyyd.get())
			{
				this.anjianprivious = "C";
				this.yinhuan += ", C14";
			}
			if(!this.yinhuan.equals(""))
			{
				this.yinhuan = this.yinhuan.substring(2);
			}
			if(model.f_isjjrh.get() || model.f_isjjqz.get())
			{
				this.anjianprivious = "D";
			}
			if(model.f_isjxsb.get())
			{
				this.guzhang += ", g1";
			}
			if(model.f_iszndq.get())
			{
				this.guzhang += ", g2";
			}
			if(model.f_isznbjq.get())
			{
				this.guzhang += ", g4";
			}
			if(model.f_isddbgf.get())
			{
				this.guzhang += ", g5";
			}
			if(model.f_issqbj.get())
			{
				this.guzhang += ", g6";
			}
			if(model.f_isyjxsgz.get())
			{
				this.guzhang += ", g7";
			}
			if(model.f_isqlbf.get())
			{
				this.guzhang += ", g8";
			}
			if(model.f_iscfck.get())
			{
				this.guzhang += ", g9";
			}
			if(!this.guzhang.equals(""))
			{
				this.guzhang = this.guzhang.substring(2);
			}
		}

		/**
		 * 从页面收集各个字段的值
		 */
		public String SaveToJSONString(boolean saveRepair, boolean upload) {
			JSONObject row = new JSONObject();
			DetermineLevel();
			try {
				row.put("ID", Util.quote(uuid));
				row.put("CHECKPAPER_ID", Util.quote(this.paperId));
				row.put("CHECKPLAN_ID", Util.quote(this.planId));
				row.put("f_securityid", Util.quote(model.f_securityid.get()));
				row.put("f_userid", Util.quote(model.f_userid.get()));
				row.put("f_username", Util.quote(model.f_username.get()));
				row.put("f_huifangtelephone", Util.quote(model.f_huifangtelephone.get()));
				row.put("f_address", Util.quote(model.f_address.get()));
				row.put("f_securitydate", Util.unquote(model.f_securitydate.get()));
				row.put("f_kitchen", Util.unquote(model.f_kitchen.get()));
				row.put("f_kitchenbrand", Util.quote(model.f_kitchenbrand.get()));
				row.put("f_kitchensize", Util.quote(model.f_kitchensize.get(((Spinner)findViewById(R.id.kitchensize)).getSelectedItemPosition())));
				row.put("f_waterheater", Util.unquote(model.f_waterheater.get()));
				row.put("f_waterheaterbrand", Util.quote(model.f_waterheaterbrand.get()));
				row.put("f_heatersize", Util.quote(model.f_heatersize.get()));
				row.put("f_wallhangboiler", Util.unquote(model.f_wallhangboiler.get()));
				row.put("f_wallhangboilerbrand", Util.quote(model.f_wallhangboilerbrand.get()));
				row.put("f_handboilersize", Util.quote(model.f_handboilersize.get()));
				row.put("f_metertype", Util.quote(model.f_metertype.get()));
				row.put("f_aroundmeter", Util.quote(model.f_aroundmeter.get()));
				row.put("f_meternumber", Util.quote(model.f_meternumber.get()));
				row.put("f_gasmeterstyle", Util.quote(model.f_gasmeterstyle.get()));
				row.put("f_gaswatchbrand", Util.quote(model.f_gaswatchbrand.get()));
				row.put("f_isbtlq", Util.unquote(model.f_isbtlq.get()));
				row.put("f_isfmlq", Util.unquote(model.f_isfmlq.get()));
				row.put("f_isgdjkclq", Util.unquote(model.f_isgdjkclq.get()));
				row.put("f_ishnczlqxx", Util.unquote(model.f_ishnczlqxx.get()));
				row.put("f_issghnrqgd", Util.unquote(model.f_issghnrqgd.get()));
				row.put("f_issqgg", Util.unquote(model.f_issqgg.get()));
				row.put("f_isbb", Util.unquote(model.f_isbb.get()));
				row.put("f_isbg", Util.unquote(model.f_isbg.get()));
				row.put("f_isbf", Util.unquote(model.f_isbf.get()));
				row.put("f_isktfk", Util.unquote(model.f_isktfk.get()));
				row.put("f_isbtxs", Util.unquote(model.f_isbtxs.get()));
				row.put("f_isgdxs", Util.unquote(model.f_isgdxs.get()));
				row.put("f_isbx", Util.unquote(model.f_isbx.get()));
				row.put("f_isbqrlj", Util.unquote(model.f_isbqrlj.get()));
				row.put("f_isbhrlj", Util.unquote(model.f_isbhrlj.get()));
				row.put("f_islxwgs", Util.unquote(model.f_islxwgs.get()));
				row.put("f_isrglh", Util.unquote(model.f_isrglh.get()));
				row.put("f_isrgcc", Util.unquote(model.f_isrgcc.get()));
				row.put("f_isrgcqdmc", Util.unquote(model.f_isrgcqdmc.get()));
				row.put("f_isrgwgk", Util.unquote(model.f_isrgwgk.get()));
				row.put("f_islgwgk", Util.unquote(model.f_islgwgk.get()));
				row.put("f_isghrg", Util.unquote(model.f_isghrg.get()));
				row.put("f_isydrqj", Util.unquote(model.f_isydrqj.get()));
				row.put("f_iszxzg1", Util.unquote(model.f_iszxzg1.get()));
				row.put("f_isrggkyff", Util.unquote(model.f_isrggkyff.get()));
				row.put("f_issjrsq", Util.unquote(model.f_issjrsq.get()));
				row.put("f_issjbgl", Util.unquote(model.f_issjbgl.get()));
				row.put("f_isyjf", Util.unquote(model.f_isyjf.get()));
				row.put("f_issjrst", Util.unquote(model.f_issjrst.get()));
				row.put("f_islxwgs1", Util.unquote(model.f_islxwgs1.get()));
				row.put("f_isgzzj", Util.unquote(model.f_isgzzj.get()));
				row.put("f_isgzrsq", Util.unquote(model.f_isgzrsq.get()));
				row.put("f_isccghrqj", Util.unquote(model.f_isccghrqj.get()));
				row.put("f_isrsqwyd", Util.unquote(model.f_isrsqwyd.get()));
				row.put("f_isrsqgyyd", Util.unquote(model.f_isrsqgyyd.get()));
				row.put("f_iszxzg2", Util.unquote(model.f_iszxzg2.get()));
				row.put("f_isbglwyd", Util.unquote(model.f_isbglwyd.get()));
				row.put("f_isbglgyyd", Util.unquote(model.f_isbglgyyd.get()));
				row.put("f_iszxzg3", Util.unquote(model.f_iszxzg3.get()));
				row.put("f_isczlzyshyhy", Util.unquote(model.f_isczlzyshyhy.get()));
				row.put("f_isycqthy", Util.unquote(model.f_isycqthy.get()));
				row.put("f_issykfscf", Util.unquote(model.f_issykfscf.get()));
				row.put("f_iscfdfzw", Util.unquote(model.f_iscfdfzw.get()));
				row.put("f_isjiageduan", Util.unquote(model.f_isjiageduan.get()));
				row.put("f_iszxzg6", Util.unquote(model.f_iszxzg6.get()));
				row.put("f_isgbcfyt", Util.unquote(model.f_isgbcfyt.get()));
				row.put("f_isgbyqxz", Util.unquote(model.f_isgbyqxz.get()));
				row.put("f_ishfcfyt", Util.unquote(model.f_ishfcfyt.get()));
				row.put("f_isdgsdtsq", Util.unquote(model.f_isdgsdtsq.get()));
				row.put("f_issymbkj", Util.unquote(model.f_issymbkj.get()));
				row.put("f_isxyswlm", Util.unquote(model.f_isxyswlm.get()));
				row.put("f_isczdxcr", Util.unquote(model.f_isczdxcr.get()));
				row.put("f_isqbzrtf", Util.unquote(model.f_isqbzrtf.get()));
				row.put("f_iszxzg4", Util.unquote(model.f_iszxzg4.get()));
				row.put("f_iszjxysslm", Util.unquote(model.f_iszjxysslm.get()));
				row.put("f_isrsqxysslm", Util.unquote(model.f_isrsqxysslm.get()));
				row.put("f_iszxzg5", Util.unquote(model.f_iszxzg5.get()));
				row.put("f_islxwgs2", Util.unquote(model.f_islxwgs2.get()));
				row.put("f_isjxsb", Util.unquote(model.f_isjxsb.get()));
				row.put("f_issqbj", Util.unquote(model.f_issqbj.get()));
				row.put("f_isqlbf", Util.unquote(model.f_isqlbf.get()));
				row.put("f_iscfck", Util.unquote(model.f_iscfck.get()));
				row.put("f_isddbgf", Util.unquote(model.f_isddbgf.get()));
				row.put("f_iszndq", Util.unquote(model.f_iszndq.get()));
				row.put("f_isyjxsgz", Util.unquote(model.f_isyjxsgz.get()));
				row.put("f_isznbjq", Util.unquote(model.f_isznbjq.get()));
				row.put("f_isqbczgz", Util.unquote(model.f_isqbczgz.get()));
				row.put("f_qtyh", Util.quote(model.f_qtyh.get()));
				row.put("oughtamount", Util.unquote(model.oughtamount.get()));
				row.put("f_cumulativepurchase", Util.unquote(model.f_cumulativepurchase.get()));
				row.put("f_yejingdushu", Util.unquote(model.f_yejingdushu.get()));
				row.put("lastrecord", Util.unquote(model.lastrecord.get()));
				row.put("lastinputgasnum", Util.unquote(model.lastinputgasnum.get()));
				row.put("oughtfee", Util.unquote(model.oughtfee.get()));
				row.put("f_qiliangcha", Util.unquote(model.f_qiliangcha.get()));
				row.put("f_gaspricetype", Util.quote(model.f_gaspricetype.get()));
				row.put("f_zhyegas", Util.unquote(model.f_zhyegas.get()));
				row.put("mustpaygasfee", Util.unquote(model.mustpaygasfee.get()));
				row.put("mustpaygascount", Util.unquote(model.mustpaygascount.get()));
				row.put("f_gasprice", Util.unquote(model.f_gasprice.get()));
				row.put("f_bczhyegas", Util.unquote(model.f_bczhyegas.get()));
				row.put("yjfee", Util.unquote(model.yjfee.get()));
				row.put("yjgas", Util.unquote(model.yjgas.get()));
				row.put("f_gasmeteraccomodations", Util.unquote(model.f_gasmeteraccomodations.get()));
				row.put("CONDITION", Util.quote("正常"));
				if(((CheckBox)findViewById(R.id.isjjrh)).isChecked())
				{
					row.put("CONDITION", Util.quote("拒入"));
				}
				if(((CheckBox)findViewById(R.id.isjjrh)).isChecked())
				{
					row.put("CONDITION", Util.quote("拒签"));
				}
				row.put("f_isjjrh", Util.unquote(model.f_isjjrh.get()));
				row.put("f_isjjqz", Util.unquote(model.f_isjjqz.get()));
				row.put("f_beizhu", Util.quote(model.f_beizhu.get()));
				row.put("f_isaqcsxc", Util.unquote(model.f_isaqcsxc.get()));
				row.put("f_iszxjc", Util.unquote(model.f_iszxjc.get()));
				row.put("f_isjlyhfzsjxjl", Util.unquote(model.f_isjlyhfzsjxjl.get()));
				row.put("f_isffxczl", Util.unquote(model.f_isffxczl.get()));
				row.put("f_isajyzxjc", Util.unquote(model.f_isajyzxjc.get()));
				if(((CheckBox)findViewById(R.id.ismanyi)).isChecked())
				{
					row.put("f_ismanyi", Util.quote("满意"));
				}
				else if(((CheckBox)findViewById(R.id.isnotmanyi)).isChecked())
				{
					row.put("f_ismanyi", Util.quote("不满意"));
				}
				if(((CheckBox)findViewById(R.id.isfzgd)).isChecked())
				{
					row.put("f_isfzgd", Util.quote("是"));
				}
				else
				{
					row.put("f_isfzgd", Util.quote("否"));
				}
				row.put("f_anjianprivious", Util.quote(this.anjianprivious));
				try
				{
					//计算下次入户日期
					String d = model.f_securitydate.get().toString();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					Date date = (Date)sdf.parse(d);
					Calendar calender = Calendar.getInstance();
					calender.setTime(date);
					calender.add(calender.MONTH, 1);
					this.bcthrq1 = sdf.format(calender.getTime());
					calender.add(calender.MONTH, 5);
					this.bcthrq2 = sdf.format(calender.getTime());
					calender.add(calender.MONTH, 6);
					this.bcthrq3 = sdf.format(calender.getTime());
					calender.add(calender.MONTH, 12);
					this.bcthrq4 = sdf.format(calender.getTime());
				}
				catch(Exception e)
				{
				}
				
				if(this.anjianprivious.equals("A"))
				{
					row.put("f_bcrhrq", Util.quote(this.bcthrq4));
				}
				if(this.anjianprivious.equals("B"))
				{
					row.put("f_bcrhrq", Util.quote(this.bcthrq3));
				}
				if(this.anjianprivious.equals("C"))
				{
					row.put("f_bcrhrq", Util.quote(this.bcthrq2));
				}
				String meter = model.f_gasmeterstyle.get();
				if(meter.equals("机表"))
				{
					row.put("f_bcrhrq", Util.quote(this.bcthrq2));
				}
				if(this.anjianprivious.equals("D"))
				{
					row.put("f_bcrhrq", Util.quote(this.bcthrq1));
				}
				
				row.put("f_yinhuan", Util.quote(this.yinhuan));
				row.put("f_guzhang", Util.quote(this.guzhang));
				row.put("f_reconmegent", Util.quote(model.f_reconmegent.get()));
				row.put("f_opratedate", Util.unquote(model.f_securitydate.get()));
				// 签名
				if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid	+ "_sign.png"))
					row.put("USER_SIGN", Util.quote(uuid + "_sign"));
				// 图片
				SavePics(row);
				return row.toString();
			} catch (JSONException e) {
				Log.d("Check", e.getMessage());
				return null;
			}
		}

		private void SavePics(JSONObject row) throws JSONException {
			if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid  + "_1.jpg"))
				row.put("PHOTO_FIRST", Util.quote(uuid + "_1"));
			if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid + "_2.jpg"))
				row.put("PHOTO_SECOND", Util.quote(uuid + "_2"));
			if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid	+ "_3.jpg"))
				row.put("PHOTO_THIRD", Util.quote(uuid + "_3"));
			if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid	+ "_4.jpg"))
				row.put("PHOTO_FOUTH", Util.quote(uuid + "_4"));
			if (Util.fileExists(Util.getSharedPreference(CheckActivity.this, "FileDir") + uuid	+ "_5.jpg"))
				row.put("PHOTO_FIFTH", Util.quote(uuid + "_5"));
		}

		/**
		 * 从本地数据库读取各个字段并给字段赋值
		 */
		private void ReadFromDB(String id,  String inspectionTable) {
			// 读取该条安检数据，给界面各字段赋值
			// 打开数据库
			try {
				SQLiteDatabase db = openOrCreateDatabase("safecheck.db",Context.MODE_PRIVATE, null);
				Cursor c = db.rawQuery(	"SELECT * FROM " + inspectionTable  + " where id=?",	new String[] { id });
				if(c.moveToNext())
				{
//					// 已发到访不遇卡
//					if (c.getString(c.getColumnIndex("hasNotified")).length() > 0)
//						model.hasNotified.set(true);
//
//					// 到达时间
//					String dt = c.getString(c.getColumnIndex("ARRIVAL_TIME"));
//					model.f_anjianriqi.set(dt.substring(0, 10));
//					model.ArrivalTime.set(dt.substring(dt.length()-8, dt.length()));
//					// 离开时间
//					dt = c.getString(c.getColumnIndex("DEPARTURE_TIME"));
//					String stopAt = dt.substring(dt.length()-8, dt.length());
//					//进入界面而不是解屏时
//					if(inspectionTable.equals("T_INSPECTION"))
//					{
//						((MyDigitalClock)this.findViewById(R.id.digitalClock)).stopAt(stopAt);
//					}
//					if(c.getInt(c.getColumnIndex("f_ruhu".toUpperCase()))==0)
//					{
//						model.f_ruhu.set(false);
//						//if(inspectionTable.equals("T_INSPECTION"))
//						{
//							DisplayPic(c);
//							db.close();
//							//model.createPrecautions(null);
//							return;
//						}
//					}
//					else
//						model.f_ruhu.set(true);
//					if(c.getInt(c.getColumnIndex("f_jujian".toUpperCase()))==1)
//					{
//						model.f_jujian.set(true);
//						//if(inspectionTable.equals("T_INSPECTION"))
//						{
//							DisplayPic(c);
//							db.close();
//							//model.createPrecautions(null);
//							return;
//						}
//					}
//					else
//						model.f_jujian.set(false);
//					// 检查情况
//					if(c.getString(c.getColumnIndex("CONDITION")).equals("正常") && inspectionTable.equals("T_INSPECTION"))
//					{
//						//DisableOtherCondition();
//					}
//					//自闭阀
//					Util.SelectItem(c.getString(c.getColumnIndex("f_zibifa".toUpperCase())), model.f_zibifa, ((Spinner)findViewById(R.id.f_zibifa)));		
//					// 用户评价
//					if (c.getString(c.getColumnIndex("f_kehupingjia".toUpperCase())).equals("满意")) {
//						((RadioButton)findViewById(R.id.FeebackSatisfied)).setChecked(true);
//					} else if (c.getString(c.getColumnIndex("f_kehupingjia".toUpperCase())).equals("基本满意")) {
//						((RadioButton)findViewById(R.id.FeebackOK)).setChecked(true);
//					} else if (c.getString(c.getColumnIndex("f_kehupingjia".toUpperCase())).equals("不满意")) {
//						((RadioButton)findViewById(R.id.FeebackUnsatisfied)).setChecked(true);
//					}

					DisplayPic(c);				
				}
				db.close();
			} catch (Exception e) {
				Log.d("IndoorInspection", e.getMessage());
			}
		}

		private void DisplayPic(Cursor c) {
			try
			{
				// 照片
				if (c.getString(c.getColumnIndex("USER_SIGN")) != null) {
					ImageView signPad = (ImageView) (findViewById(R.id.signPad));
					Util.releaseBitmap(signPad);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("USER_SIGN")) + ".png");
					signPad.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIRST")) != null) {
					Util.releaseBitmap(img1);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FIRST"))
							+ ".jpg");
					img1.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_SECOND")) != null) {
					Util.releaseBitmap(img2);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_SECOND"))
							+ ".jpg");
					img2.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_THIRD")) != null) {
					Util.releaseBitmap(img3);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_THIRD"))
							+ ".jpg");
					img3.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FOUTH")) != null) {
					Util.releaseBitmap(img4);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FOUTH"))
							+ ".jpg");
					img4.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIFTH")) != null) {
					Util.releaseBitmap(img5);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(CheckActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FIFTH"))
							+ ".jpg");
					img5.setImageBitmap(bmp);
				}
			}
			catch(Exception e)
			{
				Toast.makeText(this, "获取图片失败。错误： " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPause() {
			super.onPause();
			Save(SaveToJSONString(true, false), "T_INP" ,  true);
			Util.setSharedPreference(this, "entryDateTime", Util.FormatDate("yyyy-MM-dd HH:mm:ss", entryDateTime.getTime()));
		}

		@Override
		protected void onResume() {
			super.onResume();
			if(Util.IsCached(this, uuid))
			{
				ReadFromDB(uuid, "T_INP");
//				model.GetRepairPerson(uuid, "T_INP");
				String dt = Util.getSharedPreference(this, "entryDateTime");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try
				{
					entryDateTime = formatter.parse(dt);
				}
				catch(Exception e)
				{
					
				}
			}
			else
			{
				if(inspected)
					this.ReadFromDB(uuid,"T_INSPECTION");
				//记录进入时间
				entryDateTime = new Date();			
			}
		}

		@Override
		public void onBackPressed() {
			TextView tv = new TextView(this);
			tv.setText("确认要退出吗？");
			Dialog alertDialog = new AlertDialog.Builder(this).   
					setView(tv).
					setTitle("确认").   
					setIcon(android.R.drawable.ic_dialog_info).
					setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CheckActivity.this.finish();
						}
					}).setNegativeButton("取消", null).
					create();  
			alertDialog.setCancelable(false);
			alertDialog.show();
		}
}
