package com.dorm.smartterminal.global.db.bean;

/**
 * 项目对象统一父类
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
