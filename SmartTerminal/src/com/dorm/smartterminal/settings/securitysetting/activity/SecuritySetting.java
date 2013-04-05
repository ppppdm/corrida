package com.dorm.smartterminal.settings.securitysetting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.global.widget.TabPageWithTabs;
import com.dorm.smartterminal.global.widget.bean.Page;
import com.dorm.smartterminal.settings.securitysetting.areasetting.activity.AreaSetting;
import com.dorm.smartterminal.settings.securitysetting.delaysetting.activity.DelaySetting;
import com.dorm.smartterminal.settings.securitysetting.passwordsetting.activity.PasswordSetting;
import com.dorm.smartterminal.settings.securitysetting.sensordirectlink.activity.SensorDirectLink;
import com.dorm.smartterminal.settings.securitysetting.sensorsetting.activity.SensorSetting;
import com.dorm.smartterminal.settings.securitysetting.strategysetting.activity.StrategySetting;

/***
 * main frame of security setting
 * 
 * @author andy liu
 * 
 */
public class SecuritySetting extends TabPageWithTabs implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.st__settings__security_setting);

        /*
         * init
         */

        initTabPageView();
        initButtons();

    }

    /*
     * init
     */

    private void initTabPageView() {

        addPage(new Page("StrategySetting", new Intent(this, StrategySetting.class)));
        addPage(new Page("AreaSetting", new Intent(this, AreaSetting.class)));
        addPage(new Page("SensorSetting", new Intent(this, SensorSetting.class)));
        addPage(new Page("DelaySetting", new Intent(this, DelaySetting.class)));
        addPage(new Page("PasswordSetting", new Intent(this, PasswordSetting.class)));
        addPage(new Page("SensorDirectLink", new Intent(this, SensorDirectLink.class)));

        addTab((Button) findViewById(R.id.strategy_setting));
        addTab((Button) findViewById(R.id.area_setting));
        addTab((Button) findViewById(R.id.sensor_setting));
        addTab((Button) findViewById(R.id.delay_setting));
        addTab((Button) findViewById(R.id.password_setting));
        addTab((Button) findViewById(R.id.sensor_direct_link));

        setTab(0);

    }

    private void initButtons() {

        findViewById(R.id.back).setOnClickListener(this);

    }

    /* 
     * logic
     */

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.back:
            ActivityUtil.closeActivity(this);
            break;
        }

    }

}
