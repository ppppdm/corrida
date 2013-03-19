package com.example.smartterminal.main.widget;

import java.util.ArrayList;

import com.example.smartterminal.main.bean.TabPage;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class TabPageWithTabsActivity extends TabPageActivity {

	// no style setted
	private int _DEFAULT_INTEGER_VALUE = -1;

	// buttons
	private ArrayList<View> _tab_list = null;

	// styles
	private int _button_unclick_style = _DEFAULT_INTEGER_VALUE;
	private int _button_clicked_style = _DEFAULT_INTEGER_VALUE;

	// on click listener
	private TabOnClickListener _tab_on_click_listener = null;

	// record current tab
	private int current_tab_index = _DEFAULT_INTEGER_VALUE;

	/**
	 * add tab to _tab_list.
	 * 
	 * @param tab
	 *            The view used to represent a tab, such as a button.
	 */
	public void addTab(View tab) {

		// init onclick listener
		if (_tab_on_click_listener == null)
			_tab_on_click_listener = new TabOnClickListener();

		// init tab
		if (_tab_list == null)
			_tab_list = new ArrayList<View>();

		// set id for each tab
		tab.setId(_tab_list.size());

		// set on click listener for tab
		tab.setOnClickListener(_tab_on_click_listener);

		// add tab into list
		_tab_list.add(tab);

	}

	/**
	 * set tab sellected
	 * 
	 * @param id
	 */
	public void setTab(int id) {

		// init current tab index
		if (current_tab_index == _DEFAULT_INTEGER_VALUE)
			current_tab_index = id;

		// cancel tab
		if (current_tab_index != id)
			_tab_list.get(current_tab_index).setSelected(false);

		// show tab
		_tab_list.get(id).setSelected(true);

		// record current tab index
		current_tab_index = id;

	}

	protected void onPageSelected(int index) {
		super.onPageSelected(index);

		setTab(index);
	}

	/**
	 * onclick listener for tabs
	 * 
	 */
	public class TabOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// get tab id
			int id = v.getId();

			if (id != current_tab_index) {

				// change tab page
				setPage(id);

				// change tab selllected
				setTab(id);

			}

		}
	}

}
