package com.example.actiongame;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Runnable {
    // キャラクターと障害物の画像
    private ImageView kyara1;
    private ImageView rect;
    // キャラクターを左右に動かすボタン
    private Button leftbtn;
    private Button rightbtn;
    // キャラクターと障害物の初期位置
    private float rectx = 50f;
    private float recty = -70f;
    private  float kyarax = 500f;
    // 画面の座標
    private float screenx;
    private float screeny;

    private volatile boolean clickphase = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 画像をIDで取得
        kyara1 = findViewById(R.id.kyara1);
        rect = findViewById(R.id.rect);
        // ボタンをIDで取得
        leftbtn = findViewById(R.id.leftbtn);
        leftbtn.setOnClickListener(this);
        rightbtn = findViewById(R.id.rightbtn);
        rightbtn.setOnClickListener(this);

        // 画面のサイズを取得
        WindowManager win = getWindowManager();
        Display dis = win.getDefaultDisplay();
        Point poi = new Point();
        dis.getSize(poi);

        screenx = poi.x;
        screeny = poi.y;

        // 2つの画像の初期値を設定
        rect.setX(rectx);
        rect.setY(recty);
        kyara1.setX(kyarax);
        kyara1.setY(800f);

        clickphase = false;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        int period = 100; // 1000ミリ秒 = 1秒
        while(!clickphase) {
            try {
                Thread.sleep(period);
            } catch(InterruptedException e) {
                clickphase = true;
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //実行したい機能
                    recty += 30f; // 時間periodで動く距離
                    if(recty > screeny) {
                        recty = -100f;

                        // 障害物の横位置をランダムにする
                        Random random = new Random();
                        int x = random.nextInt((int)screenx) + 1;
                        rectx = x;
                    }
                    // 配置場所をセット
                    rect.setX(rectx);
                    rect.setY(recty);
                }
            });
        }
    }

    // 左右の移動ボタンを押したらそれぞれキャラクターが移動する
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.leftbtn) {
            kyarax -= 20f;
            kyara1.setX(kyarax);
        } else if (view.getId() == R.id.rightbtn) {
            kyarax += 20f;
            kyara1.setX(kyarax);
        }
    }
}