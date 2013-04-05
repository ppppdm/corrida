package com.dorm.smartterminal.global.db.config;

import com.dorm.smartterminal.global.db.exception.BeanExistedException;

/**
 * 数据库访问模块，配置文件
 * 
 * @author andy liu
 * 
 */
public class DataBaseConfig {

    /**
     * Name of db4o data base file.
     * 
     * @author andy liu
     * 
     */
    public final static String DATA_BASE_FILE_NAME = "SmartTerminal.db4o";

    /**
     * 
     * Type of query.
     * 
     * @author andy liu
     * 
     */
    public final static class QueryTypes {

        /**
         * Insert bean object into database.
         */
        public final static int INSERT = 1;

        /**
         * Update bean objects into database.
         */
        public final static int UPDATE = 2;

        /**
         * Delete bean objects form database.
         */
        public final static int DELETE = 3;

        /**
         * Search bean objects from database.
         */
        public final static int SEARCH = 4;
    }

    /**
     * 
     * Error codes of query.
     * 
     * @author andy liu
     * 
     */
    public final static class ErrorCode {

        /**
         * Query executed success.
         */
        public final static int NO_ERROR = 1;

        /**
         * Database has bean closed unexpected.
         */
        public final static int DATA_BASE_CLOSED = 2;

        /**
         * Database id read only can not do query operation.
         */
        public final static int DATA_BASE_READ_ONLY = 3;

        /**
         * Database can not access.
         */
        public final static int DATA_BASE_IO_EXCEPION = 4;

        /**
         * Database file is locked.
         */
        public final static int DATA_BASE_FILE_LOCKED = 5;

        /**
         * Database file format is incompatible.
         */
        public final static int INCOMPATIBLE_FILE_FORMAT = 6;

        /**
         * Database format is old.
         */
        public final static int OLD_FORMAT_EXCEPTION = 7;

        /**
         * unknow exception.
         */
        public final static int UNKNOW_EXCEPTION = 8;

        /**
         * bean already existed in database.
         */
        public final static int BEAN_EXISTED_EXCEPTION = 9;

        /**
         * bean not existed in database.
         */
        public final static int BEAN_NOT_EXISTED_EXCEPTION = 10;

        /**
         * caller to call back is null.
         */
        public final static int CALLER_IS_NULL = 11;

    }

    /**
     * Default value of Activation depth. default is 1.
     */
    public final static int DEFAULT_ACTIVATOIN_DEPTH = 1;

    /**
     * Max value of Activation depth. default is 5.
     */
    public final static int MAX_ACTIVATOIN_DEPTH = 5;
}
