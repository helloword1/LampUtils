package com.example.jjt_ssd.streetlamp;


import android.os.Bundle;

//客户介绍页面
public class CMIntroduceActivity extends BasePictureActivity {

    //设置显示的页面
    @Override
    int setLoyoutId() {
        return R.layout.activity_cmintroduce;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI("cmintroduce","客户介绍");
    }
}