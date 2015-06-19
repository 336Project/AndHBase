package com.team.hbase.utils;


import java.util.ArrayList;

import android.app.Activity;

public class AppManager {
	private ArrayList<Activity> activities=new ArrayList<Activity>();
	private static AppManager instance;
	
	private AppManager(){
	}
	public static AppManager getInstance(){
		if(instance==null){
			instance=new AppManager();
		}
		return instance;
	}
	public void add(Activity activity){
		activities.add(activity);
	}
	
	public void remove(Activity activity){
		activities.remove(activity);
	}
	
	public void ExitApp(){
		for (Activity activity : activities) {
			if(activity!=null){
				activity.finish();
			}
		}
		activities.clear();
		System.exit(0);
	}
}
