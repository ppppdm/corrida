package com.dorm.smartterminal.global.db.interfaces;

import java.util.List;

/**
 * ��Ŀ����ͳһ����
 * 
 * @author andy liu
 * 
 */
public interface DataBaseQueryInterface {

    public void doBeanMotification(int transactionId, int customType, List<?> result);

    public void onDataBaseQueryFinish(int transactionId, int customType, boolean isSuccess, int errorCode,
            List<?> result);

}
