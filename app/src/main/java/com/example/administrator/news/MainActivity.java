package com.example.administrator.news;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import baidu.location.service.LocationService;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import org.apache.cordova.*;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CordovaInterface {

    CordovaWebView cordovaWebView;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    protected int activityResultRequestCode;
    protected CordovaPlugin activityResultCallback;
    protected CordovaPreferences prefs = new CordovaPreferences();
    protected ArrayList<PluginEntry> pluginEntries = null;
    protected SystemWebView systemWebView;
    protected Button compass;
    protected Button refresh;
    private AlertDialog.Builder builder;
    public final static int CODE = 1;
    private SlidingDrawer slidingDrawer;
    private LinearLayout linearLayout;
    private int[] pics;
    private String[] picsName;
    private String[] date;
    private String[] introduce;
    private LayoutInflater mInflater;
    private LocationService locationService;
    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;
    private Toolbar toolbar;
    private String longtitude;
    private String latitude;
    private CoordinatorLayout container;
    public static MainActivity mactivity;
    private CompoundButton nav_cameraButton;
    private CompoundButton nav_galleryButton;
    private CompoundButton nav_slideshowButton;
    private TourGuide mTourGuideHandler;
    private ImageView iv = null;

    private CountDownTimer timer = new CountDownTimer(8000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            systemWebView.loadUrl("file:///android_asset/www/index.html");
        }

        @Override
        public void onFinish() {
            iv.setVisibility(View.GONE);
            systemWebView.setVisibility(View.VISIBLE);
            compass.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);

            mTourGuideHandler = TourGuide.init(MainActivity.this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setDescription("点击展示/刷新新闻").setBackgroundColor(Color.parseColor("#919191")).setGravity(Gravity.LEFT | Gravity.BOTTOM))
                    .setOverlay(new Overlay())
                    .playOn(refresh);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPersimmions();
        mInflater = LayoutInflater.from(this);
        mactivity = this;
        container = (CoordinatorLayout) findViewById(R.id.coordinator);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        systemWebView.loadUrl("javascript:backHome()");
                        break;

                    case R.id.share:
                        Snackbar.make(container, "抱歉，分享功能尚未开放", Snackbar.LENGTH_LONG).show();
                        break;

                    case R.id.locate:
                        locationService.registerListener(mListener);
                        locationService.start();

                }
                return true;
            }

        });

        compass = (Button) findViewById(R.id.compass);
        refresh = (Button) findViewById(R.id.refresh);
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                systemWebView.loadUrl("javascript:compass(\"compass\")");
            }
        });



        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTourGuideHandler.cleanUp();
                systemWebView.loadUrl("javascript:refresh()");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        iv = (ImageView) this.findViewById(R.id.id_iv);
        systemWebView = (SystemWebView) findViewById(R.id.cordovaWebView);
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(systemWebView));
        cordovaWebView.init(this, parser.getPluginEntries(), parser.getPreferences());
        //systemWebView.loadUrl("file:///android_asset/www/index.html");
        timer.start();
        MenuItem nav_camera = navigationView.getMenu().findItem(R.id.layer1);
        MenuItem nav_gallery = navigationView.getMenu().findItem(R.id.layer2);
        MenuItem nav_slideshow = navigationView.getMenu().findItem(R.id.layer3);
        nav_cameraButton = (CompoundButton) MenuItemCompat.getActionView(nav_camera);
        nav_galleryButton = (CompoundButton) MenuItemCompat.getActionView(nav_gallery);
        nav_slideshowButton = (CompoundButton) MenuItemCompat.getActionView(nav_slideshow);

        nav_cameraButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    nav_cameraButton.setClickable(false);
            }
        });

        nav_galleryButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    nav_galleryButton.setClickable(false);
            }
        });

        nav_slideshowButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    nav_slideshowButton.setClickable(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationService = ((PicApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener);
        locationService.stop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        /**
         * city life
         */
        if (id == R.id.layer1) {
            nav_cameraButton.setChecked(true);
            nav_galleryButton.setChecked(false);
            nav_slideshowButton.setChecked(false);
            systemWebView.loadUrl("javascript:selectLayers(\"0\")");
        } else if (id == R.id.layer2) {
            nav_cameraButton.setChecked(false);
            nav_galleryButton.setChecked(true);
            nav_slideshowButton.setChecked(false);
            systemWebView.loadUrl("javascript:selectLayers(\"1\")");
        } else if (id == R.id.layer3) {
            nav_cameraButton.setChecked(false);
            nav_galleryButton.setChecked(false);
            nav_slideshowButton.setChecked(true);
            systemWebView.loadUrl("javascript:selectLayers(\"2\")");
        } else if (id == R.id.nav_send) {
            Snackbar.make(container, "抱歉，社交功能尚未开放", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.userInfo) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, 0);
        } else if (id == R.id.order) {
            Snackbar.make(container, "抱歉，订单功能尚未开放", Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.guide) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, guideActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int i) {
        setActivityResultCallback(cordovaPlugin);
        try {
            startActivityForResult(intent, i);
        } catch (RuntimeException e) {
            activityResultCallback = null;
            throw e;
        }
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {
        if (activityResultCallback != null) {
            activityResultCallback.onActivityResult(activityResultRequestCode, Activity.RESULT_CANCELED, null);
        }
        this.activityResultCallback = cordovaPlugin;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onMessage(String s, Object o) {
        if ("exit".equals(s)) {
            super.finish();
        }
        return null;
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    public void requestPermission(CordovaPlugin cordovaPlugin, int i, String s) {

    }

    @Override
    public void requestPermissions(CordovaPlugin cordovaPlugin, int i, String[] strings) {

    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            /**
             * get UserName
             */
            Bundle bundle = data.getExtras();
            final String userName = bundle.getString("userName");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                systemWebView.loadUrl("javascript:showAlert(\"" + userName + "\")");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);


            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";

            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE))
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";

            if (permissions.size() > 0)
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                latitude = "" + location.getLatitude();
                longtitude = "" + location.getLongitude();
                systemWebView.loadUrl("javascript:setLocation(\"" + latitude + "," + longtitude + "\")");
                sb.append(location.getLatitude());
                sb.append("-");
                sb.append(location.getLongitude());
                logMsg(sb.toString());
                Snackbar.make(container, "您当前所在位置的经纬度是: " + sb.toString(), Snackbar.LENGTH_LONG).show();
                locationService.stop();

            }
        }
    };

    public void logMsg(String str) {
        Log.e("LOCATION", str);
    }

}

