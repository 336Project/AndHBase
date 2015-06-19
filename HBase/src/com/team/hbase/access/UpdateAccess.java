package com.team.hbase.access;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.team.hbase.access.I.HRequestCallback;
import com.team.hbase.model.UpdateInfo;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-25 上午10:09:32
 * @Version 
 * @TODO 检测更新接口
 */
public class UpdateAccess extends HBaseAccess<UpdateInfo> {

	public UpdateAccess(Context c, HRequestCallback<UpdateInfo> requestCallback) {
		super(c, requestCallback);
	}

	public void execute(String version){
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("", ""));
		execute("http://www.baidu.com/",nvps);
	}
	
}
