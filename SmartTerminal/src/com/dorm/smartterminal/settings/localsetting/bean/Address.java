package com.dorm.smartterminal.settings.localsetting.bean;

public class Address {

    public int id;

    public String buildingPhase;
    public String area;
    public String buildingGroup;
    public String building;
    public String door;
    public String localDeviceId;
    
    public Address(){
        
    }

    public Address(int id) {
        this.id = id;
    }

}
