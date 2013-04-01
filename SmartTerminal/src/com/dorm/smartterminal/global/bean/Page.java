package com.dorm.smartterminal.global.bean;

import android.content.Intent;

/***
 * TabPageActivityµÄ×ÓTabÒ³
 * 
 * @author andy liu
 */
public class Page {
    private String id;
    private Intent intent;

    public Page(String id, Intent intent) {
        this.id = id;
        this.intent = intent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
