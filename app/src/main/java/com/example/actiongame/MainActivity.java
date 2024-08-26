package com.example.actiongame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView maintext;
    private Button mainstartbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        maintext = findViewById(R.id.maintext);

        // ボタンをIDで取得
        mainstartbtn = findViewById(R.id.mainstartbtn);
        mainstartbtn.setOnClickListener(this);
    }

    // GameActivity画面を表示
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mainstartbtn) {
            Intent intent = new Intent(getApplication(), GameActivity.class);
            startActivity(intent);
        }
    }
}