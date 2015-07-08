package info.androidhive.actionbar.support;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import info.androidhive.CollectionDemoActivity;
import info.androidhive.actionbar.ConfigActivity;
import info.androidhive.actionbar.LocationFound;
import info.androidhive.actionbar.R;
import info.androidhive.actionbar.model.SpinnerNavItem;
import info.androidhive.info.actionbar.adapter.TitleNavigationAdapter;

public class MainActivity extends AppCompatActivity implements
        ActionBar.OnNavigationListener, ActionBar.TabListener {
    private static final String TAG = "MainActivity";
    // action bar
    private ActionBar mActionBar;
    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> mNavSpinner;
    // Navigation adapter
    private TitleNavigationAdapter mTitleNavigationAdapter;
    // Menu search
    private SearchView mSearchView;
    private SimpleCursorAdapter mCursorAdapter;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private ArrayAdapter<String> mArrayAdapter;
    // Refresh menu item
    private MenuItem mRefreshMenuItem;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time. Show action bar navigation mode with tab.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_main);

        mActionBar = getSupportActionBar();

        // Hide the action bar title
        mActionBar.setDisplayShowTitleEnabled(false);
        // Show Home Icon
        mActionBar.setDisplayShowHomeEnabled(true);
        // Changing the action bar icon
        mActionBar.setIcon(R.drawable.ico_actionbar);

        // Spinner Navigation / Tab Navigation
        setupListNavigation();
        setupTabNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String navigationMode = sharedPreferences.getString(getString(R.string.config_navigation_key), "0");
        setupNavigation(Integer.valueOf(navigationMode));
    }

    private void setupListNavigation() {
        // Spinner title navigation data
        mNavSpinner = new ArrayList<SpinnerNavItem>();
        mNavSpinner.add(new SpinnerNavItem("Local", R.drawable.ic_location));
        mNavSpinner.add(new SpinnerNavItem("My Places", R.drawable.ic_my_places));
        mNavSpinner.add(new SpinnerNavItem("Checkins", R.drawable.ic_checkin));
        mNavSpinner.add(new SpinnerNavItem("Latitude", R.drawable.ic_latitude));
        // title drop down adapter
        mTitleNavigationAdapter = new TitleNavigationAdapter(getApplicationContext(),
                mNavSpinner);
        // assigning the spinner navigation
        mActionBar.setListNavigationCallbacks(mTitleNavigationAdapter, this);
    }

    private void setupTabNavigation() {
//        mActionBar.addTab(mActionBar.newTab().setText("Tab 1").setTabListener(this));
//        mActionBar.addTab(mActionBar.newTab().setText("Tab 2").setTabListener(this));
//        mActionBar.addTab(mActionBar.newTab().setText("Tab 3").setTabListener(this));
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                mActionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            mActionBar.addTab(
                    mActionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private void setupNavigation(int mode) {
        switch (mode) {
            case ActionBar.NAVIGATION_MODE_STANDARD:
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                break;
            case ActionBar.NAVIGATION_MODE_LIST:
                // Enabling Spinner dropdown navigation
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                break;
            case ActionBar.NAVIGATION_MODE_TABS:
                // Enabling Tab navigation
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_support_main_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        setupSearchView(searchItem);
        return super.onCreateOptionsMenu(menu);
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    // http://nlopez.io/how-to-style-the-actionbar-searchview-programmatically/
    private void setupSearchView(MenuItem searchItem) {
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // 1. Config search view appearance
        mSearchView.setQueryHint(getString(R.string.search_hint));
        // 设置true后，右边会出现一个箭头按钮。如果用户没有输入，就不会触发提交（submit）事件
        mSearchView.setSubmitButtonEnabled(true);
        // Modify default style via java reflection, reference android/search_view.xml
        try {
            Field field = mSearchView.getClass().getDeclaredField("mSubmitButton");
            field.setAccessible(true);
            ImageView iv = (ImageView) field.get(mSearchView);
            iv.setImageDrawable(this.getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isAlwaysExpanded()) {
            // 设置该搜索框默认是否自动缩小为图标。
            mSearchView.setIconifiedByDefault(false);
        } else {
            // 初始是否已经是展开的状态
            // 写上此句后searchView初始展开的，也就是是可以点击输入的状态，
            // 如果不写，那么就需要点击下放大镜，才能展开出现输入框
            mSearchView.onActionViewExpanded();
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        // 2. Show application matching search text
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            // Associate searchable configuration with the SearchView
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
////            List<SearchableInfo> searchables = searchManager
////                    .getSearchablesInGlobalSearch();
////            for (SearchableInfo inf : searchables) {
////                if (inf.getSuggestAuthority() != null
////                        && inf.getSuggestAuthority().startsWith("applications")) {
////                    info = inf;
////                }
////            }
            mSearchView.setSearchableInfo(info);
        }
        // 3. Get AutoComplete text
        Cursor cursor = getTestCursor();
        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.mytextview, cursor, new String[] { "tb_name" },
                new int[] { R.id.textview });
        String[] item = new String[] {"test", "target", "total", "title", "turtle"};
        mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, item);
        mSearchView.setSuggestionsAdapter(mCursorAdapter);
        mSearchAutoComplete = (SearchView.SearchAutoComplete)
                mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setAdapter(mArrayAdapter);
        mSearchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchAutoComplete.setText(mArrayAdapter.getItem(position).toString());
            }
        });
        // 4. Get query text
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    // 添加suggestion需要的数据
    public Cursor getTestCursor() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                this.getFilesDir() + "/search_suggestion.db3", null);
        Cursor cursor = null;
        try {
            String insertSql = "insert into tb_test values (null,?,?)";
            db.execSQL(insertSql, new Object[] { "test", 1 });
            db.execSQL(insertSql, new Object[] { "target", 2 });
            db.execSQL(insertSql, new Object[] { "total", 3 });
            db.execSQL(insertSql, new Object[] { "title", 4 });
            db.execSQL(insertSql, new Object[] { "turtle", 5 });
            String querySql = "select * from tb_test";
            cursor = db.rawQuery(querySql, null);
        } catch (Exception e) {
            String sql = "create table tb_test (_id integer primary key autoincrement,tb_name varchar(20),tb_age integer)";
            db.execSQL(sql);
            String insertSql = "insert into tb_test values (null,?,?)";
            db.execSQL(insertSql, new Object[] { "test", 1 });
            db.execSQL(insertSql, new Object[] { "target", 2 });
            db.execSQL(insertSql, new Object[] { "total", 3 });
            db.execSQL(insertSql, new Object[] { "title", 4 });
            db.execSQL(insertSql, new Object[] { "turtle", 5 });
            String querySql = "select * from tb_test";
            cursor = db.rawQuery(querySql, null);
        }
        return cursor;
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            case R.id.action_location_found:
                // location found
                LocationFound();
                return true;
            case R.id.action_refresh:
                // refresh
                mRefreshMenuItem = item;
                // load the data from server
                new SyncData().execute();
                return true;
            case R.id.action_help:
                // help action
                return true;
            case R.id.action_check_updates:
                // check for updates action
                return true;
            case R.id.action_config:
                // config settings
                Intent intent = new Intent(this, ConfigActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launching new activity
     * */
    private void LocationFound() {
        Intent i = new Intent(MainActivity.this, LocationFound.class);
        startActivity(i);
    }

    /*
     * Actionbar navigation list item select listener
     * Implemented from ActionBar.OnNavigationListener
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // Action to be taken after selecting a spinner item
        Log.d(TAG, "onNavigationItemSelected: (itemPosition,itemId) = (" + itemPosition
                + "," + itemId + ")");
        // Change ViewPager's Fragment
        mViewPager.setCurrentItem(itemPosition);
        return false;
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a tab is selected.
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a previously selected tab is unselected.
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a previously selected tab is selected again.
    }

    /**
     * Async task to load the data from server
     * **/
    private class SyncData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // set the progress bar view
            mRefreshMenuItem.setActionView(R.layout.action_progressbar);
            mRefreshMenuItem.expandActionView();
        }

        @Override
        protected String doInBackground(String... params) {
            // not making real request in this demo
            // for now we use a timer to wait for sometime
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mRefreshMenuItem.collapseActionView();
            // remove the progress bar view
            mRefreshMenuItem.setActionView(null);
        }
    };

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new LaunchpadSectionFragment();

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
