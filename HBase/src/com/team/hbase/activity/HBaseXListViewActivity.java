package com.team.hbase.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.team.hbase.R;
import com.team.hbase.widget.xlist.XListView;
import com.team.hbase.widget.xlist.XListView.IXListViewListener;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-13 上午9:20:17
 * @Version 
 * @TODO 带有XListView的activity
 */
public abstract class HBaseXListViewActivity<T> extends HBaseActivity  implements IXListViewListener{
	private XListView mListView;
	protected int page_size=10;
	protected int current_page=1;
	private List<T> mDataSource;
	private OnXListItemClickListener onXListItemClickListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_base_x_list);
		initListView();
		initData();
		mListView.setAdapter(getAdapter());
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-13 上午10:33:56
	 * @TODO 初始化ListView
	 */
	private void initListView(){
		mListView=(XListView) findViewById(R.id.xListView);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(onXListItemClickListener!=null){
					onXListItemClickListener.onItemClick(parent, view, position-1, id);
				}
			}
			
		});
	}
	
	@Override
	public void onRefresh() {
		current_page=1;
		request();
	}
	@Override
	public void onLoadMore() {
		current_page++;
		request();
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-4 下午4:56:00
	 * @TODO 加载完成
	 */
	public void  onLoadComplete(long totalSize,List<T> newDatas) {
		if(mDataSource==null) {
			mDataSource=getDataSource();
			if(mDataSource==null){
				throw new NullPointerException("DataSource must be not null");
			}
		}
		stopRefreshOrLoad();
		if(current_page==1){
			mDataSource.clear();
		}
		if(newDatas!=null&&!newDatas.isEmpty()){
			mDataSource.addAll(newDatas);
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		mListView.setRefreshTime(df.format(new Date()));
		if(mDataSource!=null&&mDataSource.size()<totalSize){
			mListView.setPullLoadEnable(true);
		}else{
			mListView.setPullLoadEnable(false);
		}
		getAdapter().notifyDataSetChanged();
		if(totalSize==0){
			showMsg(this, R.string.empty_data);
		}
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-13 下午2:16:01
	 * @TODO 
	 */
	public void stopRefreshOrLoad(){
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-5 下午2:08:13
	 * @TODO 请求网络数据
	 */
	public abstract void request();
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-5 下午2:08:39
	 * @param adapter
	 * @TODO 设置XListView适配器
	 */
	public abstract BaseAdapter getAdapter();
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-5 下午2:06:08
	 * @param source
	 * @TODO 设置Adapter数据源
	 */
	public abstract List<T> getDataSource();
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-13 上午10:52:40
	 * @TODO 初始化数据
	 */
	public abstract void initData();
	/**
	 * 
	 * @author 李晓伟
	 * @Create_date 2015-3-6 下午1:57:56
	 * @Version 
	 * @TODO XListView item点击事件
	 */
	protected interface OnXListItemClickListener{
		public void onItemClick(AdapterView<?> parent, View view,int position, long id);
	}

	public OnXListItemClickListener getOnXListItemClickListener() {
		return onXListItemClickListener;
	}

	public void setOnXListItemClickListener(
			OnXListItemClickListener onXListItemClickListener) {
		this.onXListItemClickListener = onXListItemClickListener;
	}
}
