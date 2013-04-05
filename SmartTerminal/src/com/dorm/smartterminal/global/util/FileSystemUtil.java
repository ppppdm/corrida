/**
 * 
 */
package com.dorm.smartterminal.global.util;

import java.io.File;

/**
 * 
 * 关于文件与文件夹的操作
 * 
 * @author Andy Liu
 * 
 */
public class FileSystemUtil {

    public static void createFolder(String dir) {

        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

    }

}
