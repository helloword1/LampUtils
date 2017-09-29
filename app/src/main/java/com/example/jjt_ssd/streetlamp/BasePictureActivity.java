
package com.example.jjt_ssd.streetlamp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * 三个图片页的基类
 * Created by JJT-ssd on 2016/8/31.
 */
public abstract class BasePictureActivity extends BaseActivity {

    private ImageView iv_MapPre;
    private ImageView iv_MapNext;
    private Button bt_Previous;
    private Button bt_Next;
    private int CountNum = 0;
    private List<String> images = new ArrayList<String>();
    private List<Integer> ins = new ArrayList<>();

    private ViewFlipper viewFlipper;
    private GestureDetector detector; //手势检测

    Animation leftInAnimation;
    Animation leftOutAnimation;
    Animation rightInAnimation;
    Animation rightOutAnimation;

    abstract int setLoyoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //设置显示的页面
    @Override
    protected int getLoyoutId() {
        return setLoyoutId();
    }

    //设置、获取UI参数
    public void setupUI(String SDFileName, String Title) {
        setTitle(Title);
        iv_MapNext = (ImageView) findViewById(R.id.MAPImageOne);
        iv_MapPre = (ImageView) findViewById(R.id.MAPImageTwo);

        try {
//            images = GetExternalDevice.getImagePathFromSD(SDFileName);
            getView(SDFileName);
//            SetImage(iv_MapNext, CountNum);
            iv_MapNext.setImageResource(ins.get(CountNum));
        } catch (Exception e) {
        }

        bt_Previous = (Button) findViewById(R.id.MAPPrevious);
        bt_Next = (Button) findViewById(R.id.MAPNext);

        viewFlipper = (ViewFlipper) findViewById(R.id.MAPDetails);
        //动画效果
        leftInAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftOutAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.left_out);
        rightOutAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.right_out);
        rightInAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.right_in);

        //设置UI的响应事件
        EventHandle();
    }

    private void getView(String sdFileName) {
        if (TextUtils.equals(sdFileName,"cmintroduce")){//客户介绍
            ins.clear();
            ins.add(R.mipmap.goockr);
        }else if (TextUtils.equals(sdFileName,"map")){//周边地图
            ins.clear();
            ins.add(R.mipmap.map_bicycle);
            ins.add(R.mipmap.map_building);
            ins.add(R.mipmap.map_bus);
            ins.add(R.mipmap.map_food);
        }else if (TextUtils.equals(sdFileName,"zrintroduce")){//中睿介绍
            ins.clear();
            ins.add(R.mipmap._0000_1);
            ins.add(R.mipmap._0001_2);
            ins.add(R.mipmap._0002_3);
            ins.add(R.mipmap._0003_4);
            ins.add(R.mipmap._0004_5);
            ins.add(R.mipmap._0005_6);
            ins.add(R.mipmap._0006_7);
            ins.add(R.mipmap._0007_8);
            ins.add(R.mipmap._0008_9);
            ins.add(R.mipmap._0009_10);

        }
    }

    private ImageView getImageView(int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(id);
        return imageView;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.detector.onTouchEvent(event); //touch事件交给手势处理。
    }

    //设置UI的响应事件
    private void EventHandle() {
        bt_Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prev();
            }
        });

        bt_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next();
            }
        });

        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 120) {
                    Next();//向右滑动
                    return true;
                } else if (e1.getX() - e2.getX() < -120) {
                    Prev();//向左滑动
                    return true;
                }
                return false;
            }
        });
    }

    //设置SD卡的图片
    private void SetImage(ImageView iView, int num) {
//        String myJpgPath = ins.get(num);
//        Bitmap bm = BitmapFactory.decodeFile(myJpgPath);
//        iView.setImageBitmap(bm);
        iView.setImageResource(ins.get(num));

    }

    private void Prev() {
        if (ins.size() > 0) {
            if (CountNum <= 0) CountNum = ins.size() - 1;
            else CountNum--;
            viewFlipper.setInAnimation(this, R.anim.right_in);
            viewFlipper.setOutAnimation(this, R.anim.right_out);
            if (viewFlipper.getDisplayedChild() == 0)
                SetImage(iv_MapPre, CountNum);
            else
                SetImage(iv_MapNext, CountNum);
            viewFlipper.showPrevious();
            viewFlipper.stopFlipping();
        }
    }

    public void Next() {
        if (ins.size() > 0) {
            if (CountNum >= ins.size() - 1) CountNum = 0;
            else CountNum++;
            viewFlipper.setInAnimation(this, R.anim.left_in);
            viewFlipper.setOutAnimation(this, R.anim.left_out);
            if (viewFlipper.getDisplayedChild() == 0)
                SetImage(iv_MapPre, CountNum);
            else
                SetImage(iv_MapNext, CountNum);
            // 显示下一个组件
            viewFlipper.showNext();
            // 停止自动播放
            viewFlipper.stopFlipping();
        }
    }

//    public void auto(View source)
//    {
//        viewFlipper.setInAnimation(this , android.R.anim.slide_in_left);
//        viewFlipper.setOutAnimation(this , android.R.anim.slide_out_right);
//        // 开始自动播放
//        viewFlipper.startFlipping();
//    }
}
