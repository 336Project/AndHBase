package com.team.hbase.access;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtilsHC4;

import com.team.hbase.access.I.HRequestCallback;
import com.team.hbase.access.I.HURL;
import com.team.hbase.utils.SysUtil;
import com.team.hbase.widget.dialog.CustomProgressDialog;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-4 上午9:17:52
 * @Version 
 * @TODO
 */
public class HBaseAccess<T> implements HURL{
	private CustomProgressDialog dialog;
	private static final ExecutorService executorService=Executors.newCachedThreadPool();
	private Handler handler;
	private HRequestCallback<T> callback;
	private Context mContext;
	
	private boolean mIsShow=true;
	public HBaseAccess(final Context c,final HRequestCallback<T> requestCallback){
		this.callback=requestCallback;
		this.mContext=c;
		dialog=new CustomProgressDialog(mContext, "努力加载中...");
		initHandler();
	}
	private void initHandler(){
		handler=new Handler(new Handler.Callback() {
			
			@SuppressWarnings("unchecked")
			@Override
			public boolean handleMessage(Message msg) {
				if(isShow()){
					dialog.stopAndDismiss();
				}
				switch (msg.what) {
				case HRequestCallback.RESULT_SUCCESS:
					if(callback!=null){
						callback.onSuccess((T)msg.obj);
					}
					break;
				case HRequestCallback.RESULT_EMPTY:
					if(callback!=null){
						callback.onFail(mContext,"数据获取异常");
					}
					break;
				case HRequestCallback.RESULT_EXCEPTION:
					if(callback!=null){
						callback.onFail(mContext,"发生未知异常");
					}
					break;
				case HRequestCallback.RESULT_NETWORK_EXCEPTION:
					if(callback!=null){
						callback.onFail(mContext,"请检查网络是否正常");
					}
					break;
				case HRequestCallback.RESULT_TIMEOUT_EXCEPTION:
					if(callback!=null){
						callback.onFail(mContext,"请求超时，请重试");
					}
					break;
				case HRequestCallback.RESULT_SERVER_EXCEPTION:
					if(callback!=null){
						callback.onFail(mContext,"连接服务器失败，请稍后再试");
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-3 下午5:27:11
	 * @param url
	 * @param nvps
	 * @TODO 执行
	 */
	public void execute(String url,List<NameValuePair> nvps){
		if(isShow()){
			dialog.show();
		}
		executorService.execute(new TaskRunnable(url, nvps));
	}
	protected void execute(String url){
		execute(url, null);
	}
	/**
	 * 
	 * @author 李晓伟
	 * @Create_date 2015-3-3 下午5:26:39
	 * @Version 
	 * @TODO Task
	 */
	class TaskRunnable implements Runnable{
		private String url;
		private List<NameValuePair> nvps;
		TaskRunnable(String url, List<NameValuePair> nvps){
			this.nvps=nvps;
			this.url=url;
		}
		@Override
		public void run() {
			Message msg=Message.obtain();
			try {
				if(!SysUtil.isNetworkConnected(mContext)){
					msg.what=HRequestCallback.RESULT_NETWORK_EXCEPTION;
				}else{
					String result=_post(url, nvps);
					//测试数据
					result="{\"updateContent\": \"xxxx\",\"downloadUrl\":" +
							" \"http://10.1.1.230:8080/hczd-sys/upload_files/app_sys_file/2015-03/1425977626618.apk\"," +
							"\"force_flag\": \"否\",\"size\": \"0.91MB\",\"version\": \"2.2\"}";
					if(!TextUtils.isEmpty(result)){
						if(callback!=null){
							T t=callback.parseJson(result);
							if(t!=null){
								msg.obj=t;
								msg.what=HRequestCallback.RESULT_SUCCESS;
							}else{
								msg.what=HRequestCallback.RESULT_EMPTY;
							}
						}
					}else{
						msg.what=HRequestCallback.RESULT_SERVER_EXCEPTION;
					}
				}
			}catch(ClassCastException e){
				msg.what=HRequestCallback.RESULT_EMPTY;
				e.printStackTrace();
			}catch(SocketTimeoutException e){
				msg.what=HRequestCallback.RESULT_TIMEOUT_EXCEPTION;
				e.printStackTrace();
			}catch(SocketException e){
				msg.what=HRequestCallback.RESULT_TIMEOUT_EXCEPTION;
				e.printStackTrace();
			}catch (Exception e) {
				msg.what=HRequestCallback.RESULT_EXCEPTION;
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-3 下午5:24:49
	 * @param url
	 * @param nvps
	 * @return
	 * @throws Exception
	 * @TODO 访问url，获取数据
	 */
	protected String _post(String url, List<NameValuePair> nvps) throws Exception {
		RequestConfig requestConfig=RequestConfig.custom()
				.setConnectTimeout(10000)
				.setSocketTimeout(10000)
				.setConnectionRequestTimeout(10000)
				.setStaleConnectionCheckEnabled(true)
				.build();
		CloseableHttpClient httpclient=HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setRetryHandler(new HttpRequestRetryHandler() {//重连回调
					
					@Override
					public boolean retryRequest(IOException exception, int executionCount,
							HttpContext context) {
						/*System.out.println("executionCount:"+executionCount);
						if(executionCount<2) return true;*/
						return false;//false表示不重连
					}
				}).build();
		CloseableHttpResponse response = null;
		try {
			HttpPostHC4 request = new HttpPostHC4(url);
			request.setConfig(requestConfig);
			if(nvps!=null){
				Log.i("NameValuePair", nvps.toString());
				request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
			request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			request.setHeader("Connection", "Keep-Alive");
			response = httpclient.execute(request);
			String result="";
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity = response.getEntity();
				result=EntityUtilsHC4.toString(entity);
			}
			Log.i("result",result);
			return result;
		} finally {
			if(response!=null){
				response.close();
			}
			httpclient.close();
		}
	}
	public boolean isShow() {
		return mIsShow;
	}
	public void setIsShow(boolean isShow) {
		this.mIsShow = isShow;
	}

}
