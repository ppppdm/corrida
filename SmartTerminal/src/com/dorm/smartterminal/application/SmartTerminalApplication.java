/**
 * 
 */
package com.dorm.smartterminal.application;

import com.dorm.smartterminal.global.db.DBHelper;
import com.dorm.smartterminal.global.util.LogUtil;

import android.app.Application;

/**
 * @author Andy
 * 
 */
public class SmartTerminalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        initDBHelper();
    }

    private void initDBHelper() {

        // db helper
        DBHelper.initDBhelper();

        LogUtil.log(this, "Create application success.");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    
    private void destoryDBhelper(){

        // db helper
        DBHelper.destoryDBhelper();

        LogUtil.log(this, "Terminal application success.");
    }

    public void exitApplication() {

        System.exit(0);
    }

}
