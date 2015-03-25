package com.team.hbase.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.team.hbase.MainActivity;
import com.team.hbase.R;
import com.team.hbase.application.HBaseApp;
import com.team.hbase.utils.AppManager;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-9 下午5:49:07
 * @Version 
 * @TODO
 */
public class HBaseActivity extends FragmentActivity{
	protected HBaseApp mBaseApp;
	
	private FrameLayout mLayoutContent;
	private RelativeLayout mLayoutError;
	
	private LinearLayout mLayoutCustomView;
	private ImageView mLeftIcon;
	private ImageView mRightIcon;
	
	private TextView mTitleView;
	private TextView mErrorView;
	private AnimationDrawable anim;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);
		AppManager.getInstance().add(this);
		mBaseApp=(HBaseApp) getApplication();
		initActionBar();
	}
	
	private void initActionBar() {
		BaseOnClickListener listener=new BaseOnClickListener();
		mLayoutContent=(FrameLayout) findViewById(R.id.fragment_content);
		mLayoutCustomView=(LinearLayout) findViewById(R.id.layout_custom_title_view);
		mLeftIcon=(ImageView) findViewById(R.id.iv_left_icon);
		mLeftIcon.setOnClickListener(listener);
		mRightIcon=(ImageView) findViewById(R.id.iv_right_icon);
		mRightIcon.setOnClickListener(listener);
		setCustomView(mLayoutCustomView);
	}
	
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-5 上午9:58:43
	 * @param customViewLayout
	 * @TODO 自定义标题栏
	 */
	public void setCustomView(LinearLayout customViewLayout){
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1);
		params.gravity=Gravity.CENTER;
		View view=getLayoutInflater().inflate(R.layout.item_action_bar_title, null);
		mTitleView=(TextView) view.findViewById(R.id.action_bar_title);
		mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		customViewLayout.addView(view, params);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-2 下午5:07:01
	 * @param title
	 * @TODO 设置标题
	 */
	public void setActionBarTitle(String title){
		if(mTitleView!=null){
			mTitleView.setText(title);
		}
	}
	public void setActionBarTitle(int resId){
		if(mTitleView!=null){
			mTitleView.setText(resId);
		}
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-9 上午11:01:33
	 * @param c
	 * @param clzz
	 * @TODO 页面跳转
	 */
	public void jump(Context c,Class<?> clzz){
		Intent intent=new Intent(c, clzz);
		startActivity(intent);
	}
	public void jump(Context c,Class<?> clzz,Bundle bundle){
		Intent intent=new Intent(c, clzz);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-5 上午10:11:40
	 * @param layoutResID
	 * @TODO 
	 */
	public void setBaseContentView(int layoutResID){
		hideErrorView();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(layoutResID, null);
		mLayoutContent.addView(v);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-12 上午11:56:51
	 * @TODO 隐藏错误页面
	 */
	public void hideErrorView(){
		mLayoutContent.setVisibility(View.VISIBLE);
		if(mLayoutError!=null)mLayoutError.setVisibility(View.GONE);
		if(anim!=null){
			anim.stop();
			anim=null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().remove(this);
	}
	/**
	 * 
	 * @author 李晓伟
	 * @Create_date 2015-3-12 上午11:18:13
	 * @Version 
	 * @TODO 按钮点击事件
	 */
	private class BaseOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_left_icon:
				finish();
				break;
			case R.id.iv_right_icon:
				Intent intent=new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(HBaseActivity.this, MainActivity.class);
				startActivity(intent);
				break;
			case R.id.layout_error:
				onReload();
				break;
			default:
				break;
			}
		}
		
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-9 下午5:11:39
	 * @TODO 数据请求失败，显示的页面
	 */
	public void onLoadFail(){
		if(mLayoutError==null){
			View view=((ViewStub) findViewById(R.id.vs_error)).inflate();
			mLayoutError=(RelativeLayout) view.findViewById(R.id.layout_error);
			mLayoutError.setOnClickListener(new BaseOnClickListener());
			mErrorView=(TextView) view.findViewById(R.id.img_error);
			anim =(AnimationDrawable) mErrorView.getCompoundDrawables()[1];
		}
		getLayoutError().setVisibility(View.VISIBLE);
		mLayoutContent.setVisibility(View.GONE);
		if(anim!=null){
			anim.start();
		}
	}
	public void onLoadFail(String errorMsg){
		onLoadFail();
		mErrorView.setText(errorMsg);
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-9 下午4:10:47
	 * @TODO 触摸layout_error，重新加载
	 */
	public void onReload(){
	}
	
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-9 上午10:18:37
	 * @param c
	 * @param msg
	 * @TODO 提示信息
	 */
	public void showMsg(Context c,String msg){
		Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
	}
	public void showMsg(Context c,int resId){
		Toast.makeText(c, resId, Toast.LENGTH_SHORT).show();
	}
	public ImageView getLeftIcon() {
		return mLeftIcon;
	}

	public void setLeftIcon(ImageView mLeftIcon) {
		this.mLeftIcon = mLeftIcon;
	}

	public ImageView getRightIcon() {
		return mRightIcon;
	}

	public void setRightIcon(ImageView mRightIcon) {
		this.mRightIcon = mRightIcon;
	}

	public TextView getTitleView() {
		return mTitleView;
	}

	public void setTitleView(TextView mTitleView) {
		this.mTitleView = mTitleView;
	}
	public FrameLayout getLayoutContent() {
		return mLayoutContent;
	}

	public RelativeLayout getLayoutError() {
		return mLayoutError;
	}
}
