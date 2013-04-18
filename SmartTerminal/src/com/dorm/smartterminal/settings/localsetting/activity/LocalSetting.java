package com.dorm.smartterminal.settings.localsetting.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.db.DBHelper;
import com.dorm.smartterminal.global.db.config.DataBaseConfig;
import com.dorm.smartterminal.global.db.interfaces.DataBaseQueryInterface;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.global.util.WifiUtil;
import com.dorm.smartterminal.global.util.bean.WifiDhcpInfo;
import com.dorm.smartterminal.settings.localsetting.bean.Address;
import com.dorm.smartterminal.settings.localsetting.bean.OtherIP;

/***
 * local setting page
 * 
 * @author andy liu
 * 
 */
public class LocalSetting extends Activity implements OnClickListener, OnLongClickListener, // OnFocusChangeListener,
        DataBaseQueryInterface {

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

    private TextView ip = null;
    private TextView netmask = null;
    private TextView gateway = null;
    private TextView dns = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__settings__local_setting);

        /*
         * init
         */

        initView();

        /*
         * logic
         */
        getAddress();
        getOtherIP();

    }

    /*
     * init
     */

    @Override
    protected void onResume() {
        super.onResume();

        updateWifiDhcpInfo();
    }

    private void updateWifiDhcpInfo() {

        WifiDhcpInfo wifiDhcpInfo = WifiUtil.WifiDhcpInfo(this);
        ip.setText(wifiDhcpInfo.ipAddress);
        netmask.setText(wifiDhcpInfo.netmask);
        gateway.setText(wifiDhcpInfo.gateway);
        dns.setText(wifiDhcpInfo.DNS1);

    }

    private void initView() {

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

        ip = (TextView) findViewById(R.id.ip);
        netmask = (TextView) findViewById(R.id.netmask);
        gateway = (TextView) findViewById(R.id.gateway);
        dns = (TextView) findViewById(R.id.dns);

        // ip.setOnFocusChangeListener(this);
        // netmask.setOnFocusChangeListener(this);
        // gateway.setOnFocusChangeListener(this);
        // dns.setOnFocusChangeListener(this);

        ip.setOnLongClickListener(this);
        netmask.setOnLongClickListener(this);
        gateway.setOnLongClickListener(this);
        dns.setOnLongClickListener(this);

        // ip.setOnClickListener(this);
        // netmask.setOnClickListener(this);
        // gateway.setOnClickListener(this);
        // dns.setOnClickListener(this);

    }

    /*
     * logic
     */

    private void getAddress() {

        Address address = new Address();

        DBHelper.query(DataBaseConfig.QueryTypes.SEARCH, GET_ADDRESS, address, this, false,
                DataBaseConfig.DEFAULT_ACTIVATOIN_DEPTH);

    }

    private void getOtherIP() {

        OtherIP otherIP = new OtherIP();

        DBHelper.query(DataBaseConfig.QueryTypes.SEARCH, GET_OTHER_IP, otherIP, this, false,
                DataBaseConfig.DEFAULT_ACTIVATOIN_DEPTH);

    }

    @Override
    public void onDataBaseQueryFinish(int transactionId, int customType, boolean isSuccess, int errorCode,
            List<?> result) {

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

    private void showAddress(List<?> beans) {

        if (null != beans && !beans.isEmpty()) {

            Object bean = beans.get(0);

            buildingPhase.setText(((Address) bean).buildingPhase);
            area.setText(((Address) bean).area);
            buildingGroup.setText(((Address) bean).buildingGroup);
            building.setText(((Address) bean).building);
            door.setText(((Address) bean).door);
            localDeviceId.setText(((Address) bean).localDeviceId);
        }
    }

    private void showOtherIP(List<?> beans) {

        if (null != beans && !beans.isEmpty()) {

            Object bean = beans.get(0);

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

        // case R.id.ip:
        // case R.id.netmask:
        // case R.id.gateway:
        // case R.id.dns:
        // showSystemSettings();
        // break;
        }

    }

    @Override
    public boolean onLongClick(View arg0) {

        showSystemSettings();

        return false;
    }

    // @Override
    // public void onFocusChange(View v, boolean arg1) {
    //
    // if (arg1) {
    //
    // switch (v.getId()) {
    //
    // case R.id.ip:
    // case R.id.netmask:
    // case R.id.gateway:
    // case R.id.dns:
    // showSystemSettings();
    // break;
    // }
    //
    // }
    //
    // }

    private void updateAddress() {

        Address address = new Address();

        DBHelper.query(DataBaseConfig.QueryTypes.UPDATE, UPDATE_ADDRESS, address, this, false, 0);

    }

    private void updateOtherIP() {

        OtherIP otherIP = new OtherIP();

        DBHelper.query(DataBaseConfig.QueryTypes.UPDATE, UPDATE_OTHER_IP, otherIP, this, false, 0);

    }

    private void showSystemSettings() {

        ActivityUtil.intentActivity(this, Settings.ACTION_WIFI_SETTINGS, null);
    }

    @Override
    public void doBeanMotification(int transactionId, int customType, List<?> result) {

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
