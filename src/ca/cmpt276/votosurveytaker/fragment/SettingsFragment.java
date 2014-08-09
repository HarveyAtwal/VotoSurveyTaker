package ca.cmpt276.votosurveytaker.fragment;

import java.util.ArrayList;
import java.util.List;

import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Language;

import com.android.volley.VolleyError;

import ca.cmpt276.votosurveytaker.MainActivity;
import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.ErrorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class SettingsFragment extends MenuFragment implements DataChangeListener {

	private ApplicationManager app = null;
	private View view;
	private List<Language> languagesList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setTitle();
		view = inflater.inflate(R.layout.fragment_menu_settings, container,
				false);
		app = (ApplicationManager) getActivity().getApplication();

		initInterface(view);
		initLanguageManager(view);
		setupLanguageSpinnerListener();
		return view;
	}

	private void initInterface(View view) {
		initNotifications(view);
	}
	
	private void initLanguageManager(View view){
		app.initLanguageManager(app.getRegistrationManager().getApiKey(), view.getContext());
		app.getLanguageManager().fetchAllLanguages();
	}
	
	private void initLanguageSpinner(){
		Spinner languageSpinner = (Spinner)view.findViewById(R.id.languageSpinner);
		languagesList = new ArrayList<Language>();
		ArrayList<String> languageDisplay;
		for(Language language : app.getLanguageManager().languages()){
			languagesList.add(language);
		}
		Language prefLanguage = app.getLanguageManager().getPreferredLanguage();
		if(prefLanguage != null){
			languageDisplay = formatLanguageList(prefLanguage);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
					android.R.layout.simple_spinner_item,languageDisplay);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			languageSpinner.setAdapter(adapter);
		}
	}
	
	//preferred language will always be in front, efficiently tailored to 
	//how spinner operates
	private ArrayList<String> formatLanguageList(Language prefLanguage){
		ArrayList<String> languageDisplay = new ArrayList<String>();
		for(int i=0; i<languagesList.size(); i++){
			if(languagesList.get(i).getName().equals(prefLanguage.getName())){
				languagesList.remove(i);
				languagesList.add(0, prefLanguage);
			}
		}
		for(Language language : languagesList){
			languageDisplay.add(language.getName());
		}
		return languageDisplay;
	}
	
	
	private void setupLanguageSpinnerListener(){
		Spinner languageSpinner = (Spinner)view.findViewById(R.id.languageSpinner);
		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//dont operate on the first item since its the preferred language already
				if(position != 0){
					Language selectedLanguage = languagesList.get(position);
					Log.i("Get item", selectedLanguage.getName());
					app.getLanguageManager().setPreferredLanguage(selectedLanguage.getId());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
			
		});
	}
	


	private void initNotifications(final View view) {

		final MainActivity activity = (MainActivity) getActivity();
		if(activity == null){
			throw new RuntimeException("Unsupported activity type");
		}
		
		boolean enabled = activity.getNotificationPref();
		final CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.notificationsCheckBox);
		if (enabled) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}

		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (activity.initNotificationManager()) {
						activity.setNotificationPref(true);
						Log.i("GooglePlay", "initialized");
					} else {
						checkBox.setChecked(false);
						Log.i("GooglePlay", "unavailable");
					}
				} else {
					activity.setNotificationPref(false);
				}
			}
		});

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		app.getLanguageManager().removeListener(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		app.getLanguageManager().addListener(this);
	}

	@Override
	public void dataChanged() {
		initLanguageSpinner();
	}

	@Override
	public void networkError(VolleyError volleyError) {
		initLanguageSpinner();
		ErrorManager.displayErrorBox(getActivity(), volleyError);
	}
}
