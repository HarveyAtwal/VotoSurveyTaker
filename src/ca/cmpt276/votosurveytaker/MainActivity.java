package ca.cmpt276.votosurveytaker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import ca.cmpt276.votosurveytaker.adapter.SettingDrawerAdapter;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.NotificationManager;
import ca.cmpt276.votosurveytaker.exception.GooglePlayServiceUnavailableException;
import ca.cmpt276.votosurveytaker.fragment.AboutFragment;
import ca.cmpt276.votosurveytaker.fragment.InvitationFragment;
import ca.cmpt276.votosurveytaker.fragment.MenuFragment;
import ca.cmpt276.votosurveytaker.fragment.SettingsFragment;
import ca.cmpt276.votosurveytaker.logic.AbstractItem;
import ca.cmpt276.votosurveytaker.logic.DrawerItem;
import ca.cmpt276.votosurveytaker.logic.DrawerXmlParser;
import ca.cmpt276.votosurveytaker.logic.HeaderItem;
import ca.cmpt276.votosurveytaker.logic.ListItem;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity to display all surveys.
 */
public class MainActivity extends ActionBarActivity implements
		OnItemClickListener {
	
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private List<AbstractItem> settingsList;
	protected ApplicationManager app;

	private CharSequence mDrawerTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		supportRequestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);

		initSettingsDrawer();
		app = (ApplicationManager) getApplication();

		if (savedInstanceState == null) {
			setProgressBarState(true);
			selectFragment(Globals.HOME);
		}

		initNotifications();
	}

	private void selectFragment(int position) {
		MenuFragment fragment = null;

		switch (position) {
		case Globals.HOME:
			fragment = new InvitationFragment();
			break;
		case Globals.GENERAL_SETTINGS:
			fragment = new SettingsFragment();
			break;
		case Globals.ABOUT:
			fragment = new AboutFragment();
			break;
		case Globals.LOG_OUT:
			logout();
			break;
		}
		if (fragment != null) {

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager
					.beginTransaction()
					.replace(R.id.content_frame, fragment, Globals.TAG_FRAGMENT)
					.commit();

			Bundle args = new Bundle();
			args.putInt(MenuFragment.ARG_MENU_NUMBER, position);
			fragment.setArguments(args);

			Resources res = getResources();
			String[] mMenuTitles = res.getStringArray(R.array.menu_titles);

			mDrawerTitle = mMenuTitles[position];
			getSupportActionBar().setTitle(mDrawerTitle);
			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	private void logout() {
		try{
			app.getNotificationManager().unregister();
		}catch (Exception e){
			Log.i("User", "First time user hasnt turned on notifications");
			e.printStackTrace();
		}
		Intent i = new Intent(this, RegistrationActivity.class);
		i.putExtra(Globals.DONT_REMEMBER, true);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	public void setProgressBarState(boolean state) {
		setSupportProgressBarIndeterminate(state);
		setSupportProgressBarVisibility(state);
	}

	private void initSettingsDrawer() {
		// want to activate the sliding drawer when settings button is clicked
		// so i need to get the sliding drawer and the drawer layout from the
		// parent activity
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// getting the setting options strings from strings array in xml
		fillSettingsList();
		// getting the sliding drawer
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// using the custom array adapter to populate the drawer with icons and
		// text
		mDrawerList.setAdapter(new SettingDrawerAdapter(this
				.getApplicationContext(), settingsList));
		mDrawerList.setOnItemClickListener(this);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Set ActionBar Title
		getSupportActionBar().setTitle(R.string.drawer_close);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(R.string.drawer_open);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void fillSettingsList() {
		DrawerXmlParser drawerParser = new DrawerXmlParser(this);
		try {
			List<DrawerItem> drawerItems = drawerParser
					.getDrawerItems(getResources().openRawResource(
							R.raw.drawer_list));
			settingsList = new ArrayList<AbstractItem>();
			for (int i = 0; i < drawerItems.size(); i++) {
				DrawerItem item = drawerItems.get(i);
				if (item.isHeader()) {
					settingsList.add(new HeaderItem(item.getTitle()));
				} else {
					settingsList.add(new ListItem(item.getTitle(), item
							.getImgResource()));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if (menu.findItem(R.id.action_refresh) != null) {
			menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refresh() {
		setProgressBarState(true);
		app.getSurveyManager().fetchAllInvitations();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		selectFragment(position);
	}

	@Override
	public void onBackPressed() {
		Log.i("MainActivity","Back button was pressed");
		final MenuFragment fragment = (MenuFragment) getSupportFragmentManager()
				.findFragmentByTag(Globals.TAG_FRAGMENT);
		if (fragment instanceof InvitationFragment) {
			super.onBackPressed();
			onTermination();
		} else {
			selectFragment(0);
		}
	}
	
	public void onTermination(){
		int pref = RegistrationActivity.getRememberMePref(this);
		boolean enabled = getNotificationPref();
		if (pref == 0 && enabled){
			Toast.makeText(this, getResources().getString(R.string.notification_message), 
					Toast.LENGTH_LONG).show();
		}else{
			
			if (enabled) {
				try{
					app.getNotificationManager().registerForNotifications();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		finish();
	}

	public void initNotifications() {
		boolean enabled = getNotificationPref();
		if (enabled) {
			if (!initNotificationManager()) {
				setNotificationPref(false);
			}
		}
	}

	public boolean initNotificationManager() {
		try {
			app.initNotificationManager(app.getRegistrationManager()
					.getApiKey(), this);
		} catch (GooglePlayServiceUnavailableException e) {
			NotificationManager.checkPlayServices(this);
			return false;
		}
		return true;
	}

	public boolean getNotificationPref() {
		SharedPreferences prefs = getSharedPreferences(Globals.PREF_SETTINGS,
				MODE_PRIVATE);
		return (boolean) prefs.getBoolean(Globals.NOTIFICATIONS, true);
	}

	public void setNotificationPref(boolean pref) {
		SharedPreferences settings = getSharedPreferences(
				Globals.PREF_SETTINGS, MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putBoolean(Globals.NOTIFICATIONS, pref);
		prefEditor.commit();
	}
}
