package com.team.hbase.widget.dialog;


import com.team.hbase.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/***
 * 自定义的进度条窗口
 * @create 2014-07-03
 */
public class CustomProgressDialog extends ProgressDialog {
	private TextView mTxtMsg;//提示文本控件
	private String mMsg;//提示文字
	public CustomProgressDialog(Context context,String message) {
		super(context, R.style.h_progress_dialog);
		mMsg = message;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		setContentView(R.layout.item_loading_image);
		mTxtMsg = (TextView) findViewById(R.id.txtProgressMsg);
		mTxtMsg.setText(mMsg);
		this.setCanceledOnTouchOutside(false);
	}
	@Override
	public void setMessage(CharSequence message) {
		super.setMessage(message);
		mTxtMsg.setText(message);
	}
	/***
	 * 停止并关闭进度窗口
	 */
	public void stopAndDismiss() {
		if (this.isShowing()) {
			dismiss();
		}
	}
}
