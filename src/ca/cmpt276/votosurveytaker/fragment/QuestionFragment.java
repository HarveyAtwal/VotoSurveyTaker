package ca.cmpt276.votosurveytaker.fragment;

import org.votomobile.proxy.taker.Question;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.QuestionManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 
 * Question fragment class, inflates a layout with an error message if not overwritten
 *
 */
public class QuestionFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_question, container,
				false);

		return view;
	}

	protected ApplicationManager getApplicationManager(){
		return (ApplicationManager) getActivity().getApplication();
	}
	
	protected Question getQuestion(Fragment fragment){
		ApplicationManager app = getApplicationManager();
		
		QuestionManager manager = app.getQuestionManager();
		Question question = manager.getQuestions()
					.get(manager.findIndexById(fragment.getArguments().getInt(
							"question_id")));
		return question;
	}
	
	protected int getNumOfQuestions(Fragment fragment){
		return fragment.getArguments().getInt("number_questions");
	}
	
	
}
