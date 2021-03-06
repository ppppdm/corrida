package com.dorm.smartterminal.loader.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.config.GlobalConfig;
import com.dorm.smartterminal.global.db.DBHelper;
import com.dorm.smartterminal.global.db.config.DataBaseConfig;
import com.dorm.smartterminal.global.db.interfaces.DataBaseQueryInterface;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.global.util.FileSystemUtil;
import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.main.activity.Main;
import com.dorm.smartterminal.service.NetCommunicationService;
import com.dorm.smartterminal.settings.localsetting.bean.Address;
import com.dorm.smartterminal.settings.localsetting.bean.OtherIP;

public class Loader extends Activity implements DataBaseQueryInterface {

    /*
     * db
     */
    private final static int CREATE_ADDRESS = 1;
    private final static int CREATE_OTHER_IP = 2;

    /*
     * log
     */
    private final static String TAG = "Loader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__loader);

        /*
         * db
         */
        initDataBase();
        
        /*
         * services
         */
        initService();

    }
    
    private void initService(){
    	//
    	this.startService(new Intent(this, NetCommunicationService.class));
    }

    private void initDataBase() {

        // data base
        rebuildDataBase();

        // objects
        initAddress();
        initOtherIP();

        LogUtil.log(this, "init data base success.");

    }

    private void rebuildDataBase() {

        // create dir
        FileSystemUtil.createFolder(GlobalConfig.LOCAL_FILE_DIR_ROOT);

        // delete db file
        new File(GlobalConfig.LOCAL_FILE_DIR_ROOT + DataBaseConfig.DATA_BASE_FILE_NAME).delete();

        LogUtil.log(this, "rebuild data base success.");
    }

    private void initAddress() {

        Address address = new Address();
        address.buildingPhase = "";
        address.area = "";
        address.buildingGroup = "";
        address.building = "";
        address.door = "";
        address.localDeviceId = "";

        DBHelper.query(DataBaseConfig.QueryTypes.INSERT, CREATE_ADDRESS, address, this, false, 0);
    }

    private void initOtherIP() {

        OtherIP otherIP = new OtherIP();
        otherIP.outsideDoorDeviceIp = "";
        otherIP.outsideBuildingDeviceIp = "";
        otherIP.centerServerIp = "";

        DBHelper.query(DataBaseConfig.QueryTypes.INSERT, CREATE_OTHER_IP, otherIP, this, false, 0);
    }

    @Override
    public void doBeanMotification(int transactionId, int customType, List<?> result) {

    }

    @Override
    public void onDataBaseQueryFinish(int transactionId, int customType, boolean isSuccess, int errorCode,
            List<?> result) {

        switch (customType) {
        case CREATE_ADDRESS:
            LogUtil.log(TAG, "Create database : create Address success.");
            break;
            
        case CREATE_OTHER_IP:
            jumpToMainActivity();
            LogUtil.log(TAG, "Create database : create OtherIP success.");
            break;
        }
    }

    private void jumpToMainActivity() {

        ActivityUtil.intentActivity(this, Main.class);
        ActivityUtil.closeActivity(this);
    }

}
