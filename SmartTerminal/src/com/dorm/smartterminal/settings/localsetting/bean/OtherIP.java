package com.dorm.smartterminal.settings.localsetting.bean;

import com.dorm.smartterminal.global.db.bean.Bean;

public class OtherIP extends Bean {

    public String outsideDoorDeviceIp;
    public String outsideBuildingDeviceIp;
    public String centerServerIp;

    public OtherIP(int id) {
        super(id);
    }

}
