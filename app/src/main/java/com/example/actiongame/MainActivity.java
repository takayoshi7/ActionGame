package com.example.actiongame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView maintext;
    // ゲームスタートボタン
    private Button mainstartbtn;
    // レベル設定画面遷移ボタン
    private Button mainlevelbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // アプリのコンテンツがシステムバー（ステータスバーやナビゲーションバー）の背後に表示され、画面全体を利用できる（最新Androidバージョンで推奨）
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // システムバー（ステータスバーやナビゲーションバー）の領域を考慮したレイアウト作成（パディング設定）
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // ステータスバーやナビゲーションバーのインセットを取得
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // 取得したインセットに基づいて、ビューのパディングを設定
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // インセットを返す
            return insets;
        });

        maintext = findViewById(R.id.maintext);

        // ボタンをIDで取得
        mainstartbtn = findViewById(R.id.mainstartbtn);
        mainstartbtn.setOnClickListener(this);
        mainlevelbtn = findViewById(R.id.mainlevelbtn);
        mainlevelbtn.setOnClickListener(this);
    }

    // ボタンクリックによる画面遷移
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mainstartbtn) {
            // GameActivity画面を表示
            Intent intent = new Intent(getApplication(), GameActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.mainlevelbtn) {
            // LevelActivity画面を表示
            Intent level = new Intent(getApplication(), LevelActivity.class);
            startActivity(level);
        }
    }
}