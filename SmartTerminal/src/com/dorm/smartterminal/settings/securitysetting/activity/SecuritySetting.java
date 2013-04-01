package com.dorm.smartterminal.settings.securitysetting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.bean.Page;
import com.dorm.smartterminal.global.widget.TabPageWithTabs;
import com.dorm.smartterminal.settings.securitysetting.sensorsetting.activity.SensorSetting;
import com.dorm.smartterminal.settings.securitysetting.strategysetting.activity.StrategySetting;

/***
 * main frame of security setting
 * 
 * @author andy liu
 * 
 */
public class SecuritySetting extends TabPageWithTabs implements OnClickListener {

    /*
     * ui
     */

    // define buttons
    Button back = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.st__settings__security_setting);

        addPage(new Page("StrategySetting", new Intent(this, StrategySetting.class)));
        addPage(new Page("SensorSetting", new Intent(this, SensorSetting.class)));

        addTab((Button) findViewById(R.id.strategy_setting));
        addTab((Button) findViewById(R.id.sensor_setting));

        setTab(0);

        /*
         * ui
         */

        // get buttons
        back = (Button) findViewById(R.id.back);

        // set onclick listener
        back.setOnClickListener(this);

    }

    /* 
     * logic
     */

    @Override
    public void onClick(View v) {

        if (v == back) {

            this.finish();

        }

    }

}
