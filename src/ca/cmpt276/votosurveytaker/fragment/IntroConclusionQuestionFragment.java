package ca.cmpt276.votosurveytaker.fragment;

import org.votomobile.proxy.taker.Question;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.listener.IntroConclusionProceedButtonListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * Displays introduction and conclusion fragments with appropriate format
 *
 */

public class IntroConclusionQuestionFragment extends QuestionFragment{

	private ApplicationManager app = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_question_intro_and_conclusion, container,
				false);
		app = getApplicationManager();
		Question question = getQuestion(this);
		setupButtonListener(question, view);
		displayQuestion(question, view);
		
		return view;
	}
	
	protected void setupButtonListener(Question question, View view){
		Button proceedButton = (Button)view.findViewById(R.id.proceed_button);
		IntroConclusionProceedButtonListener listener = new IntroConclusionProceedButtonListener(app, this);
		listener.bindButton(proceedButton, question.getId());
	}
	
	protected void displayQuestion(Question question, View view) {
		TextView questionText = (TextView) view
				.findViewById(R.id.content_label);
		questionText.setText(question.getSMSPrompt());
	}
}
