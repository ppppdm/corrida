package com.example.smartterminal.main.widget;

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

import com.example.smartterminal.R;
import com.example.smartterminal.main.bean.TabPage;

/***
 * ViewPage效果的TabActivity
 * 
 * @author maifine
 */
public class TabPageActivity extends ActivityGroup {

	private final String LOGTAG = "TabPageActivity";
	private ViewPager _pager = null;
	private List<View> _listPages = null;
	private List<TabPage> _listIntents = null;
	private int currentTabIndex = 0;

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
		_pager = (ViewPager) findViewById(R.id.viewpager);
		_listPages = new ArrayList<View>();
		_listIntents = new ArrayList<TabPage>();
		MyPagerAdapter mpAdapter = new MyPagerAdapter(_listPages);
		_pager.setAdapter(mpAdapter);
		_pager.setOnPageChangeListener(new MyOnPageChangeListener());
		_pager.setCurrentItem(0);
	}

	/***
	 * 添加标签页
	 * 
	 * @param tabpage
	 */
	public void addTabPage(TabPage tabpage) {
		_listPages.add(getView(tabpage.getId(),
				tabpage.getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)));
		_listIntents.add(tabpage);
	}

	private View getView(String id, Intent intent) {
		return this.getLocalActivityManager().startActivity(id, intent)
				.getDecorView();
	}

	/***
	 * 当页面发生变化
	 * 
	 * @param index
	 */
	protected void onPageSelected(int index) {
		Log.d(LOGTAG, "onPageSelected:" + index);
		currentTabIndex = index;
		TabPage tabpage = _listIntents.get(index);
		getLocalActivityManager().startActivity(tabpage.getId(),
				tabpage.getIntent());
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
		_pager.setCurrentItem(index);
	}

	/***
	 * 获得当前的TabPage序号
	 * 
	 * @return
	 */
	public int getCurrentTabIndex() {
		return currentTabIndex;
	}

	/***
	 * 获得当前的TabPage
	 * 
	 * @return
	 */
	public TabPage getCurrentTabPage() {
		return _listIntents.get(currentTabIndex);
	}

	/**
	 * 
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int index) {
			TabPageActivity.this.onPageSelected(index);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			TabPageActivity.this.onPageScrolled(arg0, arg1, arg2);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			TabPageActivity.this.onPageScrollStateChanged(arg0);
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
