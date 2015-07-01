package com.example.zodlin.actionbar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ChildActivity extends Activity {
    private TextView mMainDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainDescription = (TextView) findViewById(R.id.main_description);
        mMainDescription.setText("Child Activity");
        // Set the Action Bar to use tabs for navigation
        ActionBar actionBar = getActionBar();
//        actionBar.hide();

        // Home is presented as either an activity icon or logo
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(android.R.drawable.btn_star_big_off);
//        actionBar.setTitle(R.string.app_name);

        // Navigating up with the app icon, child activity usually show this.
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from menu resource (res/menu/main)
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentChild = new Intent(this, ChildActivity.class);
        Intent intentAnother = new Intent(this, AnotherActivity.class);
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "android.R.id.home", Toast.LENGTH_SHORT).show();
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
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
}
