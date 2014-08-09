package ca.cmpt276.votosurveytaker.fragment;

import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.QuestionResponse;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Displays multiple choice question fragments with appropriate format
 * 
 */
public class MCQuestionFragment extends QuestionFragment {

	private ApplicationManager app = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_question_mc, container,
				false);
		app = getApplicationManager();
		Question question = getQuestion(this);

		displayQuestion(question, view);
		populateChoices(question, view);
		setupResponses(this, question, view);
		return view;
	}

	protected void displayQuestion(Question question, View view) {
		TextView questionText = (TextView) view
				.findViewById(R.id.question_text_label);
		questionText.setText(question.getSMSPrompt());
	}

	private void populateChoices(Question question, View view) {
		RadioGroup radioGroup = (RadioGroup) view
				.findViewById(R.id.mc_choice_list);

		for (String choice : question.getOptions()) {
			RadioButton radioButton = new RadioButton(view.getContext());
			radioButton.setText(choice);
			radioButton.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
			radioButton.setPadding(0, 5, 0, 5);
			radioGroup.addView(radioButton);
		}
	}

	private void setupResponses(final Fragment fragment, final Question question, final View view) {

		final QuestionResponse questionResponse = question.getResponse();

		RadioGroup radioGroup = (RadioGroup) view
				.findViewById(R.id.mc_choice_list);

		if (questionResponse != null) {
			RadioButton radioButton = (RadioButton) radioGroup
					.getChildAt(questionResponse.getOptionChosenIndex());
			radioButton.setChecked(true);
		}

		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (questionResponse == null) {
							QuestionResponse response = new QuestionResponse();
							
							int position = getPosition(group);
							response.setOptionChosen(position);
							app.getQuestionManager().commitAnswer(
									question.getId(), response);
						} else {
							int position = getPosition(group);
							questionResponse.setOptionChosen(position);
							app.getQuestionManager().commitAnswerChange(
									question.getId());
						}
						app.getQuestionManager().moveToNextQuestion(fragment, question.getId());
						Toast.makeText(view.getContext(),
								R.string.submitMessage, Toast.LENGTH_LONG)
								.show();
					}
				});
	}
	
	private int getPosition(RadioGroup radioGroup){
		int checkedRadioId = -1;
		
		for(int i = 0; i < radioGroup.getChildCount(); i++){
			if(((RadioButton) radioGroup.getChildAt(i)).isChecked()){
				checkedRadioId = i;
				break;
			}
		}
		
		return checkedRadioId;
	}

}
