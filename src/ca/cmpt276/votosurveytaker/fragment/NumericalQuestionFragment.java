package ca.cmpt276.votosurveytaker.fragment;

import org.votomobile.proxy.taker.Question;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.listener.NumericalQuestionResponseButtonListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 
 * Displays numeric question fragments with appropriate format
 *
 */
public class NumericalQuestionFragment extends QuestionFragment {

	private ApplicationManager app = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_question_numerical,
				container, false);
		app = getApplicationManager();
		Question question = getQuestion(this);

		setupButtonListener(question, view);
		displayQuestion(question, view);

		return view;
	}

	protected void setupButtonListener(Question question, View view) {
		Button submitButton = (Button) view
				.findViewById(R.id.submit_question_button);
		NumericalQuestionResponseButtonListener listener = new NumericalQuestionResponseButtonListener(
				app, this);
		listener.bindButton(submitButton, question.getId());
	}

	protected void displayQuestion(Question question, View view) {
		TextView questionText = (TextView) view
				.findViewById(R.id.question_text_label);
		questionText.setText(question.getSMSPrompt());

		EditText answerBox = (EditText) view.findViewById(R.id.answer_textbox);

		if (question.getResponse() != null) {
			int answer = question.getResponse().getNumericResponse();
			Log.i("numeric answer", Integer.toString(answer));
			answerBox.setText(Integer.toString(answer));
			Button submitButton = (Button) view
					.findViewById(R.id.submit_question_button);
			submitButton.setText(view.getResources().getString(
					R.string.resubmit));
		}

	}
}
