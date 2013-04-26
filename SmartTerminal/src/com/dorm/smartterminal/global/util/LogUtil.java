package com.dorm.smartterminal.global.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import android.os.Environment;
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

    static OutputStreamWriter writer;

    static String LOG_SERVICE_LOG_PATH;
    
    static {
        LOG_SERVICE_LOG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "log.txt";

        try {
            writer = new OutputStreamWriter(new FileOutputStream(LOG_SERVICE_LOG_PATH, true));
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public LogUtil() {

    }

    public static void log(String tag, String info) {

        Log.i(tag, info);
        
        try {
            writer.write(tag+" "+ info);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void log(Object caller, String info) {

        Log.i(caller.getClass().getSimpleName(), info);
        
        try {
            writer.write(caller.getClass().getSimpleName()+" "+ info);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
