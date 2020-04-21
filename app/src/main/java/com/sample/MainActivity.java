package com.sample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.scorpio.ui.dialog.MenuDialog;
import com.scorpio.ui.drawable.CircularProgressDrawable;
import com.scorpio.ui.util.DensityUtil;
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


        findViewById(R.id.gotoRefreshActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RefreshAndLoadMoreActivity.class));
            }
        });

        ImageView circleImg = findViewById(R.id.circleImg);
        CircularProgressDrawable drawable = new CircularProgressDrawable(this, Color.RED, DensityUtil.dip2px(this,10));
        circleImg.setImageDrawable(drawable);
        drawable.start();

    }


}
