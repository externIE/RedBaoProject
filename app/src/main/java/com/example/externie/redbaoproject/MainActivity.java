package com.example.externie.redbaoproject;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_startService;
    private Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_startService = (Button)findViewById(R.id.startService);
        btn_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到设置界面，让用户打开监听服务
                startActivity(accessibleIntent);
            }
        });
    }
}
