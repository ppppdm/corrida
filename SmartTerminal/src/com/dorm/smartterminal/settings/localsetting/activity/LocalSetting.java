package com.dorm.smartterminal.settings.localsetting.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.db.DBHelper;
import com.dorm.smartterminal.global.db.bean.Bean;
import com.dorm.smartterminal.global.db.config.DataBaseConfig;
import com.dorm.smartterminal.global.db.interfaces.DataBaseQueryInterface;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.settings.localsetting.bean.Address;
import com.dorm.smartterminal.settings.localsetting.bean.OtherIP;

/***
 * local setting page
 * 
 * @author andy liu
 * 
 */
public class LocalSetting extends Activity implements OnClickListener, DataBaseQueryInterface {

    /*
     * db
     */

    // define customer query type
    private final static int GET_ADDRESS = 1;
    private final static int UPDATE_ADDRESS = 2;
    private final static int GET_OTHER_IP = 3;
    private final static int UPDATE_OTHER_IP = 4;

    /*
     * ui
     */
    private TextView buildingPhase = null;
    private TextView area = null;
    private TextView buildingGroup = null;
    private TextView building = null;
    private TextView door = null;
    private TextView localDeviceId = null;

    private TextView outsideDoorDeviceIp = null;
    private TextView outsideBuildingDeviceIp = null;
    private TextView centerServerIp = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__settings__local_setting);

        /*
         * init
         */

        initButtons();

        /*
         * logic
         */
        getAddress();
        getOtherIP();

    }

    /*
     * init
     */

    private void initButtons() {

        // get buttons
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        // text view
        buildingPhase = (TextView) findViewById(R.id.building_phase);
        area = (TextView) findViewById(R.id.area);
        buildingGroup = (TextView) findViewById(R.id.building_group);
        building = (TextView) findViewById(R.id.building);
        door = (TextView) findViewById(R.id.door);
        localDeviceId = (TextView) findViewById(R.id.local_device_id);

        outsideDoorDeviceIp = (TextView) findViewById(R.id.outside_door_device_ip);
        outsideBuildingDeviceIp = (TextView) findViewById(R.id.outside_building_device_ip);
        centerServerIp = (TextView) findViewById(R.id.center_server_ip);

    }

    /*
     * logic
     */

    private void getAddress() {

        Address address = new Address(1);

        DBHelper.query(DataBaseConfig.QueryTypes.SEARCH, GET_ADDRESS, address, this, false,
                DataBaseConfig.DEFAULT_ACTIVATOIN_DEPTH);

    }

    private void getOtherIP() {

        OtherIP otherIP = new OtherIP(2);

        DBHelper.query(DataBaseConfig.QueryTypes.SEARCH, GET_OTHER_IP, otherIP, this, false,
                DataBaseConfig.DEFAULT_ACTIVATOIN_DEPTH);

    }

    @Override
    public void onDataBaseQueryFinish(int transactionId, int customType, boolean isSuccess, int errorCode,
            List<? extends Bean> result) {

        switch (customType) {
        case GET_ADDRESS:
            showAddress(result);
            LogUtil.log(this, "init address success");
            break;
        case UPDATE_ADDRESS:
            showAddress(result);
            LogUtil.log(this, "update address success");
            break;
        case GET_OTHER_IP:
            showOtherIP(result);
            LogUtil.log(this, "init other ip success");
            break;
        case UPDATE_OTHER_IP:
            showOtherIP(result);
            LogUtil.log(this, "update other ip success");
            break;
        }

    }

    private void showAddress(List<? extends Bean> beans) {

        if (null != beans && !beans.isEmpty()) {

            Bean bean = beans.get(0);

            buildingPhase.setText(((Address) bean).buildingPhase);
            area.setText(((Address) bean).area);
            buildingGroup.setText(((Address) bean).buildingGroup);
            building.setText(((Address) bean).building);
            door.setText(((Address) bean).door);
            localDeviceId.setText(((Address) bean).localDeviceId);
        }
    }

    private void showOtherIP(List<? extends Bean> beans) {

        if (null != beans && !beans.isEmpty()) {

            Bean bean = beans.get(0);

            outsideDoorDeviceIp.setText(((OtherIP) bean).outsideDoorDeviceIp);
            outsideBuildingDeviceIp.setText(((OtherIP) bean).outsideBuildingDeviceIp);
            centerServerIp.setText(((OtherIP) bean).centerServerIp);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.back:
            ActivityUtil.closeActivity(this);
            break;
        case R.id.save:
            updateAddress();
            updateOtherIP();
            break;
        }

    }

    private void updateAddress() {

        Address address = new Address(1);

        DBHelper.query(DataBaseConfig.QueryTypes.UPDATE, UPDATE_ADDRESS, address, this, false, 0);

    }

    private void updateOtherIP() {

        OtherIP otherIP = new OtherIP(2);

        DBHelper.query(DataBaseConfig.QueryTypes.UPDATE, UPDATE_OTHER_IP, otherIP, this, false, 0);

    }

    @Override
    public void doBeanMotification(int transactionId, int customType, List<? extends Bean> result) {

        switch (customType) {
        case UPDATE_ADDRESS:
            updateAddress((Address) result.get(0));
            LogUtil.log(this, "motify address success");
            break;
        case UPDATE_OTHER_IP:
            updateOtherIP((OtherIP) result.get(0));
            LogUtil.log(this, "motify address success");
            break;

        }

    }

    private void updateAddress(Address address) {

        address.buildingPhase = buildingPhase.getText().toString().trim();
        address.area = area.getText().toString().trim();
        address.buildingGroup = buildingGroup.getText().toString().trim();
        address.building = building.getText().toString().trim();
        address.door = door.getText().toString().trim();
        address.localDeviceId = localDeviceId.getText().toString().trim();
    }

    private void updateOtherIP(OtherIP otherIP) {

        otherIP.outsideDoorDeviceIp = outsideDoorDeviceIp.getText().toString().trim();
        otherIP.outsideBuildingDeviceIp = outsideBuildingDeviceIp.getText().toString().trim();
        otherIP.centerServerIp = centerServerIp.getText().toString().trim();
    }

}
