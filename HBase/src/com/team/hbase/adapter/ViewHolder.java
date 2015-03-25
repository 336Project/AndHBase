package com.team.hbase.adapter;

import android.util.SparseArray;
import android.view.View;
/**
 * 
 * @author 李晓伟
 * @Create_date 2014-8-25 下午3:03:23
 * @TODO  ViewHolder模式超简洁写法
 */
public class ViewHolder {

	@SuppressWarnings("unchecked")
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
	}
}
