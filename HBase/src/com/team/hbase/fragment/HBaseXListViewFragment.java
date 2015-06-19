package com.team.hbase.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.team.hbase.R;
import com.team.hbase.application.HBaseApp;
import com.team.hbase.widget.xlist.XListView;
import com.team.hbase.widget.xlist.XListView.IXListViewListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-5 下午1:54:28
 * @Version 
 * @TODO 带有XListView的Fragment
 */
public abstract class HBaseXListViewFragment<T> extends Fragment implements IXListViewListener{
	protected HBaseApp mBaseApp;
	private XListView mListView;
	protected int page_size=10;
	protected int current_page=1;
	private List<T> mDataSource;
	private OnXListItemClickListener onXListItemClickListener;
	
	private boolean mHasLoadedOnce = false;//是否已经加载过一次
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_base_x_list, null);
		mBaseApp=(HBaseApp) getActivity().getApplication();
		initListView(view);
		initData();
		mListView.setAdapter(getAdapter());
		return view;
	}
	
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-4 下午2:44:25
	 * @param view
	 * @TODO
	 */
	private void initListView(View view) {
		mListView=(XListView) view.findViewById(R.id.xListView);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(onXListItemClickListener!=null){
					onXListItemClickListener.onItemClick(parent, view, position, id);
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
	public void onLoadComplete(long totalSize,List<T> newDatas) {
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
			Toast.makeText(getActivity(), "暂无相关数据", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-14 上午11:11:37
	 * @return
	 * @TODO 是否延迟加载
	 */
	public boolean isLazyLoad(){
		return false;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {//让fragment第一次可见的时候再去请求网络数据，
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisible()&&isLazyLoad()){
			if(isVisibleToUser&&!mHasLoadedOnce){
				mHasLoadedOnce=true;
				request();
			}
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
	 * 2015-3-11 下午5:05:37
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
