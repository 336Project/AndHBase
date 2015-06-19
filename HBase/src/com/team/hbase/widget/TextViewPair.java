package com.team.hbase.widget;


import com.team.hbase.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author 李晓伟
 * @Create_date 2015-3-10 下午5:27:06
 * @Version 
 * @TODO 水平成对TextView
 */
public class TextViewPair extends LinearLayout {
	public static final int LEN_BYTES=25;//超过多少字节换行，中文占2个字节，英文占1个字节
	public static final int LEN_PER=10;//多少个字添加一个换行符
	private String mNameText;
	private int mNameColor;
	private float mNameSize;
	
	private String mValueText;
	private int mValueColor;
	private float mValueSize;
	
	private TextView mNameView;
	private TextView mValueView;
	public TextViewPair(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.TextViewPair);
		mNameText=a.getString(R.styleable.TextViewPair_nameText);
		mNameColor=a.getColor(R.styleable.TextViewPair_nameColor, Color.BLACK);
		mNameSize=a.getDimension(R.styleable.TextViewPair_nameSize, 16);
		mValueText=a.getString(R.styleable.TextViewPair_valueText);
		mValueColor=a.getColor(R.styleable.TextViewPair_valueColor, Color.BLACK);
		mValueSize=a.getDimension(R.styleable.TextViewPair_valueSize, 16);
		
		a.recycle();
		init();
	}

	public TextViewPair(Context context) {
		this(context,null);
	}
	
	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(6, 6, 6, 6);
		//setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics()));
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		mNameView=new TextView(getContext());
		mNameView.setText(mNameText);
		mNameView.setTextSize(mNameSize);
		mNameView.setTextColor(mNameColor);
		addView(mNameView, params);
		
		params=new LayoutParams(0, LayoutParams.MATCH_PARENT,1);
		View emptyView=new View(getContext());
		addView(emptyView, params);
		
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		mValueView=new TextView(getContext());
		mValueView.setText(enter(mValueText));
		mValueView.setTextSize(mValueSize);
		mValueView.setTextColor(mValueColor);
		addView(mValueView, params);
	}

	public String getNameText() {
		return mNameText;
	}

	public void setNameText(String mNameText) {
		this.mNameText = mNameText;
		mNameView.setText(mNameText);
	}

	public String getValueText() {
		return mValueText;
	}

	public void setValueText(String mValueText) {
		this.mValueText = mValueText;
		mValueView.setText(enter(mValueText));
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2015-3-10 下午3:47:12
	 * @param msg
	 * @return
	 * @TODO 当字符过长时，添加换行符
	 */
	private String enter(String msg){
		if(TextUtils.isEmpty(msg)||msg.getBytes().length<=LEN_BYTES) return msg;
		int len=msg.length();
		int time=len/LEN_PER;
		if(time<LEN_BYTES/LEN_PER){
			return msg;
		}
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < time+1; i++) {
			if(i==time){
				sb.append(msg.substring(i*LEN_PER, len));
			}else{
				sb.append(msg.substring(i*LEN_PER, (i+1)*LEN_PER)+"\n");
			}
		}
		return sb.toString().trim();
	}
}
