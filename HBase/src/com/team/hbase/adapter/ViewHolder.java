package com.team.hbase.adapter;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author 李晓伟
 * @Create_date 2014-8-25 下午3:03:23
 * @TODO ViewHolder模式超简洁写法
 */
public class ViewHolder {
	private final SparseArray<View> mViews;
	private View convertView;

	private ViewHolder(View convertView) {
		this.convertView = convertView;
		mViews = new SparseArray<View>();
	}

	public static ViewHolder get(View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}

	@SuppressWarnings("unchecked")
	public <E extends View> E getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (E) view;
	}
	
	public TextView getTextView(int viewId){
		return getView(viewId);
	}
	public CheckBox getCheckBox(int viewId){
		return getView(viewId);
	}
	public ImageView getImageView(int viewId){
		return getView(viewId);
	}
	public Button getButton(int viewId){
		return getView(viewId);
	}
	public ImageButton getImageButton(int viewId){
		return getView(viewId);
	}
	public View getConvertView() {
		return convertView;
	}

	/*@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}*/

}
