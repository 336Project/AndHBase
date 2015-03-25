package com.team.hbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.widget.BaseAdapter;

import com.team.hbase.activity.HBaseXListViewActivity;

public class TestXListViewActivity extends HBaseXListViewActivity<Map<String, String>> {
	private List<Map<String, String>> datas=new ArrayList<Map<String,String>>();
	private TestAdapter adapter;
	@Override
	public void request() {
		loadData();
	}

	@Override
	public BaseAdapter getAdapter() {
		return adapter;
	}

	@Override
	public List<Map<String, String>> getDataSource() {
		return datas;
	}

	@Override
	public void initData() {
		setActionBarTitle("测试XListView");
		adapter=new TestAdapter(this, datas);
		loadData();
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-25 下午3:18:20
	 * @TODO 模拟请求
	 */
	private void loadData(){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				List<Map<String, String>> newDatas=new ArrayList<Map<String,String>>();
				for (int i = 0; i < 5; i++) {
					Map<String, String> map=new HashMap<String, String>();
					map.put("title", i+"");
					newDatas.add(map);
				}
				onLoadComplete(30, newDatas);
			}
		}, 3000);
	}
}
