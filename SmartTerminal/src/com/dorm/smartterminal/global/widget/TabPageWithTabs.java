package com.dorm.smartterminal.global.widget;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

/***
 * 带Tab按钮的ViewPage效果的TabActivity
 * 
 * @author andy liu
 * 
 */
public class TabPageWithTabs extends TabPage {

    // no style setted
    private int _DEFAULT_INTEGER_VALUE = -1;

    // buttons
    private ArrayList<View> _tabList = null;

    // on click listener
    private TabOnClickListener _tabOnClickListener = null;

    // record current tab
    private int _currentTabIndex = _DEFAULT_INTEGER_VALUE;

    /**
     * add tab to _tabList.
     * 
     * @param tab
     *            The view used to represent a tab, such as a button.
     */
    public void addTab(View tab) {

        // init onclick listener
        if (_tabOnClickListener == null)
            _tabOnClickListener = new TabOnClickListener();

        // init tab
        if (_tabList == null)
            _tabList = new ArrayList<View>();

        // set id for each tab
        tab.setId(_tabList.size());

        // set on click listener for tab
        tab.setOnClickListener(_tabOnClickListener);

        // add tab into list
        _tabList.add(tab);

    }

    /**
     * set tab sellected
     * 
     * @param id
     *            id of tab which you want to show.
     */
    public void setTab(int id) {

        // init current tab index
        if (_currentTabIndex == _DEFAULT_INTEGER_VALUE)
            _currentTabIndex = id;

        // cancel tab
        if (_currentTabIndex != id)
            _tabList.get(_currentTabIndex).setSelected(false);

        // show tab
        _tabList.get(id).setSelected(true);

        // record current tab index
        _currentTabIndex = id;

    }

    /***
     * after page changed
     * 
     * @param index
     *            id of the page showing after changed
     */
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

            if (id != _currentTabIndex) {

                // change tab page
                setPage(id);

                // change tab selllected
                setTab(id);

            }

        }

    }

}
