package com.team.hbase.activity;

import java.util.List;

import com.team.hbase.R;
import com.team.hbase.adapter.TabFtagmentAdapter;
import com.team.hbase.widget.viewpagerindicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-25 下午2:00:42
 * @Version 
 * @TODO 带有Tab选项卡的activity
 */
public abstract class HBaseTabPageActivity extends HBaseActivity {
	private String[] titles;
	private int[] icons;
	private List<? extends Fragment> fragments;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.StyledIndicators);
		setBaseContentView(R.layout.item_tab_page);
		init();
		titles=getTitles();
		icons=getIcons();
		fragments=getFragments();
		initTab();
	}
	
	private void initTab() {
		TabFtagmentAdapter adapter=new TabFtagmentAdapter(getSupportFragmentManager(), titles, icons, fragments);
		ViewPager tabPager=(ViewPager) findViewById(R.id.tab_pager);
		tabPager.setAdapter(adapter);
		//tabPager.setOffscreenPageLimit(titles.length);
		TabPageIndicator mTabIndicator= (TabPageIndicator)findViewById(R.id.tab_indicator);
		mTabIndicator.setViewPager(tabPager);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-25 下午2:07:18
	 * @return
	 * @TODO 设置选项卡标题
	 */
	public abstract String[] getTitles();
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-25 下午2:07:27
	 * @return
	 * @TODO 设置选项卡图片
	 */
	public abstract int[] getIcons();
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-25 下午2:07:47
	 * @return
	 * @TODO 设置选项卡内容
	 */
	public abstract List<? extends Fragment> getFragments();
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-25 下午2:23:11
	 * @TODO 做一些初始化工作
	 */
	public abstract void init();
	
}
