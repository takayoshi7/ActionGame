package com.example.actiongame;

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

        mainstartbtn = findViewById(R.id.mainstartbtn);
        mainstartbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mainstartbtn) {
            maintext.setText("TAP");
        }

//        switch(view.getId()) {
//            case(R.id.mainstartbtn):
//                maintext.setText("TAP");
//                break;
//        }
    }

}