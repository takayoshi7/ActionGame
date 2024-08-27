package com.example.actiongame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LevelActivity extends AppCompatActivity  implements View.OnClickListener {
    // バックボタン
    private Button levelback;
    // レベル設定ボタン
    private Button levelbtn;
    // スピードレベル
    private EditText speededit;
    // ロングスピードレベル
    private EditText longedit;
    // 内部ストレージ保存用
    private SharedPreferences actgamepref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.level), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 内部ストレージに保存できるようにファイル作成（第1引数：ファイル名、第2引数：現在はPRIVATEのみ）
        actgamepref = getSharedPreferences("actgamepref", MODE_PRIVATE);
        // 値を取得（第1引数：キー、第2引数：キーが存在しない場合のデフォルト値）
        int speedint = actgamepref.getInt("actgamespeed", 1);
        int longint = actgamepref.getInt("actgamelong", 1);

        // スピードレベル設定値をIDで取得
        speededit = findViewById(R.id.levelspeedit);
        // ロングスピードレベル設定値をIDで取得
        longedit = findViewById(R.id.levellongedit);
        // ボタンをIDで取得
        levelback = findViewById(R.id.levelback);
        levelback.setOnClickListener(this);
        levelbtn = findViewById(R.id.levelbtn);
        levelbtn.setOnClickListener(this);
        // スピードレベルのテキストをセット
        speededit.setText("" + speedint);
        longedit.setText("" + longint);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.levelback) {
            // バックボタンを押した場合
            // MainActivityのインテントを作成
            Intent intent = new Intent(getApplication(), MainActivity.class);
            // 現在のActivityを終了
            finish();
            // MainActivityを開始（MainActivityに遷移）
            startActivity(intent);
        } else if (view.getId() == R.id.levelbtn) {
            // レベル設定ボタンを押した場合
            // スピードレベルのnullチェック
            if (!speededit.getText().toString().equals("")) {
                // ロングスピードレベルのnullチェック
                if (!longedit.getText().toString().equals("")) {
                    // edit()でEditorオブジェクトを作成
                    SharedPreferences.Editor actedit = actgamepref.edit();
                    // 数値として設定（第1引数：key、第2引数：Value）文字列を保存するならputStringを使う
                    actedit.putInt("actgamespeed", Integer.parseInt(speededit.getText().toString()));
                    actedit.putInt("actgamelong", Integer.parseInt(longedit.getText().toString()));
                    // 設定を保存
                    actedit.apply();
                }
            }
        }
    }
}