package com.dorm.smartterminal.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.bean.Page;
import com.dorm.smartterminal.global.widget.TabPageWithTabs;
import com.dorm.smartterminal.settings.activity.Settings;
import com.dorm.smartterminal.test.activity.Activity1;
import com.dorm.smartterminal.test.activity.Activity2;
import com.dorm.smartterminal.test.activity.Activity3;

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

        addPage(new Page("a", new Intent(Main.this, Activity1.class)));
        addPage(new Page("b", new Intent(Main.this, Activity2.class)));
        addPage(new Page("c", new Intent(Main.this, Activity3.class)));
        addPage(new Page("she_zhi", new Intent(Main.this, Settings.class)));

        addTab((Button) findViewById(R.id.home));
        addTab((Button) findViewById(R.id.monitor));
        addTab((Button) findViewById(R.id.household_electrical_appliances));
        addTab((Button) findViewById(R.id.settings));

        setTab(0);

    }

}
