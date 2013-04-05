package com.dorm.smartterminal.global.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.widget.bean.Page;

/***
 * ViewPage效果的TabActivity
 * 
 * @author maifine
 */
public class TabPage extends ActivityGroup {

    private final String LOGTAG = "TabPageActivity";
    private ViewPager _pageContainer = null;
    private List<View> _listPages = null;
    private List<Page> _listIntents = null;
    private int _currentTabIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        InitViewPager();
    }

    private void InitViewPager() {
        _pageContainer = (ViewPager) findViewById(R.id.page_container);
        _listPages = new ArrayList<View>();
        _listIntents = new ArrayList<Page>();
        MyPagerAdapter mpAdapter = new MyPagerAdapter(_listPages);
        _pageContainer.setAdapter(mpAdapter);
        _pageContainer.setOnPageChangeListener(new MyOnPageChangeListener());
        _pageContainer.setCurrentItem(0);
    }

    /***
     * 添加标签页
     * 
     * @param tabpage
     */
    public void addPage(Page page) {
        _listPages.add(getView(page.getId(), page.getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)));
        _listIntents.add(page);
    }

    private View getView(String id, Intent intent) {
        return this.getLocalActivityManager().startActivity(id, intent).getDecorView();
    }

    /***
     * 当页面发生变化
     * 
     * @param index
     */
    protected void onPageSelected(int index) {
        Log.d(LOGTAG, "onPageSelected:" + index);
        _currentTabIndex = index;
        Page page = _listIntents.get(index);
        getLocalActivityManager().startActivity(page.getId(), page.getIntent());
    }

    protected void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    protected void onPageScrollStateChanged(int arg0) {

    }

    /***
     * 跳转页面
     * 
     * @param index
     */
    public void setPage(int index) {
        _pageContainer.setCurrentItem(index);
    }

    /***
     * 获得当前的TabPage序号
     * 
     * @return
     */
    public int getCurrentTabIndex() {
        return _currentTabIndex;
    }

    /***
     * 获得当前的TabPage
     * 
     * @return
     */
    public Page getCurrentPage() {
        return _listIntents.get(_currentTabIndex);
    }

    /**
     * 
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int index) {
            TabPage.this.onPageSelected(index);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            TabPage.this.onPageScrolled(arg0, arg1, arg2);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            TabPage.this.onPageScrollStateChanged(arg0);
        }
    }

    /**
     * 
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }
    }

}
