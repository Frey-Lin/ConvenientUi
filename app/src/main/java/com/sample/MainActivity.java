package com.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.scorpio.ui.dialog.MenuDialog;
import com.scorpio.ui.widget.CountDownButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CountDownButton countDownButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.btnShowMenuDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MenuDialog.MenuItem> items = new ArrayList<>();
                items.add(new MenuDialog.MenuItem(123, "upload"));
                items.add(new MenuDialog.MenuItem(122, "download"));
                items.add(new MenuDialog.MenuItem(132, "delete"));
                items.add(new MenuDialog.MenuItem(132, "delete"));
                items.add(new MenuDialog.MenuItem(132, "delete"));
                MenuDialog dialog = new MenuDialog.Builder(MainActivity.this).items(items).create();
                dialog.show();
            }
        });

        countDownButton = findViewById(R.id.btnCountDown);
        countDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownButton.startCountDown();
            }
        });

    }


}
