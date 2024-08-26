package com.example.actiongame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView kyara1;
    private ImageView rect;

    private Button leftbtn;
    private Button rightbtn;

    private float rectx = 50f;
    private  float kyarax = 500f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        kyara1 = findViewById(R.id.kyara1);
        rect = findViewById(R.id.rect);

        leftbtn = findViewById(R.id.leftbtn);
        leftbtn.setOnClickListener(this);
        rightbtn = findViewById(R.id.rightbtn);
        rightbtn.setOnClickListener(this);

        rect.setX(rectx);
        rect.setY(50f);
        kyara1.setX(kyarax);
        kyara1.setY(500f);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.leftbtn) {
            kyarax -= 10f;
            kyara1.setX(kyarax);
        } else if (view.getId() == R.id.rightbtn) {
            kyarax += 10f;
            kyara1.setX(kyarax);
        }
    }
}