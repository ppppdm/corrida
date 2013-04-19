package com.dorm.smartterminal.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.application.SmartTerminalApplication;
import com.dorm.smartterminal.global.widget.TabPageWithTabs;
import com.dorm.smartterminal.global.widget.bean.Page;
import com.dorm.smartterminal.home.Home;
import com.dorm.smartterminal.settings.activity.Settings;

/***
 * main frame of this program
 * 
 * @author andy liu
 * 
 */
public class Main extends TabPageWithTabs {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.st__main);

        /*
         * init
         */

        initTabPageView();

    }

    /*
     * init
     */

    private void initTabPageView() {

        addPage(new Page("home", new Intent(Main.this, Home.class)));
        addPage(new Page("settings", new Intent(Main.this, Settings.class)));

        addTab((Button) findViewById(R.id.home));
        addTab((Button) findViewById(R.id.settings));

        setTab(0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        exitApplication();
    }

    private void exitApplication() {

        ((SmartTerminalApplication) this.getApplication()).exitApplication();

    }

}
