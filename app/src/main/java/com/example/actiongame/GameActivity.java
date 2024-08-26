package com.example.actiongame;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Runnable {
    // キャラクターと障害物の画像
    private ImageView kyara1;
    private ImageView rect;
    // スコア表示部
    private TextView scoretext;
    // キャラクターを左右に動かすボタン
    private Button leftbtn;
    private Button rightbtn;
    // ゲームエリアのレイアウト
    private FrameLayout frame;
    // キャラクターと障害物の初期位置
    private float rectx;
    private float recty = -70f;
    private float kyarax;
    private float kyaray;
    // 障害物のサイズ
    private float rectwidth;
    private float rectheight;
    private int rectmoverange;
    // キャラクターのサイズ
    private float kyarawidth;
    private float kyaraheight;
    // ゲームエリアのサイズ
    private float screenx;
    private float screeny;
    // スコアカウント用
    private int score = 0;
    // 処理停止判定　true:停止　false:続行
    private volatile boolean clickphase = false;
    // Handlerクラスのインスタンス化
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 画像をIDで取得
        kyara1 = findViewById(R.id.kyara1);
        rect = findViewById(R.id.rect);
        // スコアテキストエリアをIDで取得
        scoretext = findViewById(R.id.scoretext);
        // ゲームエリアをIDで取得
        frame = findViewById(R.id.frame);
        // ボタンをIDで取得
        leftbtn = findViewById(R.id.leftbtn);
        leftbtn.setOnClickListener(this);
        rightbtn = findViewById(R.id.rightbtn);
        rightbtn.setOnClickListener(this);

        ViewTreeObserver obser = kyara1.getViewTreeObserver();
        obser.addOnGlobalLayoutListener(() -> {
            // キャラクターのサイズ取得
            kyarawidth = kyara1.getWidth();
            kyaraheight = kyara1.getHeight();
            // ゲームエリアのサイズ取得
            screenx = frame.getWidth();
            screeny = frame.getHeight();
            // キャラクターの初期配置
            kyarax = (screenx / 2) - (kyarawidth / 2);
            kyara1.setX(kyarax);
            kyaray = screeny - kyaraheight;
            kyara1.setY(kyaray);
        });

        ViewTreeObserver obser2 = rect.getViewTreeObserver();
        obser2.addOnGlobalLayoutListener(() -> {
            // 障害物のサイズ取得
            rectwidth = rect.getWidth();
            rectheight = rect.getHeight();
            // ゲームエリアのサイズ取得
            screenx = frame.getWidth();
            screeny = frame.getHeight();
            // 障害物の初期配置（ランダム）
            rectmoverange = (int)screenx + 1 - (int)rectwidth;
            int firstX = new Random().nextInt(rectmoverange) ;
            rectx = (float)firstX;
            rect.setX(rectx);
            rect.setY(recty);

//            scoretext.setText("rect:" + rectwidth + " screen:" + screenx);
        });

        // 初期スコア表示
        scoretext.setText("score:" + score);

        clickphase = false;
        Thread thread = new Thread(this);
        // 実行
        thread.start();
    }

    @Override
    public void run() {
        int period = 10; // 1000ミリ秒 = 1秒
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
                    recty += 3f; // 時間periodで動く距離
                    if(recty > screeny) {
                        recty = -100f;

                        // 障害物の横位置をランダムにする
                        Random random = new Random();
                        int x = random.nextInt(rectmoverange);
                        rectx = x;
                        score++;
                        scoretext.setText("score:" + score);
                    }

                    // 衝突判定
                    if((recty + rectheight) >= kyaray && recty <= (kyaray + kyaraheight) &&
                            (rectx + rectwidth) > kyarax && rectx < (kyarax + kyarawidth)) {
                        // 処理停止
                        clickphase = true;
                        // ゲームオーバー表示
                        scoretext.setText("GAME OVER");
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
            if(kyarax <= 50f) {
                kyarax = 0f;
            } else {
                kyarax -= 50f;
            }
            kyara1.setX(kyarax);
        } else if (view.getId() == R.id.rightbtn) {
            if(kyarax >= (screenx - kyarawidth - 50f)) {
                kyarax = (screenx - kyarawidth);
            } else {
                kyarax += 50f;
            }
            kyara1.setX(kyarax);
        }
    }
}