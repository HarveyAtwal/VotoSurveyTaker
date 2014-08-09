package ca.cmpt276.votosurveytaker.fragment;

import ca.cmpt276.votosurveytaker.R;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends MenuFragment {

	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setTitle();
		view = inflater.inflate(R.layout.fragment_menu_about, container, false);
		
		setVersion();
		return view;
	}

	private void setVersion() {
		TextView versionTextView = (TextView) view.findViewById(R.id.txtVersion);
		PackageManager packetManager = getActivity().getPackageManager();
		try {
			String version = packetManager.getPackageInfo(getActivity().getPackageName(), 0).versionName;
			String text = getActivity().getResources().getString(R.string.version, version);
			
			versionTextView.setText(text);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.main, menu);
	    super.onCreateOptionsMenu(menu,inflater);
	}
}
