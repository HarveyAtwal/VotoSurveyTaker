package ca.cmpt276.votosurveytaker.fragment;

import ca.cmpt276.votosurveytaker.R;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public abstract class MenuFragment extends Fragment {

    public static final String ARG_MENU_NUMBER = "menu_number";
    
    protected void setTitle() {
        int i = getArguments().getInt(ARG_MENU_NUMBER);
        String menuTitle = getResources().getStringArray(R.array.menu_titles)[i];
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(menuTitle);
    }
    
}
