package com.dorm.smartterminal.settings.localsetting.bean;

import com.dorm.smartterminal.global.db.bean.Bean;

public class Address extends Bean {

    public String buildingPhase;
    public String area;
    public String buildingGroup;
    public String building;
    public String door;
    public String localDeviceId;

    public Address(int id) {
        super(id);
    }

}
