package com.dorm.smartterminal.global.db;

import java.util.ArrayList;
import java.util.List;

import com.dorm.smartterminal.global.db.bean.Bean;
import com.dorm.smartterminal.global.util.RandomNumUtil;

/**
 * 数据库访问工具,提供了对DB4O数据库的访问接口
 * 
 * @author andy liu
 * 
 */
public class DBHelper {

    /**
     * API for data base access, which you can store, update, delete bean
     * object directly.
     * 
     * @param queryType
     *            Type of query, see
     *            {@linkplain com.dorm.smartterminal.global.db.config.DataBaseConfig.QueryTypes
     *            DataBaseConfig.QueryTypes}.
     * @param customType
     *            You can set a custom type, to indicate you logic query type.
     * @param beans
     *            {@linkplain com.dorm.smartterminal.global.db.bean.Bean Bean}
     *            object list, that you want to operate.
     * @param caller
     *            Caller of this function, which have to implements interface
     *            {@linkplain com.dorm.smartterminal.global.db.interfaces.DataBaseQueryInterface
     *            DataBaseQueryInterface}.
     * @param isCascade
     *            Indicate that whether this query is cascade to it's children
     *            objects.
     * @param activationDepth
     *            Indicate how many levels of depth to cascade.This only work
     *            when isCascade set "true".and then,must set a custom value, if
     *            custom value <= 0, will be set to
     *            default value in
     *            {@linkplain com.dorm.smartterminal.global.db.config.DataBaseConfig
     *            DataBaseConfig}, and there is a maximum value in the config
     *            too.
     *            Tip:Accurate value of activation depth, is good for program
     *            performance.
     *            But, bad value kill it!!! Be carefully !!!
     * @return Id of this transaction, for you to know, the response coming from
     *         which query.
     * 
     * @author andy liu
     */
    public static int query(int queryType, int customType, List<? extends Bean> beans, Object caller,
            boolean isCascade, int activationDepth) {

        // generate transaction id
        int transactionId = RandomNumUtil.getRandomInteger();

        // do query
        new QueryTask(transactionId, queryType, customType, beans, caller, isCascade, activationDepth).execute();

        // return id
        return transactionId;

    }

    public static int query(int queryType, int customType, Bean bean, Object caller, boolean isCascade,
            int activationDepth) {

        ArrayList<Bean> beans = new ArrayList<Bean>();
        beans.add(bean);

        return query(queryType, customType, beans, caller, isCascade, activationDepth);

    }

}
