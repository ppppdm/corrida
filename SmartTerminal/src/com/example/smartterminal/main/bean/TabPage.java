package com.example.smartterminal.main.bean;

import android.content.Intent;

/***
 * TabPageActivityµÄ×ÓTabÒ³
 * @author maifine
 */
public class TabPage {
	private String id;
	private Intent intent;

	public TabPage(String id, Intent intent) {
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
