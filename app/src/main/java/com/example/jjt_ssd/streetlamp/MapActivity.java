package com.example.jjt_ssd.streetlamp;

import android.os.Bundle;

//地图界面
public class MapActivity extends BasePictureActivity {

    //设置显示的页面
    @Override
    int setLoyoutId() {
        return R.layout.activity_map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI("map","周边地图");
    }

}

