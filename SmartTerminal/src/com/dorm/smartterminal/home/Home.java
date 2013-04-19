package com.dorm.smartterminal.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.netchat.activiy.NetChart;

public class Home extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__home);

        /*
         * view
         */

        initButtons();

    }

    private void initButtons() {

        findViewById(R.id.net_chart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.net_chart:
            ActivityUtil.intentActivity(this, NetChart.class);
            break;
        }

    }

}
