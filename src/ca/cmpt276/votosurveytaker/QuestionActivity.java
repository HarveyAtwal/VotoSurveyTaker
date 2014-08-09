package ca.cmpt276.votosurveytaker;

import java.util.ArrayList;
import org.votomobile.datamodel.taker.DataChangeListener;
import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.Question.ResponseType;

import com.android.volley.VolleyError;

import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.ErrorManager;
import ca.cmpt276.votosurveytaker.data.MediaManager;
import ca.cmpt276.votosurveytaker.fragment.IntroConclusionQuestionFragment;
import ca.cmpt276.votosurveytaker.fragment.MCQuestionFragment;
import ca.cmpt276.votosurveytaker.fragment.NumericalQuestionFragment;
import ca.cmpt276.votosurveytaker.fragment.OpenQuestionFragment;
import ca.cmpt276.votosurveytaker.transformer.ZoomOutPageTransformer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 
 * Main question activity that inflates a variety of question fragments
 *
 */

public class QuestionActivity extends ActionBarActivity implements
		DataChangeListener, OnPageChangeListener {

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private boolean initialized = false;
	private int totalNumQuestions;
	private int invitationId;
	
	private int currentPage = 0;

	private ArrayList<Fragment> fragments;

	protected ApplicationManager app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fragments = new ArrayList<Fragment>();
		// Get the application instance
		app = (ApplicationManager) getApplication();

		supportRequestWindowFeature(Window.FEATURE_PROGRESS);
		
		setContentView(R.layout.activity_question);
		totalNumQuestions = getIntent().getIntExtra(
				Globals.NUM_OF_QUESTIONS, 0);
		invitationId = getIntent().getIntExtra(
				Globals.INVITATION_ID, 0);

		app.initMediaManager(this);
		app.initQuestionManager(app.getRegistrationManager().getApiKey(), this);
		app.getQuestionManager().setCurrentSurveyId(invitationId);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);

		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mPager.setOnPageChangeListener(this);

		SeekBar seekBar = (SeekBar) findViewById(R.id.songProgressBar);
		ImageButton mediaButton = (ImageButton) findViewById(R.id.imgButtonMedia);
		seekBar.setEnabled(false);
		mediaButton.setEnabled(false);
		
		downloadQuestions();
		
	}
	
	private void setProgressBar() {
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		int questionsAnswered = 0;
		for (Question question : app.getQuestionManager().getQuestions()) {
			if(question.getResponse() != null) {
				questionsAnswered ++;
			}
		}
		int progress = (questionsAnswered* 100) / getNumOfQuestions();
		progressBar.setProgress(progress);
	}

	@Override
	public void onPause() {
		super.onPause();
		app.getQuestionManager().removeListener(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		app.getQuestionManager().addListener(this);
	}

	public void downloadQuestions() {
		setProgressBarState(true);
		app.getQuestionManager().fetchQuestions();
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	public void setProgressBarState(boolean state) {
		setSupportProgressBarIndeterminate(state);
		setSupportProgressBarVisibility(state);
	}

	@Override
	public void dataChanged() {
		setProgressBarState(false);
		Log.i("Data change", "Data is being changed");
		setProgressBar();
		if (!initialized) {
			fragments.clear();
			for (Question question : app.getQuestionManager().getQuestions()) {
				createQuestionFragment(question);
			}
			currentPage = app.getQuestionManager()
					.findFirstUnanswered();

			mPager.setCurrentItem(currentPage);//automation is being used
			mPagerAdapter.notifyDataSetChanged();
			initialized = true;

			// Set Actionbar Title
			Question firstQuestion = app.getQuestionManager().getQuestions().get(0);
			ResponseType responseType = firstQuestion.getResponseType();
			if(responseType == ResponseType.INTRODUCTION_OR_CONCLUSION) {
				getSupportActionBar().setTitle(R.string.introduction);
				MediaManager media = app.getMediaManager();
				media.stopMusic();
				String songUrl = firstQuestion.getAudioPromptUrl();
				if(songUrl != "false") {
					setMediaVisibility(View.VISIBLE);
					media.setSongURL(songUrl);
					media.startMusic();
				}
			} else {
				String questionStr = getResources().getString(R.string.question);
				getSupportActionBar().setTitle(questionStr + " " + (currentPage + 1));
			}
			setPageNum(1);
		}
	}

	private void createQuestionFragment(Question question) {
		ResponseType responseType = question.getResponseType();
		Fragment fragment = null;
		switch (responseType) {
		case OPEN:
			fragment = new OpenQuestionFragment();
			break;
		case MULTIPLE_CHOICE:
			fragment = new MCQuestionFragment();
			break;
		case NUMERIC:
			fragment = new NumericalQuestionFragment();
			break;
		case INTRODUCTION_OR_CONCLUSION:
			fragment = new IntroConclusionQuestionFragment();
			break;
		case NONE:
			throw new RuntimeException("Unsupported question response type");
		default:
			throw new RuntimeException("Unsupported question response type");
		}
		Bundle bundle = new Bundle();
		bundle.putInt("question_id", question.getId());
		bundle.putInt("number_questions", totalNumQuestions);
		fragment.setArguments(bundle);

		fragments.add(fragment);
		mPagerAdapter.notifyDataSetChanged();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu
	    getMenuInflater().inflate(R.menu.question, menu);
	    return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle your other action bar items...
        switch (item.getItemId()) {
        	case android.R.id.home:
        	      Intent homeIntent = new Intent(this, MainActivity.class);
        	      homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	      startActivity(homeIntent);
        	      finish();
        	return true;
        	case R.id.action_prev_item:
        		prev();
            	return true;
            case R.id.action_next_item:
                next();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private int getItem(int pos) {
    	return mPager.getCurrentItem() + pos;
    }
    
	public void next() {
		mPager.setCurrentItem(getItem(+1));
	}

	private void prev() {
		mPager.setCurrentItem(getItem(-1));
	}

	public void onDestroy() {
	    super.onDestroy();
		MediaManager media = app.getMediaManager();
		media.stopMusic();
	}
	@Override
	public void networkError(VolleyError volleyError) {
		setProgressBarState(false);
		ErrorManager.displayErrorBox(this, volleyError);
	}

	public ViewPager getPager() {
		return mPager;
	}

	public PagerAdapter getPagerAdapter() {
		return mPagerAdapter;
	}

	public int getNumOfQuestions() {
		return totalNumQuestions;
	}
	
	public int getNumFragments(){
		return fragments.size();
	}

	public ArrayList<Fragment> getFragments() {
		return fragments;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	private void setMediaVisibility(int visible) {
		RelativeLayout mediaLayout = (RelativeLayout) findViewById(R.id.mediaLayout);
		mediaLayout.setVisibility(visible);
	}
	
	private void setPageNum(int i) {
		TextView txtPageNum = (TextView) findViewById(R.id.txtPageNumber);
		TextView txtPageSize = (TextView) findViewById(R.id.txtPageSize);
		
		txtPageNum.setText("" + i);
		txtPageSize.setText("" + getNumFragments());
	}
	
	//Checking if user is selecting the page with sliding
	//so that we can update the actionbar's tracking
	@Override
	public void onPageSelected(int pos) {
		MediaManager media = app.getMediaManager();
		media.stopMusic();
		setMediaVisibility(View.GONE);
		Question firstQuestion = app.getQuestionManager().getQuestions().get(0);
		Question question = app.getQuestionManager().getQuestions().get(pos);
		ResponseType responseType = question.getResponseType();
		ResponseType checkIntrOorConclusion = responseType.INTRODUCTION_OR_CONCLUSION;
		
		String songUrl = question.getAudioPromptUrl();
		SeekBar seekBar = (SeekBar) findViewById(R.id.songProgressBar);
		ImageButton mediaButton = (ImageButton) findViewById(R.id.imgButtonMedia);
		seekBar.setEnabled(false);
		mediaButton.setEnabled(false);
		if(songUrl != "false") {
			setMediaVisibility(View.VISIBLE);
			media.setSongURL(songUrl);
			media.startMusic();
		}
		
		if(responseType == checkIntrOorConclusion && pos == 0) {
			getSupportActionBar().setTitle(R.string.introduction);
			
		} else if((responseType == checkIntrOorConclusion && pos == (getNumFragments() - 1))) {
			getSupportActionBar().setTitle(R.string.conclusion);
			
		} else if(firstQuestion.getResponseType() != checkIntrOorConclusion) {
			String questionStr = getResources().getString(R.string.question);
			getSupportActionBar().setTitle(questionStr + " " + (pos + 1));
			
		} else {
			String questionStr = getResources().getString(R.string.question);
			getSupportActionBar().setTitle(questionStr + " " + pos);
		}
		setPageNum(pos + 1);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent homeIntent = new Intent(this, MainActivity.class);
	    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(homeIntent);
	    finish();
	}
	
}
