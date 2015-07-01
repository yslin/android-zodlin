package com.example.zodlin.actionbar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * This sample shows you how to use ActionBarCompat with a customized theme. It utilizes a split
 * action bar when running on a device with a narrow display, and show three tabs.
 *
 * This Activity extends from ActionBarActivity, which provides all of the function
 * necessary to display a compatible Action Bar on devices running Android v2.1+.
 *
 * The interesting bits of this sample start in the theme files
 * ('res/values/styles.xml' and 'res/values-v14</styles.xml').
 *
 * Many of the drawables used in this sample were generated with the
 * 'Android Action Bar Style Generator': http://jgilfelt.github.io/android-actionbarstylegenerator
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private TextView mMainDescription;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainDescription = (TextView) findViewById(R.id.main_description);

        // Set the Action Bar to use tabs for navigation
        ActionBar actionBar = getActionBar();
//        actionBar.hide();

        // Home is presented as either an activity icon or logo
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setIcon(android.R.drawable.btn_star_big_on);
//        actionBar.setTitle(R.string.app_name);

        // Navigating up with the app icon, main activity usually won't show this.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Add three tabs to the Action Bar for display
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.addTab(actionBar.newTab().setText("Tab 1").setTabListener(this));
//        actionBar.addTab(actionBar.newTab().setText("Tab 2").setTabListener(this));
//        actionBar.addTab(actionBar.newTab().setText("Tab 3").setTabListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from menu resource (res/menu/main)
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchItem.getActionView();
        Log.d(TAG, "mSearchView:" + mSearchView);
//        MenuItemCompat.setActionView(searchItem, mSearchView);
//        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                Log.d(TAG, "on expand: " + mSearchView.getQuery());
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                Log.d(TAG, "on collapse: " + mSearchView.getQuery());
//                return true;
//            }
//        });
        // 配置SearchView的属性
//        setupSearchView(searchItem);

        return super.onCreateOptionsMenu(menu);
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    private void setupSearchView(MenuItem searchItem) {
        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        // Show application matching search text
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager
                    .getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager
                    .getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentChild = new Intent(this, ChildActivity.class);
        Intent intentAnother = new Intent(this, AnotherActivity.class);
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mMainDescription.setText("menu_location");
                startActivity(intentChild);
                Toast.makeText(this, "menu_refresh", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_location:
                mMainDescription.setText("menu_location");
                startActivity(intentAnother);
                Toast.makeText(this, "menu_location", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_settings:
                Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//
//    // Implemented from ActionBar.TabListener
//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // This is called when a tab is selected.
//    }
//
//    // Implemented from ActionBar.TabListener
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // This is called when a previously selected tab is unselected.
//    }
//
//    // Implemented from ActionBar.TabListener
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // This is called when a previously selected tab is selected again.
//    }
}