package com.dorm.smartterminal.global.db.interfaces;

import java.util.List;

import com.dorm.smartterminal.global.db.bean.Bean;

/**
 * 项目对象统一父类
 * 
 * @author andy liu
 * 
 */
public interface DataBaseQueryInterface {

    public void doBeanMotification(int transactionId, int customType, List<? extends Bean> result);

    public void onDataBaseQueryFinish(int transactionId, int customType, boolean isSuccess, int errorCode,
            List<? extends Bean> result);

}
