package com.dorm.smartterminal.global.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 包含Acivity跳转相关的工具
 * 
 * @author andy liu
 */
public class ActivityUtil {

    /**
     * 跳转到其他的Acivity,具体启动模式请在AndroidManifest中设置
     * 
     * @param starter
     *            发送请求的上下文（Activity）
     * @param action
     *            action信息
     * @param category
     *            category信息
     * 
     */
    public static void intentActivity(Context starter, String action, String category) {

        Intent intent = new Intent();

        if (null != action && action != "")
            intent.setAction(action);

        if (null != category && category != "")
            intent.addCategory(category);

        if (null != starter)
            starter.startActivity(intent);
    }

    /**
     * 跳转到其他的Acivity,具体启动模式请在AndroidManifest中设置
     * 
     * @param starter
     *            上下文
     * 
     * @param intent
     *            intent include target and other propertise.
     * 
     */
    public static void intentActivity(Context starter, Intent intent) {

        starter.startActivity(intent);
    }

    /**
     * 跳转到其他的Acivity,具体启动模式请在AndroidManifest中设置
     * 
     * @param starter
     *            上下文
     * @param target
     *            Activity
     */
    public static void intentActivity(Context starter, Class<?> target) {

        if (null == starter || null == target)
            return;

        Intent intent = new Intent();
        intent.setClass(starter, target);
        starter.startActivity(intent);
    }

    /**
     * 关闭Acivity
     * 
     * @param starter
     *            上下文
     * @param target
     *            Activity
     * @see [类、类#方法、类#成员]
     */
    public static void closeActivity(Activity target) {

        target.finish();
    }
}
