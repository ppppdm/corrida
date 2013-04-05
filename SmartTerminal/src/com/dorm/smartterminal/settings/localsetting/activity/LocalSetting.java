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

/***
 * local setting page
 * 
 * @author andy liu
 * 
 */
public class LocalSetting extends Activity implements OnClickListener, DataBaseQueryInterface {

    /*
     * log
     */
    private final static String TAG = "LocalSetting";

    /*
     * db
     */
    private final static int GET_ADDRESS = 1;
    private final static int UPDATE_ADDRESS = 2;

    /*
     * ui
     */
    private TextView buildingPhase = null;
    private TextView area = null;
    private TextView buildingGroup = null;
    private TextView building = null;
    private TextView door = null;
    private TextView localDeviceId = null;

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
        case R.id.save:
            updateAddress();
            break;
        }

    }

    private void getAddress() {

        Address address = new Address(1);

        DBHelper.query(DataBaseConfig.QueryTypes.SEARCH, GET_ADDRESS, address, this, false, 0);

    }

    private void updateAddress() {

        Address address = new Address(1);

        DBHelper.query(DataBaseConfig.QueryTypes.UPDATE, UPDATE_ADDRESS, address, this, false, 0);

    }

    @Override
    public void doBeanMotification(int transactionId, int customType, List<? extends Bean> result) {

        switch (customType) {
        case UPDATE_ADDRESS:
            Address bean = (Address) result.get(0);
            bean.buildingPhase = buildingPhase.getText().toString().trim();
            bean.area = area.getText().toString().trim();
            bean.buildingGroup = buildingGroup.getText().toString().trim();
            bean.building = building.getText().toString().trim();
            bean.door = door.getText().toString().trim();
            bean.localDeviceId = localDeviceId.getText().toString().trim();
            LogUtil.log(TAG, "motify address success");
            break;
        }

    }

    @Override
    public void onDataBaseQueryFinish(int transactionId, int customType, boolean isSuccess, int errorCode,
            List<? extends Bean> result) {

        switch (customType) {
        case GET_ADDRESS:
            showAddress(result);
            LogUtil.log(TAG, "init address success");
            break;
        case UPDATE_ADDRESS:
            showAddress(result);
            LogUtil.log(TAG, "update address success");
            break;
        }

    }
    
    /*
     * ui
     */

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
}
