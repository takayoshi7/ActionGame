package com.example.actiongame;

import android.content.SharedPreferences;
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
    private ImageView rect2;
    private ImageView space1;
    // スコア表示部
    private TextView scoretext;
    // ゲームオーバー表示部
    private TextView frametext;

    private TextView speedtext;
    // キャラクターを左右に動かすボタン
    private Button leftbtn;
    private Button rightbtn;
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
    // キャラクターが通れる障害物の隙間のサイズ
    private float spacewidth;
    private float spaceheight;
    private int spacemoverange;
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
    // ステージ判定用
    private int stagephase = 1;
    // 内部ストレージ保存用
    private SharedPreferences actgamepref;
    private int speedint = 1;
    private int longint = 1;

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

        actgamepref = getSharedPreferences("actgamepref", MODE_PRIVATE);

        speedint = actgamepref.getInt("actgamespeed", 1);
        longint = actgamepref.getInt("actgamelong", 1);
        stagephase = actgamepref.getInt("actgamestage", 1);

        if (speedint <= 10 && speedint >= 1) {
            kyaraspeed = (speedint) * 10f;
        }

        if (longint <= 10 && longint >= 1) {
            kyaraspeedlong = (longint) * 1f;
        }

        // 画像をIDで取得
        kyara1 = findViewById(R.id.kyara1);
        rect = findViewById(R.id.rect);
        rect2 = findViewById(R.id.rect2);
        space1 = findViewById(R.id.space1);
        // スコアテキストエリアをIDで取得
        scoretext = findViewById(R.id.scoretext);
        // ゲームエリアをIDで取得
        frame = findViewById(R.id.frame);
        // ボタン以外のテキストやレイアウトなどにクリック機能を付けるときに必要
        frame.setClickable(true);
        // クリック機能
        frame.setOnClickListener(this);
        // ゲームオーバーエリアをIDで取得
        frametext = findViewById(R.id.frametext);
        // スピードレベル表示エリアをIDで取得
        speedtext = findViewById(R.id.speedtext);
        // ボタンをIDで取得
        leftbtn = findViewById(R.id.leftbtn);
        leftbtn.setOnClickListener(this);
        leftbtn.setOnLongClickListener(this);
        leftbtn.setOnTouchListener(this);
        rightbtn = findViewById(R.id.rightbtn);
        rightbtn.setOnClickListener(this);
        rightbtn.setOnLongClickListener(this);
        rightbtn.setOnTouchListener(this);

        // スピードレベルの表示テキストセット
        speedtext.setText("SpeedLv:" + speedint + " LongLv:" + longint);

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

        // ステージ1なら処理
        if (stagephase == 1) {
            // ステージ1用障害物表示
            rect.setVisibility(View.VISIBLE);

            ViewTreeObserver obser2 = rect.getViewTreeObserver();
            obser2.addOnGlobalLayoutListener(() -> {
                // 障害物のサイズ取得
                rectwidth = rect.getWidth();
                rectheight = rect.getHeight();
                // ゲームエリアのサイズ取得
                screenx = frame.getWidth();
                screeny = frame.getHeight();
                // 障害物が画面からはみ出ないように範囲取得
                rectmoverange = (int)screenx + 1 - (int)rectwidth;
                // ランダム配置取得
                int randomX = new Random().nextInt(rectmoverange);
                rectx = (float)randomX;
                // 配置設定
                rect.setX(rectx);
                rect.setY(recty);
            });

            // ステージ2用障害物と隙間を非表示
            rect2.setVisibility(View.INVISIBLE);
            space1.setVisibility(View.INVISIBLE);
        } else if (stagephase == 2) {
            // ステージ2用障害物と隙間を表示
            rect2.setVisibility(View.VISIBLE);
            space1.setVisibility(View.VISIBLE);

            ViewTreeObserver obser3 = space1.getViewTreeObserver();
            obser3.addOnGlobalLayoutListener(() -> {
                // 隙間のサイズ取得
                spacewidth = space1.getWidth();
                spaceheight = space1.getHeight();
                // 障害物のサイズ取得
                rectheight = space1.getHeight();
                // ゲームエリアのサイズ取得
                screenx = frame.getWidth();
                screeny = frame.getHeight();
                // 隙間が画面からはみ出ないように範囲取得
                spacemoverange = (int)screenx + 1 - (int)spacewidth;
                // ランダム配置取得
                int randomX = new Random().nextInt(spacemoverange);
                rectx = (float)randomX;

                // 配置設定
                space1.setX(rectx);
                space1.setY(recty);
                rect2.setX(0f);
                rect2.setY(recty);
            });

            // ステージ1用障害物を非表示
            rect.setVisibility(View.INVISIBLE);
        }

        // 初期スコア表示
        scoretext.setText("score:" + score);
    }

    // 処理
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
                        if (stagephase == 1) {
                            rectx = random.nextInt(rectmoverange);
                        } else if (stagephase == 2) {
                            rectx = random.nextInt(spacemoverange);
                        }

                        // スコアを1追加して表示
                        score++;
                        scoretext.setText("score:" + score);
                    }

                    // 左ボタン長押し時の動作
                    if (leftphase) {
                        if (kyarax <= kyaraspeedlong) {
                            kyarax = 0f;
                        } else {
                            kyarax -= kyaraspeedlong;
                        }
                        kyara1.setX(kyarax);
                    }

                    // 右ボタン長押し時の動作
                    if (rightphase) {
                        if (kyarax >= (screenx - kyarawidth - kyaraspeedlong)) {
                            kyarax = (screenx - kyarawidth);
                        } else {
                            kyarax += kyaraspeedlong;
                        }
                        kyara1.setX(kyarax);
                    }

                    // 衝突判定
                    if (stagephase == 1) {
                        stage1check();
                    } else if (stagephase == 2) {
                        stage2check();
                    }
                }
            });
        }
    }

    // stage1の衝突判定
    private void stage1check() {
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

    // stage2の衝突判定
    private void stage2check() {
        if ((recty + rectheight) >= kyaray && recty <= (kyaray + kyaraheight) &&
                !(kyarax >= rectx && kyarax <= (rectx + spacewidth) && (rectx + spacewidth) >= rectx &&
                        (kyarax + kyarawidth) <= (rectx + spacewidth))) {
            // 処理停止
            clickphase = true;
            // ゲームオーバー表示
            frametext.setVisibility(View.VISIBLE);
            frametext.setText("GAME OVER");
        }

        space1.setX(rectx);
        space1.setY(recty);
        rect2.setX(0f);
        rect2.setY(recty);
    }

    // タッチイベント
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

    // 長押しイベント
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

    // クリックイベント
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.frame) {
            frametext.setVisibility(View.INVISIBLE);
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