package ca.cmpt276.votosurveytaker.listener;

import android.support.v4.app.Fragment;
import android.view.View;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;
/**
 * 
 * Custom button listener for the introduction and conclusion "Proceed" buttons
 *
 */
public class IntroConclusionProceedButtonListener extends QuestionFragmentButtonListener{

	public IntroConclusionProceedButtonListener(ApplicationManager app,
			Fragment fragment) {
		super(app, fragment);
	}
	
	@Override
	public void onClick(View v) {
		app.getQuestionManager().moveToNextQuestion(fragment,
				questionId);
	}

}
