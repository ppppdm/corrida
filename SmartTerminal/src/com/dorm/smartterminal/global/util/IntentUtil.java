package com.dorm.smartterminal.global.util;

import android.content.Context;
import android.content.Intent;

/**
 * ����Acivity��ת��صĹ���
 * 
 * @author andy liu
 */
public class IntentUtil {

    /**
     * ��ת��������Acivity,��������ģʽ����AndroidManifest������
     * 
     * @param starter
     *            ��������������ģ�Activity��
     * @param action
     *            action��Ϣ
     * @param category
     *            category��Ϣ
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
     * ��ת��������Acivity,��������ģʽ����AndroidManifest������
     * 
     * @param starter
     *            ������
     * @param target
     *            Activity
     * @see [�ࡢ��#��������#��Ա]
     */
    public static void intentActivity(Context starter, Class<?> target) {

        if (null == starter || null == target)
            return;

        Intent intent = new Intent();
        intent.setClass(starter, target);
        starter.startActivity(intent);
    }
}
