package com.team.hbase;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.team.hbase.access.UpdateAccess;
import com.team.hbase.access.I.HRequestCallback;
import com.team.hbase.activity.HBaseActivity;
import com.team.hbase.model.UpdateInfo;
import com.team.hbase.utils.JSONParse;

public class TestRequestActivity extends HBaseActivity {
	private TextView msg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarTitle("测试数据请求");
		setBaseContentView(android.R.layout.simple_list_item_1);
		msg=(TextView) findViewById(android.R.id.text1);
		request();
	}

	private void request() {
		HRequestCallback<UpdateInfo> requestCallback = new HRequestCallback<UpdateInfo>() {
			
			@Override
			public void onFail(Context c, String errorMsg) {
				super.onFail(c, errorMsg);
				onLoadFail();
			}
			
			@Override
			public void onSuccess(UpdateInfo result) {
				msg.setText(result.toString());
			}

			@Override
			public UpdateInfo parseJson(String jsonStr) {
				return (UpdateInfo) JSONParse.jsonToBean(jsonStr,UpdateInfo.class);
			}
		};
		UpdateAccess access = new UpdateAccess(this, requestCallback);
		access.execute("");
	}
}
