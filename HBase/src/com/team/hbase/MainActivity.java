package com.team.hbase;

import com.team.hbase.activity.HBaseActivity;
import com.team.hbase.utils.AppManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends HBaseActivity {
	private long currTime=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarTitle("测试");
		setBaseContentView(R.layout.activity_main);
	}
	
	public void testTab(View v){
		startActivity(new Intent(this, TestTabActivity.class));
	}
	public void testXListView(View v){
		startActivity(new Intent(this, TestXListViewActivity.class));
	}
	public void testRequest(View v){
		startActivity(new Intent(this, TestRequestActivity.class));
	}
	
	@Override
	public void onBackPressed() {
		if(System.currentTimeMillis()-currTime>2000){
			currTime=System.currentTimeMillis();
			showMsg(MainActivity.this, "再按一次退出程序");
		}else{
			AppManager.getInstance().ExitApp();
		}
	}
	
}
