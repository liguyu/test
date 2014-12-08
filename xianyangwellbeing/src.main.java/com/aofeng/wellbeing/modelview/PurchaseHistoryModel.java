package com.aofeng.wellbeing.modelview;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.StringObservable;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;
import com.aofeng.wellbeing.model.PurchaseRowModel;

public class PurchaseHistoryModel {
	public Activity mContext;

	public Activity getActivity() {
		return mContext;
	}

	public PurchaseHistoryModel(Activity context) {
		this.mContext = context;
	}

	public StringObservable SUM = new StringObservable("");
	public StringObservable COUNT = new StringObservable("");
	
	// �б�
	public ArrayListObservable<PurchaseRowModel> PurchaseList = new ArrayListObservable<PurchaseRowModel>(
			PurchaseRowModel.class);

	public void listPurchases(final String card_id) {
		// �ӷ�������ȡ�ƻ��б�
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Vault.TUNNEL_URL
							+ URLEncoder.encode("from T_IC_SELLGAS t where t.CARD_ID='" + card_id+ "' order by t.OPERATE_DATE desc, t.OPERATE_TIME desc", "UTF8")
									.replace("+", "%20");
					HttpGet getMethod = new HttpGet(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(getMethod);

					int code = response.getStatusLine().getStatusCode();

					// �����������
					if (code == 200) {
						String strResult = EntityUtils.toString(response
								.getEntity(),"UTF8");
						Message msg = new Message();
						msg.obj = strResult;
						msg.what = 1;
						listHandler.sendMessage(msg);
					}
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;
					listHandler.sendMessage(msg);
				}
			}
		});
		th.start();
	}

	// ��ȡ�����б��Ĵ������
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);

				try {
					String text = (String) msg.obj;
					JSONArray array = new JSONArray(text);
					for(int i=0; i<array.length(); i++)
					{
						JSONObject json = array.getJSONObject(i);
						PurchaseRowModel row = new PurchaseRowModel();
						row.CARD_ID.set(json.getString("CARD_ID"));
						row.OPERATE_DATE.set(Util.FormatDate("yyyy-MM-dd", json.getLong("OPERATE_DATE")));
						row.SELLGAS_GAS.set(json.getString("SELLGAS_GAS"));
						row.PRICE.set(json.getString("PRICE"));
						row.MONEY.set(json.getString("MONEY"));
						PurchaseList.add(row);
					}
					COUNT.set(array.length()+"");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				Toast.makeText(mContext, "����������������Ա��ϵ��", Toast.LENGTH_LONG)
						.show();
			}
		}
	};
	
}
