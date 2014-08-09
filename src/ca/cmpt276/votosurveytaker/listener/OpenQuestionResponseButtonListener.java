package ca.cmpt276.votosurveytaker.listener;

import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.Question.ResponseType;
import org.votomobile.proxy.taker.QuestionResponse;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import ca.cmpt276.votosurveytaker.data.QuestionManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
/**
 * 
 * Custom button listener for the open question "Submit" buttons
 *
 */
public class OpenQuestionResponseButtonListener extends
		QuestionFragmentButtonListener {

	public OpenQuestionResponseButtonListener(ApplicationManager app,
			Fragment fragment) {
		super(app, fragment);
	}

	@Override
	public void onClick(View v) {
		EditText answerBox = (EditText) fragment.getView().findViewById(
				R.id.answer_textbox);
		String input = answerBox.getText().toString();

		if (input.trim().equals("")) {
			return;
		}
		QuestionManager manager = app.getQuestionManager();
		Question question = manager.getQuestions().get(
				manager.findIndexById(questionId));

		QuestionResponse existingResponse = question.getResponse();

		String answer = answerBox.getText().toString();

		if (question.getResponseType() == ResponseType.OPEN) {
			if (existingResponse == null) {
				QuestionResponse response = new QuestionResponse();
				response.setOpenText(answer);
				app.getQuestionManager().commitAnswer(questionId, response);

				Button submitButton = (Button) v
						.findViewById(R.id.submit_question_button);
				submitButton.setText(v.getResources().getString(
						R.string.resubmit));
			} else {

				existingResponse.setOpenText(answer);
				app.getQuestionManager().commitAnswerChange(questionId);
			}
			app.getQuestionManager().moveToNextQuestion(fragment, questionId);
		}
		displayShortToast(fragment, R.string.submitMessage);

	}
}
