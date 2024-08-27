package com.example.actiongame;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Runnable, View.OnLongClickListener, View.OnTouchListener {
    // キャラクターと障害物の画像
    private ImageView kyara1;
    private ImageView rect;
    // スコア表示部
    private TextView scoretext;
    // ゲームオーバー表示部
    private TextView frametext;
    // キャラクターを左右に動かすボタン
    private Button leftbtn;
    private Button rightbtn;
    // ゲームオーバー時リスタートボタン
    private Button framebtn;
    // ゲームエリアのレイアウト
    private FrameLayout frame;
    // キャラクターと障害物の初期位置
    private float rectx;
    private float recty = -170f;
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
    // キャラクターのスピード
    private float kyaraspeed = 10f;
    private float kyaraspeedlong = 1f;
    // スコアカウント用
    private int score = 0;
    // 処理停止判定　true:停止　false:続行
    private volatile boolean clickphase = false;
    // Handlerクラスのインスタンス化
    private final Handler handler = new Handler(Looper.getMainLooper());
    // ボタンが長押しされたか判定用
    private boolean leftphase = false;
    private boolean rightphase =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 画像をIDで取得
        kyara1 = findViewById(R.id.kyara1);
        rect = findViewById(R.id.rect);
        // スコアテキストエリアをIDで取得
        scoretext = findViewById(R.id.scoretext);
        // ゲームエリアをIDで取得
        frame = findViewById(R.id.frame);
        // ゲームオーバーエリアをIDで取得
        frametext = findViewById(R.id.frametext);
        // 非表示に
        frametext.setVisibility(View.INVISIBLE);
        // ボタンをIDで取得
        framebtn = findViewById(R.id.framebtn);
        framebtn.setOnClickListener(this);
        leftbtn = findViewById(R.id.leftbtn);
        leftbtn.setOnClickListener(this);
        leftbtn.setOnLongClickListener(this);
        leftbtn.setOnTouchListener(this);
        rightbtn = findViewById(R.id.rightbtn);
        rightbtn.setOnClickListener(this);
        rightbtn.setOnLongClickListener(this);
        rightbtn.setOnTouchListener(this);

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
        });

        // 初期スコア表示
        scoretext.setText("score:" + score);
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
                    if (recty > screeny) {
                        recty = -100f;

                        // 障害物の横位置をランダムにする
                        Random random = new Random();
                        int x = random.nextInt(rectmoverange);
                        rectx = x;
                        score++;
                        scoretext.setText("score:" + score);
                    }

                    // 左ボタン長押し時の動作
                    if (leftphase == true) {
                        if (kyarax <= kyaraspeedlong) {
                            kyarax = 0f;
                        } else {
                            kyarax -= kyaraspeedlong;
                        }
                        kyara1.setX(kyarax);
                    }

                    // 右ボタン長押し時の動作
                    if (rightphase == true) {
                        if (kyarax >= (screenx - kyarawidth - kyaraspeedlong)) {
                            kyarax = (screenx - kyarawidth);
                        } else {
                            kyarax += kyaraspeedlong;
                        }
                        kyara1.setX(kyarax);
                    }

                    // 衝突判定
                    if ((recty + rectheight) >= kyaray && recty <= (kyaray + kyaraheight) &&
                            (rectx + rectwidth) > kyarax && rectx < (kyarax + kyarawidth)) {
                        // 処理停止
                        clickphase = true;
                        // ゲームオーバー表示
                        frametext.setVisibility(View.VISIBLE);
                        frametext.setText("GAME OVER");
                    }

                    // 配置場所をセット
                    rect.setX(rectx);
                    rect.setY(recty);
                }
            });
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
//            clickphase = true;
            leftphase = false;
            rightphase = false;
        }
        return false;
    }

    // ボタン長押し
    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.leftbtn) {
            clickphase = false;
            leftphase = true;
        } else if (view.getId() == R.id.rightbtn) {
            clickphase = false;
            rightphase = true;
        }
        return true;
    }

    // ボタン押下
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.framebtn) {
            // スタートボタンを押したらスタート
            framebtn.setVisibility(View.INVISIBLE);
            clickphase = false;
            Thread thread = new Thread(this);
            thread.start();
        } else if (view.getId() == R.id.leftbtn) {
            // 左ボタンを押したらキャラクターが移動する
            if (kyarax <= kyaraspeed) {
                kyarax = 0f;
            } else {
                kyarax -= kyaraspeed;
            }
            kyara1.setX(kyarax);
        } else if (view.getId() == R.id.rightbtn) {
            // 右ボタンを押したらキャラクターが移動する
            if (kyarax >= (screenx - kyarawidth - kyaraspeed)) {
                kyarax = (screenx - kyarawidth);
            } else {
                kyarax += kyaraspeed;
            }
            kyara1.setX(kyarax);
        }
    }
}