package com.dorm.smartterminal.global.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.global.util.RandomNumUtil;

/**
 * 数据库访问工具,提供了对DB4O数据库的访问接口
 * 
 * @author andy liu
 * 
 */
public class DBHelper {

    /**
     * queue for query task.
     */
    private static Queue<QueryTask> queryTaskQueue = new LinkedList<QueryTask>();

    /**
     * loop executer for task in queue
     */
    private static LoopExecuter loopExecuter = new LoopExecuter(queryTaskQueue);

    /**
     * application must start helper first.
     */
    public static void initDBhelper() {

        // start loop executer if not stated.
        loopExecuter.startExeceter();

        LogUtil.log("DBHelper", "init data helper success.");
    }

    /**
     * notify execute finish
     */
    public static void notifyQueryTaskExecuteFinish() {

        loopExecuter.nextExecute();
    }

    /**
     * API for data base access, which you can store, update, delete bean
     * object directly.
     * 
     * @param queryType
     *            Type of query, see {@linkplain com.dorm.smartterminal.global.db.config.DataBaseConfig.QueryTypes
     *            DataBaseConfig.QueryTypes}.
     * @param customType
     *            You can set a custom type, to indicate you logic query type.
     * @param beans
     *            {@linkplain com.dorm.smartterminal.global.db.bean.Bean Bean} object list, that you want to operate.
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
     *            default value in {@linkplain com.dorm.smartterminal.global.db.config.DataBaseConfig
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
    public static int query(int queryType, int customType, List<?> beans, Object caller, boolean isCascade,
            int activationDepth) {

        // generate transaction id
        int transactionId = RandomNumUtil.getRandomInteger();

        // do query
        queryTaskQueue.offer(new QueryTask(transactionId, queryType, customType, beans, caller, isCascade,
                activationDepth));

        loopExecuter.notifyEcecute();

        LogUtil.log("DBHelper", "query success.");

        // return id
        return transactionId;

    }

    /**
     * @see com.dorm.smartterminal.global.db.DBHelper#query(int, int, List, Object, boolean, int)
     */
    public static int query(int queryType, int customType, Object bean, Object caller, boolean isCascade,
            int activationDepth) {

        ArrayList<Object> beans = new ArrayList<Object>();
        beans.add(bean);

        return query(queryType, customType, beans, caller, isCascade, activationDepth);

    }

    /**
     * @see com.dorm.smartterminal.global.db.DBHelper#query(int, int, List, Object, boolean, int)
     */
    public static int query(int queryType, int customType, Class<?> classType, int[] idList, Object caller,
            boolean isCascade, int activationDepth) {

        ArrayList<Object> beans = new ArrayList<Object>();

        Field idField = null;

        Object bean = null;
        try {

            idField = classType.getField("id");

            for (int id : idList) {

                bean = classType.newInstance();
                idField.setInt(bean, id);
                beans.add(bean);
            }

        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return query(queryType, customType, beans, caller, isCascade, activationDepth);

    }
}
