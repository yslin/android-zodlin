package fr.xgouchet.tuto.switchpreferences;

import java.util.ArrayList;
import java.util.List;

import android.preference.PreferenceActivity;
import android.widget.ListAdapter;

public class MyPrefsActivity extends PreferenceActivity {

	private List<Header> mHeaders;

	@Override
	protected void onResume() {
		super.onResume();
		
		setTitle("Settings"); 
		
		if (getListAdapter() instanceof MyPrefsHeaderAdapter)
			((MyPrefsHeaderAdapter) getListAdapter()).resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (getListAdapter() instanceof MyPrefsHeaderAdapter)
			((MyPrefsHeaderAdapter) getListAdapter()).pause();
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		// Called when the settings screen is up for the first time
		// we load the headers from our xml description

		loadHeadersFromResource(R.xml.my_prefs_headers, target);

		mHeaders = target;
	}

	@Override
	public void setListAdapter(ListAdapter adapter) {
		int i, count;

		if (mHeaders == null) {
			mHeaders = new ArrayList<Header>();
			// When the saved state provides the list of headers,
			// onBuildHeaders is not called
			// so we build it from the adapter given, then use our own adapter

			count = adapter.getCount();
			for (i = 0; i < count; ++i)
				mHeaders.add((Header) adapter.getItem(i));
		}
		super.setListAdapter(new MyPrefsHeaderAdapter(this, mHeaders));
	}
}
