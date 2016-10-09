package com.hitomi.circlemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

public class MainActivity extends AppCompatActivity {

    private CircleMenu circleMenu;

    private int[] iconResArray = new int[5];

    {
        iconResArray[0] = R.mipmap.icon_home;
        iconResArray[1] = R.mipmap.icon_search;
        iconResArray[2] = R.mipmap.icon_notify;
        iconResArray[3] = R.mipmap.icon_setting;
        iconResArray[4] = R.mipmap.icon_gps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setMainIconResource(R.mipmap.icon_menu, R.mipmap.icon_cancel);
        circleMenu.setSubIconResources(iconResArray);

        circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int index) {
            }
        });

        circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
            @Override
            public void onMenuOpened() {
            }

            @Override
            public void onMenuClosed() {
            }
        });
    }
}
