package com.dorm.smartterminal.settings.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.settings.localsetting.activity.LocalSetting;
import com.dorm.smartterminal.settings.securitysetting.activity.SecuritySetting;

/***
 * main view of setting select page
 * 
 * @author andy liu
 * 
 */
public class Settings extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__settings);

        /*
         * init
         */

        initButtons();

    }

    /*
     * init
     */

    private void initButtons() {

        findViewById(R.id.local_setting).setOnClickListener(this);
        findViewById(R.id.security_setting).setOnClickListener(this);
        findViewById(R.id.motinor_setting).setOnClickListener(this);

    }

    /*
     * logic
     */

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.local_setting:
            ActivityUtil.intentActivity(Settings.this, LocalSetting.class);
            break;

        case R.id.security_setting:
            ActivityUtil.intentActivity(Settings.this, SecuritySetting.class);
            break;

        case R.id.motinor_setting:
            break;
        }
    }
}
