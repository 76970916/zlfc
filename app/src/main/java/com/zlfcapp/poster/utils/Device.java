package com.zlfcapp.poster.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.zlfcapp.poster.App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

public class Device {
	public static String imei;

	public static void init(Context context) {
		imei = getUniqueId(context);
	}

	public static String getImei(Context context) {
		try {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED) {
				String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				return deviceId;
			}

		}
		catch (Exception paramContext)
		{

			paramContext.printStackTrace();
		}
		return "";
	}
	public static String getMac(Context context) {

		String strMac = null;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			strMac = getLocalMacAddressFromWifiInfo(context);
			return strMac;
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			strMac = getMacAddress(context);
			return strMac;
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (!TextUtils.isEmpty(getMacAddress())) {
				strMac = getMacAddress();
				return strMac;
			} else if (!TextUtils.isEmpty(getMachineHardwareAddress())) {
				strMac = getMachineHardwareAddress();
				return strMac;
			} else {
				strMac = getLocalMacAddressFromBusybox();
				return strMac;
			}
		}

		return randomMac4Qemu();
	}

	public static String getUniqueId(Context context){
		String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		String id = androidID + Build.SERIAL;
		String md5= App.getApp().getString("uniqueid","");
		if(!TextUtils.isEmpty(md5)){
			return md5;
		}
		try {
			md5=Tools.md5(id);
			App.getApp().saveString("uniqueid",md5);
			return md5;
		} catch (Exception e) {
			md5=Tools.md5(Math.random()+"");
			App.getApp().saveString("uniqueid",md5);
			return md5;
		}
	}
	/**
	 * 获取手机型号
	 *
	 * @return  手机型号
	 */
	public static String getHis() {
		String tm=System.currentTimeMillis()/1000+"";
		String tms= App.getApp().getString("launchtime","");
		if(!TextUtils.isEmpty(tms)){
			try{
				String[] args = tms.split(",");
				if(args.length==2){
					tms=args[0]+","+args[1];
					return tms;
				}
			}catch (Exception e){

			}
		}
		tms=tm+"."+tm;
		return tms;
	}

	/**
	 * 获取手机型号
	 *
	 * @return  手机型号
	 */
	public static String getSystemModel() {
		return Build.MODEL;
	}

	/**
	 * 获取手机厂商
	 *
	 * @return  手机厂商
	 */
	public static String getDeviceBrand() {
		return Build.BRAND;
	}
	public static String randomMac4Qemu() {
		String macstr="";
		Random random = new Random();
		String[] mac = {
				String.format("%02x", 0x52),
				String.format("%02x", 0x54),
				String.format("%02x", 0x00),
				String.format("%02x", random.nextInt(0xff)),
				String.format("%02x", random.nextInt(0xff)),
				String.format("%02x", random.nextInt(0xff))
		};
		for (int i=0;i<mac.length;i++){
			if(i==0){
				macstr=mac[i];
			}else{
				macstr=macstr+":"+mac[i];
			}
		}
		return macstr;
	}
	/**
	 * 根据wifi信息获取本地mac
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddressFromWifiInfo(Context context){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo winfo = wifi.getConnectionInfo();
		String mac =  winfo.getMacAddress();
		return mac;
	}

	/**
	 * android 6.0及以上、7.0以下 获取mac地址
	 *
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {

		// 如果是6.0以下，直接通过wifimanager获取
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			String macAddress0 = getMacAddress0(context);
			if (!TextUtils.isEmpty(macAddress0)) {
				return macAddress0;
			}
		}

		String str = "";
		String macSerial = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str; ) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (Exception ex) {
		}
		if (macSerial == null || "".equals(macSerial)) {
			try {
				return loadFileAsString("/sys/class/net/eth0/address")
						.toUpperCase().substring(0, 17);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return macSerial;
	}

	private static String getMacAddress0(Context context) {
		if (isAccessWifiStateAuthorized(context)) {
			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = null;
			try {
				wifiInfo = wifiMgr.getConnectionInfo();
				return wifiInfo.getMacAddress();
			} catch (Exception e) {
			}

		}
		return "";

	}

	/**
	 * Check whether accessing wifi state is permitted
	 *
	 * @param context
	 * @return
	 */
	private static boolean isAccessWifiStateAuthorized(Context context) {
		if (PackageManager.PERMISSION_GRANTED == context
				.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
			return true;
		} else
			return false;
	}

	private static String loadFileAsString(String fileName) throws Exception {
		FileReader reader = new FileReader(fileName);
		String text = loadReaderAsString(reader);
		reader.close();
		return text;
	}

	private static String loadReaderAsString(Reader reader) throws Exception {
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[4096];
		int readLength = reader.read(buffer);
		while (readLength >= 0) {
			builder.append(buffer, 0, readLength);
			readLength = reader.read(buffer);
		}
		return builder.toString();
	}
	/**
	 * 根据IP地址获取MAC地址
	 *
	 * @return
	 */
	public static String getMacAddress() {
		String strMacAddr = null;
		try {
			// 获得IpD地址
			InetAddress ip = getLocalInetAddress();
			byte[] b = NetworkInterface.getByInetAddress(ip)
					.getHardwareAddress();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				if (i != 0) {
					buffer.append(':');
				}
				String str = Integer.toHexString(b[i] & 0xFF);
				buffer.append(str.length() == 1 ? 0 + str : str);
			}
			strMacAddr = buffer.toString().toUpperCase();
		} catch (Exception e) {

		}

		return strMacAddr;
	}

	/**
	 * 获取移动设备本地IP
	 *
	 * @return
	 */
	private static InetAddress getLocalInetAddress() {
		InetAddress ip = null;
		try {
			// 列举
			Enumeration<NetworkInterface> en_netInterface = NetworkInterface
					.getNetworkInterfaces();
			while (en_netInterface.hasMoreElements()) {// 是否还有元素
				NetworkInterface ni = (NetworkInterface) en_netInterface
						.nextElement();// 得到下一个元素
				Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
				while (en_ip.hasMoreElements()) {
					ip = en_ip.nextElement();
					if (!ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1)
						break;
					else
						ip = null;
				}

				if (ip != null) {
					break;
				}
			}
		} catch (SocketException e) {

			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 获取本地IP
	 *
	 * @return
	 */
	private static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	/**
	 * android 7.0及以上 （2）扫描各个网络接口获取mac地址
	 *
	 */
	/**
	 * 获取设备HardwareAddress地址
	 *
	 * @return
	 */
	public static String getMachineHardwareAddress() {
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		String hardWareAddress = null;
		NetworkInterface iF = null;
		if (interfaces == null) {
			return null;
		}
		while (interfaces.hasMoreElements()) {
			iF = interfaces.nextElement();
			try {
				hardWareAddress = bytesToString(iF.getHardwareAddress());
				if (hardWareAddress != null)
					break;
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		return hardWareAddress;
	}

	/***
	 * byte转为String
	 *
	 * @param bytes
	 * @return
	 */
	private static String bytesToString(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		for (byte b : bytes) {
			buf.append(String.format("%02X:", b));
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}
	/**
	 * 根据busybox获取本地Mac
	 *
	 * @return
	 */
	public static String getLocalMacAddressFromBusybox() {
		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig", "HWaddr");
		// 如果返回的result == null，则说明网络不可取
		if (result == null) {
			return "网络异常";
		}
		// 对该行数据进行解析
		// 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
		if (result.length() > 0 && result.contains("HWaddr") == true) {
			Mac = result.substring(result.indexOf("HWaddr") + 6,
					result.length() - 1);
			result = Mac;
		}
		return result;
	}

	private static String callCmd(String cmd, String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			while ((line = br.readLine()) != null
					&& line.contains(filter) == false) {
				result += line;
			}

			result = line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

