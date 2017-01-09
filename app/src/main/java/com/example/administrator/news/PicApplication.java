package com.example.administrator.news;

import android.app.Application;
import baidu.location.service.LocationService;


public class PicApplication extends Application {
	public LocationService locationService;

	@Override
	public void onCreate() {
		super.onCreate();
		locationService = new LocationService(getApplicationContext());
	}
}