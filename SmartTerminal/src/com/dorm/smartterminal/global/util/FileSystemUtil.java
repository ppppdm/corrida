/**
 * 
 */
package com.dorm.smartterminal.global.util;

import java.io.File;

/**
 * 
 * �����ļ����ļ��еĲ���
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
