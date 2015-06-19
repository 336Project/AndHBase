package com.team.hbase.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;



import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;


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
	/**
	 * 
	 * 2015-4-8 下午12:00:37
	 * @param context
	 * @param uri
	 * @return
	 * @TODO 根据uri获取图片路径
	 */
	public static String getRealFilePath( final Context context, final Uri uri ) {
	    if ( null == uri ) return null;
	    String filePath=null;
	    try{
		    ContentResolver cr = context.getContentResolver();
		    InputStream is=cr.openInputStream(uri);
		    Bitmap bitmap = BitmapFactory.decodeStream(is, null, null);
			is.close();
			byte[] datas=extractThumbnail(bitmap);
			if(datas!=null){
				File file=FileUtil.getInstance().createFileInSDCard("hbase/image", System.currentTimeMillis()+".jpg");
				FileOutputStream fileOutputStream=new FileOutputStream(file);
				fileOutputStream.write(datas);
				fileOutputStream.flush();
				fileOutputStream.close();
				filePath=file.getAbsolutePath();
			}
	    }catch(Exception e){
	    	
	    }
	    return filePath;
	}
	
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-20 下午3:22:49
	 * @param bitmap
	 * @return
	 * @throws Exception
	 * @TODO 生成缩略图
	 */
	public static byte[] extractThumbnail(Bitmap bitmap) throws Exception{
		float scale=calScale(bitmap);
		bitmap=ThumbnailUtils.extractThumbnail(bitmap, (int)(bitmap.getWidth()/scale), (int)(bitmap.getHeight()/scale), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 90;
        while (baos.toByteArray().length>1024*1024&&options>0) {  //循环判断如果压缩后图片是否大于1M,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10
        }
        bitmap.recycle();
        bitmap=null;
        baos.flush();
		byte[] resource =baos.toByteArray();
		baos.close();
		return resource;
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-20 上午10:13:18
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 * @TODO 根据宽高获取缩放比例
	 */
	private static float calScale(Bitmap bitmap){
		int width=480,height=800;//一般分辨率480*800
		float w = bitmap.getWidth(); 
		float h = bitmap.getHeight();
		if(w>=1920||h>=1920){//1920*1080
			width=1080;
			height=1920;
		}else if(w>=1280||h>=1280){//720*1280
			width=720;
			height=1280;
		}
		float be = 1.0f;//be=1表示不缩放
	    if (w > h && w > width) {//如果宽度大的话根据宽度固定大小缩放  
	        be = (float) (w / width);  
	    } else if (w < h && h > height) {//如果高度高的话根据宽度固定大小缩放  
	        be = (float) (h / height);  
	    }  
	    if (be <= 0)
	        be = 1; 
		return be;
	}
}
