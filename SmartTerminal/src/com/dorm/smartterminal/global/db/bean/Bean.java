package com.dorm.smartterminal.global.db.bean;

/**
 * ��Ŀ����ͳһ����
 * 
 * @author andy liu
 * 
 */
public abstract class Bean {

    private int id;

    public Bean(int id) {

        this.id = id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
