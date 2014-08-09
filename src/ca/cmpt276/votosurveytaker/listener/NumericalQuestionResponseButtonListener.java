package ca.cmpt276.votosurveytaker.listener;

import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.QuestionResponse;
import org.votomobile.proxy.taker.Question.ResponseType;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.QuestionManager;
import ca.cmpt276.votosurveytaker.helper.Validate;
/**
 * 
 * Custom button listener for the numeric question "Submit" buttons
 *
 */
public class NumericalQuestionResponseButtonListener extends
		QuestionFragmentButtonListener {

	public NumericalQuestionResponseButtonListener(ApplicationManager app,
			Fragment fragment) {
		super(app, fragment);
	}

	@Override
	public void onClick(View v) {
		if(validated()) {
			EditText answerBox = (EditText) fragment.getView().findViewById(
					R.id.answer_textbox);
			String input = answerBox.getText().toString();
			long numberInput = Long.parseLong(input);
			QuestionManager manager = app.getQuestionManager();
			Question question = manager.getQuestions().get(
					manager.findIndexById(questionId));
	
			QuestionResponse existingResponse = question.getResponse();
	
			if (question.getResponseType() == ResponseType.NUMERIC) {
				if (existingResponse == null) {
					QuestionResponse response = new QuestionResponse();
					response.setNumericResponse((int) numberInput);
					app.getQuestionManager().commitAnswer(questionId, response);
	
					Button submitButton = (Button) v
							.findViewById(R.id.submit_question_button);
					submitButton.setText(v.getResources().getString(
							R.string.resubmit));
				} else {
	
					existingResponse.setNumericResponse((int) numberInput);
					app.getQuestionManager().commitAnswerChange(questionId);
				}
				app.getQuestionManager().moveToNextQuestion(fragment, questionId);
	
				displayShortToast(fragment, R.string.submitMessage);
			}
		}
	}

	private boolean validated() {
		EditText answerBox = (EditText) fragment.getView().findViewById(
				R.id.answer_textbox);
		String input = answerBox.getText().toString();
		// Get Phone Number
		
		if(!Validate.isInteger(input)) {
			String errorMessage = fragment.getActivity().getResources().getString(R.string.number_out_of_range);
			answerBox.setError(errorMessage);
			return false;
		}
		return true;
	}
}
