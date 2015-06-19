package com.team.hbase;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;

import com.team.hbase.activity.HBaseTabPageActivity;

public class TestTabActivity extends HBaseTabPageActivity{
	private String[] titles=new String[]{"选项卡1","选项卡2"};
	private List<Fragment> fragments;
	@Override
	public String[] getTitles() {
		return titles;
	}

	@Override
	public int[] getIcons() {
		return null;
	}

	@Override
	public List<? extends Fragment> getFragments() {
		return fragments;
	}
	@Override
	public void init() {
		setActionBarTitle("选项卡测试");
		fragments=new ArrayList<Fragment>();
		fragments.add(new TestFragment("选项卡1"));
		fragments.add(new TestFragment("选项卡2"));
	}

}
