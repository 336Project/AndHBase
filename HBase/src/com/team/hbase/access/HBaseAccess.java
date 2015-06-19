package com.team.hbase.access;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
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
		dialog=new CustomProgressDialog(c, "努力加载中...");
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
	public void execute(String url,List<NameValuePair> nvps,Map<String, File> files){
		if(isShow()){
			dialog.show();
		}
		executorService.execute(new TaskRunnable(url, nvps,files));
	}
	public void execute(String url,List<NameValuePair> nvps){
		if(isShow()){
			dialog.show();
		}
		executorService.execute(new TaskRunnable(url, nvps,null));
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
		private Map<String, File> files;
		TaskRunnable(String url, List<NameValuePair> nvps,Map<String, File> files){
			this.nvps=nvps;
			this.url=url;
			this.files=files;
		}
		@Override
		public void run() {
			Message msg=Message.obtain();
			try {
				if(!SysUtil.isNetworkConnected(mContext)){
					msg.what=HRequestCallback.RESULT_NETWORK_EXCEPTION;
				}else{
					String result="";
					if(files==null){
						result=_post(url, nvps);
					}else{
						result=_postFile(url, nvps, files);
					}
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
				HttpEntity httpEntity=new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
				Log.i("url",url+"&"+EntityUtilsHC4.toString(httpEntity));
				request.setEntity(httpEntity);
			}
			request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			request.setHeader("Connection", "Keep-Alive");
			response = httpclient.execute(request);
			String result="";
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity = response.getEntity();
				result=EntityUtilsHC4.toString(entity);
				entity.consumeContent();
			}
			request.abort();
			Log.i("result",result);
			return result;
		} finally {
			if(response!=null){
				response.close();
			}
			httpclient.close();
		}
	}
	
	protected String _get(String url, List<NameValuePair> nvps) throws Exception {
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
		try{
			HttpGetHC4 request=new HttpGetHC4(url);
			request.setConfig(requestConfig);
			HttpParams params=new BasicHttpParams();
			if(nvps!=null){
				Log.i("NameValuePair", nvps.toString());
				for (NameValuePair nameValuePair : nvps) {
					params.setParameter(nameValuePair.getName(), nameValuePair.getValue());
				}
				request.setParams(params);
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
		}finally{
			if(response!=null){
				response.close();
			}
			httpclient.close();
		}
	}
	/**
	 * 
	 * 2015-4-3 下午3:27:24
	 * @param url
	 * @param nvps
	 * @param files
	 * @return
	 * @throws Exception
	 * @TODO post文件资源上传
	 */
	protected String _postFile(String url,List<NameValuePair> nvps,Map<String, File> files)throws Exception{
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
			MultipartEntityBuilder builder=MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			if(nvps!=null){
				Log.i("NameValuePair", nvps.toString());
				for (NameValuePair pair : nvps) {
					builder.addTextBody(pair.getName(), pair.getValue());
				}
			}
			if(files!=null){
				Set<Entry<String, File>> entries=files.entrySet();
				for (Entry<String, File> entry : entries) {
					Log.i("file-key", entry.getKey());
					builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
				}
			}
			request.setEntity(builder.build());
			response = httpclient.execute(request);
			String result="";
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity = response.getEntity();
				result=EntityUtilsHC4.toString(entity);
				entity.consumeContent();
			}
			request.abort();
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
