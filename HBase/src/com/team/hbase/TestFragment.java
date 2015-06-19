package com.team.hbase;

import com.team.hbase.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class TestFragment extends Fragment {
	private String title;
	
	public TestFragment(String title) {
		super();
		this.title = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_test, null);
		((TextView)v.findViewById(R.id.text01)).setText(title);
		return v;
	}
	
	
}
