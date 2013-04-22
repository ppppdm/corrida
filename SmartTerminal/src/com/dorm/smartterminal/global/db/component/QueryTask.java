package com.dorm.smartterminal.global.db.component;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.ObjectClass;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;
import com.db4o.ext.IncompatibleFileFormatException;
import com.db4o.ext.OldFormatException;
import com.dorm.smartterminal.global.config.GlobalConfig;
import com.dorm.smartterminal.global.db.DBHelper;
import com.dorm.smartterminal.global.db.config.DataBaseConfig;
import com.dorm.smartterminal.global.db.exception.BeanExistedException;
import com.dorm.smartterminal.global.db.exception.BeanNotExistedException;
import com.dorm.smartterminal.global.db.exception.CallerIsNullException;
import com.dorm.smartterminal.global.db.interfaces.DataBaseQueryInterface;
import com.dorm.smartterminal.global.util.LogUtil;

/**
 * 实现请求任务
 * 
 * @author andy liu
 * 
 */
public class QueryTask extends AsyncTask {

    /*
     * logic
     */
    private int transactionId;
    private int queryType;
    private int customType;
    private List beans;
    private Object caller;
    private boolean isCascade;
    private int activationDepth;
    private boolean isSuccess;
    private int errorCode;

    /*
     * db 
     */
    private EmbeddedConfiguration config = null;
    private ObjectContainer db = null;
    private List result = null;

    /*
     * log
     */
    private String TAG = "QueryTask";

    public QueryTask(int transactionId, int queryType, int customType, List beans, Object caller, boolean isCascade,
            int activationDepth) {

        this.transactionId = transactionId;
        this.customType = customType;
        this.caller = caller;
        this.queryType = queryType;
        this.beans = beans;
        this.isCascade = isCascade;
        this.isSuccess = false;
        this.errorCode = DataBaseConfig.ErrorCode.NO_ERROR;

        if (activationDepth <= 0)
            this.activationDepth = DataBaseConfig.DEFAULT_ACTIVATOIN_DEPTH;
        else if (activationDepth > DataBaseConfig.MAX_ACTIVATOIN_DEPTH)
            this.activationDepth = DataBaseConfig.MAX_ACTIVATOIN_DEPTH;
        else
            this.activationDepth = activationDepth;

    }

    @Override
    protected Object doInBackground(Object... params) {

        try {

            // init config
            initConfig();

            // connet to db
            connect();

            // do request;
            query();

            // success
            isSuccess = true;

        }
        catch (DatabaseClosedException e) {
            errorCode = DataBaseConfig.ErrorCode.DATA_BASE_CLOSED;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (Db4oIOException e) {
            errorCode = DataBaseConfig.ErrorCode.DATA_BASE_IO_EXCEPION;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (DatabaseFileLockedException e) {
            errorCode = DataBaseConfig.ErrorCode.DATA_BASE_FILE_LOCKED;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (IncompatibleFileFormatException e) {
            errorCode = DataBaseConfig.ErrorCode.INCOMPATIBLE_FILE_FORMAT;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (OldFormatException e) {
            errorCode = DataBaseConfig.ErrorCode.OLD_FORMAT_EXCEPTION;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (DatabaseReadOnlyException e) {
            errorCode = DataBaseConfig.ErrorCode.DATA_BASE_READ_ONLY;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (BeanExistedException e) {
            errorCode = DataBaseConfig.ErrorCode.BEAN_EXISTED_EXCEPTION;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (BeanNotExistedException e) {
            errorCode = DataBaseConfig.ErrorCode.BEAN_NOT_EXISTED_EXCEPTION;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (CallerIsNullException e) {
            errorCode = DataBaseConfig.ErrorCode.CALLER_IS_NULL;
            isSuccess = false;
            e.printStackTrace();
        }
        catch (Exception e) {
            errorCode = DataBaseConfig.ErrorCode.UNKNOW_EXCEPTION;
            isSuccess = false;
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Object result) {

        try {

            // do logic
            doLogic();

        }
        catch (Db4oIOException e) {
            errorCode = DataBaseConfig.ErrorCode.DATA_BASE_IO_EXCEPION;
            e.printStackTrace();
        }
        catch (CallerIsNullException e) {
            errorCode = DataBaseConfig.ErrorCode.CALLER_IS_NULL;
            e.printStackTrace();
        }
        finally {

            // close db
            close();

            // notify loop executer this task finish.
            notifyExecuteFinish();
        }
    }

    /*
     * logic
     */

    private void doLogic() throws CallerIsNullException {

        // call back
        if (null != caller) {
            ((DataBaseQueryInterface) caller).onDataBaseQueryFinish(transactionId, customType, isSuccess, errorCode,
                    result);
        }
        else {

            throw new CallerIsNullException("Can not do call back of caller to do logic, caller is null.");

        }
    }

    private void query() throws CallerIsNullException, BeanExistedException, BeanNotExistedException {

        switch (queryType) {
        case DataBaseConfig.QueryTypes.INSERT:
            insert();
            break;
        case DataBaseConfig.QueryTypes.SEARCH:
            search();
            break;
        case DataBaseConfig.QueryTypes.UPDATE:
            update();
            break;
        case DataBaseConfig.QueryTypes.DELETE:
            delete();
            break;
        }
    }

    private void search() {

        // add into database
        result = getBeans(beans);

        LogUtil.log(TAG, "search '" + beans.get(0).getClass().getName() + "' success." + result.size());

    }

    private void insert() throws BeanExistedException {

        // get bean
        result = getBeans(beans);
        boolean test = result.isEmpty();
        int t = result.size();

        // if this bean is not existed
        if (null == result || result.isEmpty()) {

            // for each bean
            for (int i = 0; i < beans.size(); i++) {

                // add into database
                db.store(beans.get(i));

                LogUtil.log(TAG, "insert '" + beans.get(i).getClass().getName() + "' success.");

            }

        }
        else {

            throw new BeanExistedException("bean " + beans.get(0).getClass().getName()
                    + "already existed. insert failure");

        }
    }

    private void update() throws BeanNotExistedException, CallerIsNullException {

        // get bean
        result = getBeans(beans);

        if (null != result && !result.isEmpty() && result.size() >= beans.size()) {

            // let caller motify data
            motifyData();

            // update
            for (int i = 0; i < result.size(); i++) {

                setCascadeDepth4Bean(result.get(i));

                db.store(result.get(i));

            }
        }
        else {

            throw new BeanNotExistedException("beans not all existed. update failure");

        }
    }

    private void motifyData() throws CallerIsNullException {

        if (null != caller) {

            // call back
            ((DataBaseQueryInterface) caller).doBeanMotification(transactionId, customType, result);

        }
        else {

            throw new CallerIsNullException("Can not do call back of caller to motify data, caller is null.");

        }
    }

    private void delete() throws BeanNotExistedException {

        // get bean
        result = getBeans(beans);

        if (null != result && !result.isEmpty() && result.size() >= beans.size()) {

            // delete
            for (int i = 0; i < result.size(); i++) {

                setCascadeDepth4Bean(result.get(i));

                db.delete(result.get(i));

            }
        }
        else {

            throw new BeanNotExistedException("beans not all existed. delete failure");

        }

    }

    /*
     * util
     */

    private <T> List<T> getBeans(List<T> targets) {

        List<T> existedBeans = new ArrayList<T>();

        for (int i = 0; i < targets.size(); i++) {

            existedBeans.addAll((List<T>) (db.queryByExample((T) (targets.get(i)))));

        }

        return existedBeans;

    }

    private <T> void setCascadeDepth4Bean(T bean) {

        if (isCascade && null != bean) {

            db.activate(bean, activationDepth);

        }

    }

    /*
     * db
     */

    private void initConfig() {

        // create config
        config = Db4oEmbedded.newConfiguration();

        // if query is cascade
        if (isCascade) {

            // get bean class
            ObjectClass objectClass = config.common().objectClass(beans.get(0).getClass());

            // set cascade
            switch (queryType) {
            case DataBaseConfig.QueryTypes.INSERT:
                objectClass.cascadeOnUpdate(true);
                break;
            case DataBaseConfig.QueryTypes.DELETE:
                objectClass.cascadeOnDelete(true);
                break;
            }

            // set activation depth in static way
            // objectClass.cascadeOnActivate(true);
            // objectClass.maximumActivationDepth(activationDepth);

        }
    }

    private void connect() {

        // open new db
        db = Db4oEmbedded.openFile(config, GlobalConfig.LOCAL_FILE_DIR_ROOT + DataBaseConfig.DATA_BASE_FILE_NAME);

    }

    private void close() {

        closeDB();
    }

    private void closeDB() {

        if (db != null) {

            db.close();
            db = null;
        }
    }

    private void notifyExecuteFinish() {

        LogUtil.log(this, "notify loop executer this task executed finish.");

        DBHelper.notifyQueryTaskExecuteFinish();
    }

}
