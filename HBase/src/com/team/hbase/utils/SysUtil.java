package com.team.hbase.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class SysUtil {
	
	/**
	 * 
	 * @author 李晓伟
	 * 2014-8-26 下午4:07:19
	 * @return
	 * @TODO 判断网络连接是否正常
	 */
	public static boolean isNetworkConnected(final Context c){
		ConnectivityManager manager=(ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();
		if(info!=null&&info.isAvailable()){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @author 李晓伟
	 * 2014-8-30 下午2:29:01
	 * @param d
	 * @return
	 * @TODO 四舍五入保留两位
	 */
	public static String format(double d){
		DecimalFormat df=new DecimalFormat("##,##0.00");
		BigDecimal b = new BigDecimal(d);
		d=b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return df.format(d);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-8-30 下午2:29:01
	 * @param s
	 * @return
	 * @TODO 四舍五入保留两位
	 */
	public static String format(String s){
		try{
			double d=Double.valueOf(s);
			s=format(d);
		}catch(Exception e){}
		return s;
	}
	/**
	 * 金额格式化
	 * @param s 金额
	 * @param len 小数位数
	 * @return 格式后的金额
	 */
	public static String format(String s, int len) {
	    if (s == null || s.length() < 1) {
	        return "";
	    }
	    NumberFormat formater = null;
	    double num = Double.parseDouble(s);
	    if (len == 0) {
	        formater = new DecimalFormat("###,###");
	 
	    } else {
	        StringBuffer buff = new StringBuffer();
	        buff.append("###,###.");
	        for (int i = 0; i < len; i++) {
	            buff.append("0");
	        }
	        formater = new DecimalFormat(buff.toString());
	    }
	    return formater.format(num);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-23 上午10:38:46
	 * @param num
	 * @return
	 * @TODO 手机号码校验（校验第一位是否为1，是否是11位数）
	 */
	public static boolean isPhoneNumber(String num){
		if(num.startsWith("1",0)&&num.length()==11){
			return true;
		}
		return false;
	}
}
