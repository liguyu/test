package com.aofeng.wellbeing.service;

import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class GeoService extends AbstractService implements LocationListener{
    public static final int MSG_FROM_ACTIVITY_MUTE = 0;
    public static final int MSG_TO_ACTIVITY_ALARM = 1;
    public static final int MSG_TO_ACTIVITY_NETWORK_ERROR = 2;
    public static final int TIME_INTERVAL = 2;
	Thread th;
	LocationManager locationManager;
	Location location = new Location(android.location.LocationManager.GPS_PROVIDER);
	int preMinute;
	int lastMinute;

	@Override
	public void onStartService() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
        	Toast.makeText(this, "请打开GPS以获取精准的信息位置。", Toast.LENGTH_LONG).show();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, this);
        lastMinute = new Date().getMinutes();
        //normalize
        lastMinute = (lastMinute + (GeoService.TIME_INTERVAL - lastMinute % GeoService.TIME_INTERVAL)) % 60;
        preMinute = lastMinute;
		th = new Thread(new Runnable() {	
			@Override
			public void run() {
				onTimerTick();
			}
		});
		th.start();
	}

	@Override
	public void onStopService() {
	        Log.i("Location service", "Service Stopped.");
	}

    private void onTimerTick() {
    	for(;;)
    	{
	        try {
	    	        Log.i("Location service", "Timely report...");
	    	        Thread.sleep(20*1000);
//			        	Message msg = Message.obtain(null, AlarmService.MSG_TO_ACTIVITY_ALARM, -1, -1);
//			        	this.send(msg);
	    	        Upload();
	        	}
	        catch (Exception t) { 
	            Log.e("TimerTick", "Timer Tick Failed.", t);            
	        }
        } 
    }

 
	private void Upload() {
		int m = new Date().getMinutes();
		if(lastMinute < preMinute)
		{
			if(m < 60)
			{
				if(m>preMinute && m < lastMinute + 60)
					return;
			}
			else
			{
				if(m+60 > preMinute && m<lastMinute)
					return;
			}
		}
		else
		{
			if((m>=preMinute) && (m <lastMinute))
				return;
		}
		if(m>=lastMinute)
		{
			preMinute = lastMinute;
			lastMinute = (lastMinute + GeoService.TIME_INTERVAL) % 60;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				HttpPost httpPost = new HttpPost(Vault.DB_URL + "t_geolocation");
				JSONObject row = new JSONObject();
				synchronized(location)
				{
					row.put("userid", Util.getSharedPreference(GeoService.this, Vault.USER_ID));
					row.put("username", Util.getSharedPreference(GeoService.this, Vault.USER_NAME));
					row.put("provider", location.getProvider());
					row.put("longitude", location.getLongitude()+"");
					row.put("latitude", location.getLatitude()+"");
					row.put("altitude", location.getAltitude()+"");
					row.put("updateTime", Util.FormatDate("yyyy-MM-dd HH:mm:ss", location.getTime()));
					row.put("uploadTime", Util.FormatDate("yyyy-MM-dd HH:", new Date().getTime()) + m + ":00");
				}
				httpPost.setEntity(new StringEntity(row.toString()));
				HttpResponse httpResponse = httpclient.execute(httpPost);
				HttpEntity entity = httpResponse.getEntity();
				String info = EntityUtils.toString(entity);
				if(!info.equals("ok"))
					Log.i("TimerTick", "Upload Failed.");     	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {	
		//deep clone
		synchronized(this.location)
		{
			this.location.setLatitude(location.getLatitude());
			this.location.setLongitude(location.getLongitude());
			this.location.setProvider(location.getProvider());
			this.location.setTime(location.getTime());
			this.location.setAltitude(location.getAltitude());
		}
	}

	@Override
	public void onProviderDisabled(String provider) {	
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {	
	}

	@Override
	public void onReceiveMessage(Message msg) {
	}    
}


