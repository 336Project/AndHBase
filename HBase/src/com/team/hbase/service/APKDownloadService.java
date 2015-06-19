package com.team.hbase.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.team.hbase.MainActivity;
import com.team.hbase.R;
import com.team.hbase.utils.FileUtil;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;


public class APKDownloadService extends Service{
	
	public static final String KEY_DOWNLOAD_URL="download_url";
	public static final String KEY_VERSION="version";
	public static final String KEY_FILE_NAME="FileName";
	/**
	 * 文件下载目录
	 */
	private static final String DIR="hczd/eloan";
	/**
	 * 通知栏ID
	 */
	private static final int NOTIFICATION_ID=1;
	//private ELOAN_UpdateInfo mUpdateInfo;//更新信息
	private String mDownloadUrl;
	private String mVersion;
	private File mApkFile;//apk文件
	private String mFileName;//sdk保存的文件名
	private NotificationManager mManager;
	private Notification mNotice;
	private Handler mHandler;//进度更新处理器
	private int mProgress;//进度
	
	private DownloadThread mDownloadThread;
	private boolean isStop=true;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mDownloadUrl= intent.getStringExtra(KEY_DOWNLOAD_URL);
		mVersion=intent.getStringExtra(KEY_VERSION);
		String fileName=intent.getStringExtra(KEY_FILE_NAME);
		initNotification();
		initHadnler();
		if(!TextUtils.isEmpty(mDownloadUrl)&&isStop&&mDownloadThread==null){
			mProgress=0;
			if(!TextUtils.isEmpty(fileName)){
				mFileName=fileName;
			}else{
				if(!TextUtils.isEmpty(mVersion)){
					mFileName="hczd_eloan_v"+mVersion+".apk";
				}else{
					mFileName="hczd_eloan_"+System.currentTimeMillis()+".apk";
				}
			}
			mDownloadThread=new DownloadThread();
			isStop=false;
			mDownloadThread.start();
		}else{
			Message msg = new Message();
			msg.what=1;
			mHandler.sendMessage(msg);
		}
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-7 上午10:43:28
	 * @TODO 初始化通知栏
	 */
	@SuppressWarnings("deprecation")
	private void initNotification() {
		mNotice = new Notification(R.drawable.ic_launcher, getText(R.string.app_name), System.currentTimeMillis());
		mNotice.flags = Notification.FLAG_ONGOING_EVENT;
		Intent intent=new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent=PendingIntent.getActivity(this, 0, intent, 0);
		
		RemoteViews contentView=new RemoteViews(getPackageName(), R.layout.notification_download);
		contentView.setTextViewText(R.id.notificationTitle, getText(R.string.app_name));
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		
		mNotice.contentView=contentView;
		mNotice.contentIntent=contentIntent;
		mManager.notify(NOTIFICATION_ID, mNotice);
	}
	/***
	 * 初始化处理器
	 */
	private void initHadnler() {
		mHandler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				if(msg.what==0){
					Bundle bundle = msg.getData();
					String title = bundle.getString("title");
					int progress = bundle.getInt("progress");
					updateNotification(title, progress);
					if(progress>=100){
						installApk(mApkFile);
					}
				}else if(msg.what==1){//文件下载异常
					stopSelf();
					Toast.makeText(getApplicationContext(), "文件下载失败!", Toast.LENGTH_SHORT).show();
					mNotice.contentView.setTextViewText(R.id.notificationPercent, "文件下载失败！");
					mNotice.flags = Notification.FLAG_AUTO_CANCEL;
					mManager.notify(NOTIFICATION_ID, mNotice);
				}
				return true;
			}
		});
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-7 上午10:46:15
	 * @param title
	 * @param progress
	 * @TODO 更新通知栏
	 */
	private void updateNotification(String title,int progress){
		if(progress>=100){
			Intent intent=new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(mApkFile), "application/vnd.android.package-archive");
			PendingIntent contentIntent=PendingIntent.getActivity(this, 0, intent, 0);
			mNotice.contentIntent=contentIntent;
			mNotice.flags = Notification.FLAG_AUTO_CANCEL;
		}
		mNotice.contentView.setTextViewText(R.id.notificationTitle, title);
		mNotice.contentView.setTextViewText(R.id.notificationPercent, progress + "%");
		mNotice.contentView.setProgressBar(R.id.notificationProgress, 100, progress, false);
		mManager.notify(NOTIFICATION_ID, mNotice);
	}
	/**
	 * 
	 * @author 李晓伟
	 * @Create_date 2014-7-8 上午10:33:53
	 * @TODO apk下载线程
	 */
	private class DownloadThread extends Thread{
		
		@SuppressLint("SimpleDateFormat")
		@Override
		public void run() {
			if(!isStop){
				try {
					FileUtil.getInstance().createSDDir(DIR);
					mApkFile=FileUtil.getInstance().createFileInSDCard(DIR, mFileName);
					URL url=new URL(mDownloadUrl);
					URLConnection conn=url.openConnection();
					conn.setConnectTimeout(5000);
					int len=conn.getContentLength();
					long fileLen=mApkFile.length();
					/**2014-10-15 李晓伟 如果已经存在安装文件，则直接安装，无需下载*/
					if(fileLen==len){
						mManager.cancel(NOTIFICATION_ID);
						installApk(mApkFile);
						isStop=true;
						return;
					}
					InputStream is=conn.getInputStream();
					BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(mApkFile,false));
					byte buffer[] = new byte[1024];
					int temp;
					int count=0;
					//long currTime=0;
					int downcount=0;
					while((temp = is.read(buffer)) != -1){
						bos.write(buffer, 0, temp);
						count+=temp;
						mProgress=(int) (((float) count / len) * 100);
						String title="下载中...";
						if(mProgress>=100){
							title="下载完成，点击安装！";
						}
						//0.1s刷新一次通知栏
						//每增长3%刷新一次
						if(mProgress-downcount>=3||mProgress>=100){
						//if(System.currentTimeMillis()-currTime>300||mProgress>=100){
							//currTime=System.currentTimeMillis();
							downcount=mProgress;
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("title", title);
							bundle.putInt("progress", mProgress);
							msg.setData(bundle);
							msg.what=0;
							mHandler.sendMessage(msg);
						}
					}
					bos.flush();
					bos.close();
					is.close();
				} catch (Exception e) {
					Message msg = new Message();
					msg.what=1;
					mHandler.sendMessage(msg);
					e.printStackTrace();
				}
				isStop=true;
			}
		}
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-8 上午10:39:39
	 * @param file
	 * @TODO 安装apk
	 */
	protected void installApk(File file) {
		stopSelf();
		if(file!=null){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//执行的数据类型
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			startActivity(intent);
		}
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		mManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isStop=true;
		mDownloadThread=null;
	}
}
