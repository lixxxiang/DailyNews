package com.example.administrator.news;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * A login screen that offers login via email/password.
 */
public class guideActivity extends Activity {
    private Button slide;
    private Button corner;
    private Button earth;
    private TourGuide mTutorialHandler;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        slide = (Button) findViewById(R.id.slide);
        corner = (Button) findViewById(R.id.corner);
        earth = (Button) findViewById(R.id.earth);

        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip()
                        .setTitle("侧滑")
                        .setBackgroundColor(Color.parseColor("#919191"))
                        .setDescription("侧滑打开功能菜单")
                        .setGravity(Gravity.RIGHT)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(enterAnimation)
                        .setExitAnimation(exitAnimation)
                        .setStyle(Overlay.Style.NoHole)
                );

        slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTutorialHandler.cleanUp();
                mTutorialHandler.setToolTip(new ToolTip().setTitle("功能区").setBackgroundColor(Color.parseColor("#919191")).setDescription("1.主页键(点击回到地球模型主界面）   " +
                        "2.定位键（点击定位当前经纬度）   " +
                        "3.分享键(点击分享当前桌面截图至社交网络（敬请期待））   " +
                        "4.刷新新闻键(点击刷新新闻信息)     " +
                        "5.指南针(点击后地球模型旋转到正南北方向)").setGravity(Gravity.BOTTOM | Gravity.LEFT)).playOn(corner);
            }
        });


        corner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTutorialHandler.cleanUp();
                mTutorialHandler.setToolTip(new ToolTip().setTitle("地球模型").setBackgroundColor(Color.parseColor("#919191")).setDescription("1.支持多手势操作(单指旋转/双指缩放/双指平移改变视角)     2.点击红色定位图标跳转到新闻界面    3.点击数字标签展开新闻标题（敬请期待）").setGravity(Gravity.BOTTOM)).playOn(earth);
            }
        });


        earth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTutorialHandler.cleanUp();
                try {
                    Thread.sleep(100);
                    Intent intent = new Intent().setClass(guideActivity.this, guide2Activity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mTutorialHandler.playOn(slide);
    }
}