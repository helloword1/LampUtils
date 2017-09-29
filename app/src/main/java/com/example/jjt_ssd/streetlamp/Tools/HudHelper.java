package com.example.jjt_ssd.streetlamp.Tools;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by CMQ on 2017/6/12.
 */

public class HudHelper {

    KProgressHUD hud;

    private Timer tipTimer;

    TimerTask tipTask;

    public void hudShowTip(Context context, String tip, int delay) {
        final TextView tv_Reset = new TextView(context);
        tv_Reset.setTextColor(Color.WHITE);
        tv_Reset.setTextSize(13);
        tv_Reset.setText(tip);
        tv_Reset.setPadding(0, 0, 0, 0);
        hud = KProgressHUD.create(context)
                .setCustomView(tv_Reset)
                .show();
        if (tipTimer != null) {
            tipTimer.cancel();
            tipTimer = null;
        }
        if (tipTask != null) {
            tipTask.cancel();
            tipTask = null;
        }
        tipTask = new TimerTask() {
            public void run() {
                if (hud != null) {
                    hud.dismiss();
                    hud = null;
                }
            }
        };
        tipTimer = new Timer();
        tipTimer.schedule(tipTask, delay);


    }


    public void hudShow(final Context context, String tip) {
        if (hud != null) {
            return;
        }
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(tip)
                .setCancellable(false);

        hud.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    SystemClock.sleep(1000);

                }
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (hud != null)
                            hud.dismiss();
                    }
                });
            }
        }).start();
    }

    public void hudShowNoText(final Context context) {
        if (hud != null) {
            return;
        }
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setCancellable(false);
        hud.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    SystemClock.sleep(1000);

                }
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (hud != null)
                            hud.dismiss();
                    }
                });
            }
        }).start();
    }

    public void hudUpdate(String tip) {
        hud.setLabel(tip);
    }

    public void hudUpdateAndHid(String tip, double delay) {
        hud.setLabel(tip);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hudHide();
            }
        }, (int) delay * 1000);
    }

    public void hudUpdateAndHid(String tip, double delay, final SuccessCallBack callBack) {
        hud.setLabel(tip);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hudHide();
                callBack.success();
            }
        }, (int) delay * 1000);
    }

    public void hudHide() {
        if (hud != null) {
            hud.dismiss();
            hud = null;
        }
    }

    public interface SuccessCallBack {
        void success();
    }
}
