package com.dorm.smartterminal.settings.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.IntentUtil;
import com.dorm.smartterminal.settings.localsetting.activity.LocalSetting;
import com.dorm.smartterminal.settings.securitysetting.activity.SecuritySetting;

/***
 * main view of setting select page
 * 
 * @author andy liu
 * 
 */
public class Settings extends Activity implements OnClickListener {

    /*
     * ui
     */

    // define buttons
    Button localSetting = null;
    Button securitySetting = null;
    Button monitorSetting = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__settings);

        /*
         * ui
         */

        // get buttons
        localSetting = (Button) findViewById(R.id.local_setting);
        securitySetting = (Button) findViewById(R.id.security_setting);
        monitorSetting = (Button) findViewById(R.id.motinor_setting);

        // set onclick listener
        localSetting.setOnClickListener(this);
        securitySetting.setOnClickListener(this);
        monitorSetting.setOnClickListener(this);

    }

    /*
     * logic
     */

    @Override
    public void onClick(View v) {

        if (v == localSetting) {

            IntentUtil.intentActivity(Settings.this, LocalSetting.class);

        }
        else if (v == securitySetting) {

            IntentUtil.intentActivity(Settings.this, SecuritySetting.class);

        }

    }
}
