package com.team.hbase;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.TextView;

import com.team.hbase.adapter.HBaseAdapter;
import com.team.hbase.adapter.ViewHolder;

public class TestAdapter extends HBaseAdapter<Map<String, String>> {

	public TestAdapter(Context c, List<Map<String, String>> datas) {
		super(c, datas);
	}

	@Override
	public void convert(ViewHolder holder, Map<String, String> bean,int position) {
		((TextView)holder.getView(android.R.id.text1)).setText(bean.get("title"));
	}

	@Override
	public int getResId() {
		return android.R.layout.simple_list_item_1;
	}

}
