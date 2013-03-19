package com.example.smartterminal.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.smartterminal.R;
import com.example.smartterminal.main.bean.TabPage;
import com.example.smartterminal.main.widget.TabPageWithTabsActivity;
import com.example.smartterminal.test.activity.Activity1;
import com.example.smartterminal.test.activity.Activity2;
import com.example.smartterminal.test.activity.Activity3;

public class MainActivity extends TabPageWithTabsActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.st__main);

		addTabPage(new TabPage("a", new Intent(MainActivity.this,
				Activity1.class)));
		addTabPage(new TabPage("b", new Intent(MainActivity.this,
				Activity2.class)));
		addTabPage(new TabPage("c", new Intent(MainActivity.this,
				Activity3.class)));

		addTab((Button) findViewById(R.id.btn1));
		addTab((Button) findViewById(R.id.btn2));
		addTab((Button) findViewById(R.id.btn3));
		
		setTab(0);

	}

}
