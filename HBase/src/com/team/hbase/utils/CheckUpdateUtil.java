package com.team.hbase.utils;



import com.team.hbase.access.UpdateAccess;
import com.team.hbase.access.I.HRequestCallback;
import com.team.hbase.model.UpdateInfo;
import com.team.hbase.service.APKDownloadService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-2-3 上午11:58:14
 * @Version  
 * @TODO 检测更新工具
 */
public class CheckUpdateUtil {
	public static final String UPDATE_SETTING="update_setting";
	private static CheckUpdateUtil instance;
	private UpdateCallback updateCallback;
	private static Context mContext;
	private boolean isNewVersion=false;//是否有新版本
	
	public static CheckUpdateUtil getInstance(Context context){
		mContext=context;
		if(instance==null){
			instance= new CheckUpdateUtil();
		}
		return instance;
	}
	private CheckUpdateUtil(){
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-10 下午4:40:08
	 * @param isShow 是否显示提示(false:静默检测)
	 * @TODO 
	 */
	public void check(final boolean isShow) {
		HRequestCallback<UpdateInfo> requestCallback=new HRequestCallback<UpdateInfo>() {
			@Override
			public UpdateInfo parseJson(String jsonStr) {
				
				return (UpdateInfo) JSONParse.jsonToBean(jsonStr, UpdateInfo.class);
			}
			
			@Override
			public void onSuccess(UpdateInfo updateInfo) {
				setNewVersion(false);
				if(isShow){
					if(updateInfo==null){
						Toast.makeText(mContext, "抱歉，更新发生异常！", Toast.LENGTH_SHORT).show();
					}else if(updateInfo.getRemark()==null||updateInfo.getRemark().equals("")){
						setNewVersion(true);
						showDownloadDialog(0,updateInfo);
					}else{
						Toast.makeText(mContext, "当前已是最新版本", Toast.LENGTH_SHORT).show();
					}
				}else{
					if(updateInfo==null){
					}else if(updateInfo.getRemark()==null||updateInfo.getRemark().equals("")){
						SharedPreferences sp=mContext.getSharedPreferences(CheckUpdateUtil.UPDATE_SETTING, 0);
						setNewVersion(true);
						if(sp.getBoolean("is_alert", true)||updateInfo.getForce_flag().equals("是")){
							showDownloadDialog(1,updateInfo);
						}
					}else{
					}
				}
				if(updateCallback!=null){
					updateCallback.onResult(isNewVersion());
				}
			}
		};
		UpdateAccess access=new UpdateAccess(mContext, requestCallback);
		access.setIsShow(isShow);
		access.execute(getVersionName(mContext));
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-2-2 下午6:06:40
	 * @param c
	 * @return
	 * @TODO 获取版本号
	 */
	public String getVersionName(final Context c){
		try {
			PackageManager packageManager = c.getPackageManager();
	        PackageInfo packInfo = packageManager.getPackageInfo(c.getPackageName(),0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        return "";
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-2-3 上午9:13:06
	 * @param context
	 * @return
	 * @TODO 检测网络
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-7 上午9:27:08
	 * @TODO 提示更新窗口
	 */
	public void showDownloadDialog(int type, final UpdateInfo updateInfo){
		if(updateInfo==null||updateInfo.isEmpty()){
			Toast.makeText(mContext, "抱歉，更新发生异常！", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder=new Builder(mContext);
		builder.setCancelable(false);
		StringBuffer sb=new StringBuffer();
		sb.append("更新内容:\n");
		if(updateInfo.getForce_flag().equals("否")){
			builder.setTitle("软件有新版本(v"+updateInfo.getVersion()+")，是否更新？");
		}else{
			builder.setTitle("软件有重要更新(v"+updateInfo.getVersion()+")！取消更新将不能使用系统，是否更新？");
		}
		sb.append(updateInfo.getUpdateContent());
		builder.setMessage(sb.toString());
		if(type!=0){
			if(!updateInfo.getForce_flag().equals("是")){
				builder.setNeutralButton("不再提醒", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SharedPreferences sp=mContext.getSharedPreferences(UPDATE_SETTING, 0);
						sp.edit().putBoolean("is_alert", false).commit();
					}
				});
			}
		}
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//开始下载
				Intent downloadIntent = new Intent(mContext,APKDownloadService.class);
				downloadIntent.putExtra(APKDownloadService.KEY_DOWNLOAD_URL, updateInfo.getDownloadUrl());
				downloadIntent.putExtra(APKDownloadService.KEY_VERSION, updateInfo.getVersion());
				mContext.startService(downloadIntent);
				if(updateInfo.getForce_flag().equals("是")){
					AppManager.getInstance().ExitApp();
					System.exit(0);
				}
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(updateInfo.getForce_flag().equals("是")){
					AppManager.getInstance().ExitApp();
					System.exit(0);
				}
			}
		});
		AlertDialog dialog=builder.create();
		builder.show();
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = (int) (dm.heightPixels*(0.8));  // 获取屏幕的4/5大小
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, width);
	}
	
	public interface UpdateCallback{
		void onResult(boolean isNewVersion);
	}

	public UpdateCallback getUpdateCallback() {
		return updateCallback;
	}

	public void setUpdateCallback(UpdateCallback updateCallback) {
		this.updateCallback = updateCallback;
	}
	public boolean isNewVersion() {
		return isNewVersion;
	}
	public void setNewVersion(boolean isNewVersion) {
		this.isNewVersion = isNewVersion;
	}
}
