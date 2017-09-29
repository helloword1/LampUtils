package com.example.jjt_ssd.streetlamp;

import android.os.Bundle;

//中睿介绍页面
public class ZRIntroduceActivity extends BasePictureActivity {

    //设置显示的页面
    @Override
    int setLoyoutId() {
        return R.layout.activity_zrintroduce;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI("zrintroduce","中睿介绍");

    }
}
