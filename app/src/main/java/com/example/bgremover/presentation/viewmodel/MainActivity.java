package com.example.bgremover.presentation.viewmodel;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("Hello, World!");
        textView.setTextSize(24);
        textView.setPadding(16, 16, 16, 16);

        setContentView(textView);
    }
}

