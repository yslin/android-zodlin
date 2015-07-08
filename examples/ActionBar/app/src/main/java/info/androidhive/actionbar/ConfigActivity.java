package info.androidhive.actionbar;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;

/**
 */
public class ConfigActivity extends PreferenceActivity {
    private static final String TAG = "ConfigActivity";
    // 加入欄位變數宣告
    private SharedPreferences sharedPreferences;
    private Preference defaultColor;
    private String navigationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);
        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 建立SharedPreferences物件
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        navigationMode = sharedPreferences.getString(getString(R.string.config_navigation_key), "0");
        Log.d(TAG, "navigationMode: " + navigationMode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationMode = sharedPreferences.getString(getString(R.string.config_navigation_key), "0");
        Log.d(TAG, "navigationMode: " + navigationMode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button to action bar
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
