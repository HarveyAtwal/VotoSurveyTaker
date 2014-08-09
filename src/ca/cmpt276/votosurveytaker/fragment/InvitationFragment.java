package ca.cmpt276.votosurveytaker.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Invitation;

import com.android.volley.VolleyError;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import ca.cmpt276.votosurveytaker.Globals;
import ca.cmpt276.votosurveytaker.MainActivity;
import ca.cmpt276.votosurveytaker.QuestionActivity;
import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.adapter.SurveyListViewAdapter;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.ErrorManager;

public class InvitationFragment extends MenuFragment implements OnRefreshListener, DataChangeListener{

	public static int NO_START_INVITATION = -1;
	
	private PullToRefreshLayout mPullToRefreshLayout;
	
	protected ApplicationManager app;

	private SurveyListViewAdapter invitationAdapter;
	private List<Invitation> invitationList;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setTitle();
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.fragment_menu_invitation_list, container, false);


		// Get the application instance
	    app = (ApplicationManager)getActivity().getApplication();
	    initializeInvitationManager();

		initPullDownToRefresh();
	    setupSurveyLister();
		return view;
	}

	private void initializeInvitationManager() {
	    app.initSurveyManager(app.getRegistrationManager().getApiKey());
	}
	
	private void initPullDownToRefresh() {
	    // Now find the PullToRefreshLayout to setup
	    mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
	    // Now setup the PullToRefreshLayout
	    ActionBarPullToRefresh.from(getActivity())
	            // Mark Children as pullable
                .theseChildrenArePullable(R.id.survey_list, android.R.id.empty)
	            // Set the OnRefreshListener
	            .listener(this)
	            // Finally commit the setup to our PullToRefreshLayout
	            .setup(mPullToRefreshLayout);
	}

	private void initSuveyList() {
		ListView surveyListView = (ListView) view.findViewById(R.id.survey_list);
		invitationList = new ArrayList<Invitation>();
		for (Invitation invitation : app.getSurveyManager().invitations()) {
			((MainActivity)getActivity()).setProgressBarState(false);
			invitationList.add(invitation);
		}
		invitationAdapter = new SurveyListViewAdapter(app, getActivity(), R.layout.invitation_list_item, invitationList);
		surveyListView.setAdapter(invitationAdapter);
		sortInvitations(invitationList);
	}

	private void sortInvitations(List<Invitation> list) {
	      final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      Collections.sort(list, new Comparator<Invitation>() {
	    	  public int compare(Invitation o1, Invitation o2) {
			      if (o1.getDateInvited() == null || o2.getDateInvited() == null)
			    	  return 0;
				  try {
					  int invitationOneAnsweredQuestions = o1.getNumberSurveyQuestionsAnswered();
					  int invitationTwoAnsweredQuestions = o2.getNumberSurveyQuestionsAnswered();
					  int invitationOneTotalQuestions = o1.getNumberSurveyQuestions();
					  int invitationTwoTotalQuestions = o2.getNumberSurveyQuestions();
					  
					  Boolean isOnePartial = invitationOneAnsweredQuestions > 0 && invitationOneAnsweredQuestions < invitationOneTotalQuestions;
					  Boolean isTwoPartial = invitationTwoAnsweredQuestions > 0 && invitationTwoAnsweredQuestions < invitationTwoTotalQuestions;
					  
					  Boolean isOneCompleted = invitationOneTotalQuestions == invitationOneAnsweredQuestions;
					  Boolean isTwoCompleted = invitationTwoTotalQuestions == invitationTwoAnsweredQuestions;
					  
					  int completeCompare = isOneCompleted.compareTo(isTwoCompleted);
					  int partialCompare = isOnePartial.compareTo(isTwoPartial);
					  
					  if(completeCompare != 0) {
						  return completeCompare;
					  } else if(partialCompare != 0) {
						  return partialCompare;
					  } else {
						  Date date1 = format.parse(o1.getDateInvited());
					      Date date2 = format.parse(o2.getDateInvited());
					      return date2.compareTo(date1);
					  }
				  } catch (ParseException e) {
					  e.printStackTrace();
				  }
				  return 0;
			  }
	      });
	}

	private void setupSurveyLister() {
		ListView surveyListView = (ListView) view.findViewById(R.id.survey_list);
		surveyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) { 
				launchInvitation(position);
			}
		});
	}

	public void launchInvitation(int position) {
		Invitation currentInvitationId = invitationList.get(position);
		
		int numOfQuestions = currentInvitationId.getNumberSurveyQuestions();
		int invitationId = currentInvitationId.getId();
		
		Intent intent = new Intent(getActivity(), QuestionActivity.class);
		intent.putExtra(Globals.NUM_OF_QUESTIONS, numOfQuestions);
		intent.putExtra(Globals.INVITATION_ID, invitationId);
		
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		resetStartInvitationId(view.getContext());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		app.getSurveyManager().removeListener(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		app.getSurveyManager().addListener(this);
	}

    @Override
    public void onRefreshStarted(View view) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
        		app.getSurveyManager().fetchAllInvitations();
				return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Notify PullToRefreshLayout that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();
            }
        }.execute();
    }
    
	@Override
	public void dataChanged() {
		initSuveyList();
		launchStartInvitation();
	}

	@Override
	public void networkError(VolleyError volleyError) {
		((MainActivity)getActivity()).setProgressBarState(false);
		initSuveyList();
		ErrorManager.displayErrorBox(getActivity(), volleyError);	
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.invitation_list, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	public static int getStartInvitationId(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Globals.PREF_SETTINGS,
				Context.MODE_PRIVATE);
		return (int) prefs.getInt(Globals.INVITATION_ID_TO_LAUNCH, NO_START_INVITATION);
	}

	public static void setStartInvitationId(Context context, int invitationId) {
		SharedPreferences settings = context.getSharedPreferences(
				Globals.PREF_SETTINGS, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(Globals.INVITATION_ID_TO_LAUNCH, invitationId);
		prefEditor.commit();
	}
	
	public static void resetStartInvitationId(Context context){
		setStartInvitationId(context, NO_START_INVITATION);
	}
	
	private void launchStartInvitation(){
		int invitationId = getStartInvitationId(view.getContext());
		if(invitationId != NO_START_INVITATION){
			int index = app.getSurveyManager().findInvitationIndexById(invitationList, invitationId);
			Log.i("InvitationFragment", "Invitation index: "+index);
			if(index != -1){
				launchInvitation(index);
			}
		}
	}
}
