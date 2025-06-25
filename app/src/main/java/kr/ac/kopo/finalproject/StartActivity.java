package kr.ac.kopo.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView welcomeText = findViewById(R.id.welcomeText);
        Button startButton = findViewById(R.id.startButton);
        ImageView logoImage = findViewById(R.id.logoImage);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 시작 화면은 뒤로가기 시 다시 안 나오게 종료
            }
        });
    }
}
