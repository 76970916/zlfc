package com.zlfcapp.poster.utils;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.zlfcapp.poster.App;


public class Tools {
	/**one hour in ms*/
	private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
	/**one minute in ms*/
	private static final int ONE_MIN = 1 * 60 * 1000;
	/**one second in ms*/
	private static final int ONE_SECOND = 1 * 1000;

	public static String getIMEI(Context paramContext) {
		String str1 = App.getApp().getString("imei", "");
//		try {
//			str1 = ((TelephonyManager) paramContext.getApplicationContext()
//					.getSystemService("phone")).getDeviceId();
//			if ((str1 != null) && (str1.length() > 0)) {
//				App.getApp().saveString("imei", str1);
//			}
//		} catch (Exception localException) {
//			localException.printStackTrace();
//			str1 = "";
//		}
	return str1;
	}

	public static String md5(String paramString) {
		Object localObject;
		StringBuffer localStringBuffer;
		int m;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(paramString.getBytes("UTF-8"));
			byte[] arrayOfByte = messageDigest.digest();
			localStringBuffer = new StringBuffer();
			m = 0;
			for (int i = 0; i < arrayOfByte.length; i++) {
				if (Integer.toHexString(0xFF & arrayOfByte[i]).length() == 1)
					localStringBuffer.append("0").append(
							Integer.toHexString(0xFF & arrayOfByte[i]));
				else
					localStringBuffer.append(Integer
							.toHexString(0xFF & arrayOfByte[i]));

			}
			localObject = localStringBuffer.toString().toUpperCase();
			return localObject.toString().toLowerCase();
		} catch (Exception localException) {

		}
		return "";
	}

	/**HH:mm:ss*/
	public static String formatTime(long ms) {
		StringBuilder sb = new StringBuilder();
		int hour = (int) (ms / ONE_HOUR);
		int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
		int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
		if (hour == 0) {
//			sb.append("00:");
		} else if (hour < 10) {
			sb.append("0").append(hour).append(":");
		} else {
			sb.append(hour).append(":");
		}
		if (min == 0) {
			sb.append("00:");
		} else if (min < 10) {
			sb.append("0").append(min).append(":");
		} else {
			sb.append(min).append(":");
		}
		if (sec == 0) {
			sb.append("00");
		} else if (sec < 10) {
			sb.append("0").append(sec);
		} else {
			sb.append(sec);
		}
		return sb.toString();
	}
	/**-1表示可以在起始时间之前0表示在结束时间之后1表示在起始时间跟结束时间之间*/
	public static int IsInTime(String start_time,String end_time){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String start=df.format(new Date())+" "+start_time;
		String end =df.format(new Date())+" "+end_time;
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date dt=new Date();
			Date dt1 = df2.parse(start);
			Date dt2 = df2.parse(end);
			if (dt.getTime()< dt1.getTime()) {
				return -1;
			} else if (dt.getTime() < dt2.getTime()) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	public static String ParesTimeToNow(long time){
		long dtime=System.currentTimeMillis()/1000-time/1000;
		if(dtime>0){
			if(dtime<=3600){
				return  dtime/60+"分钟前";
			}else if(dtime<=3600*24){
				return  dtime/3600+"小时前";
			}else{
				return  dtime/(3600*24)+"天前";
			}

		}
		return "1小时前";
	}
}
