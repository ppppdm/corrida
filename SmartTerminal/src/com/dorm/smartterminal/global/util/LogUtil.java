package com.dorm.smartterminal.global.util;

import java.util.Random;

import android.util.Log;

/**
 * 
 * Tools for show log.
 * 
 * @author andy liu
 * 
 */
public class LogUtil {

    /**
     * 
     * create log info and show
     * 
     * @return
     */
    public static void log(String tag, String info) {

        Log.i(tag, info);

    }
    
    public static void log(Object caller, String info) {

        Log.i(caller.getClass().getSimpleName(), info);

    }

}
