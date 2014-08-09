package ca.cmpt276.votosurveytaker.listener;

import ca.cmpt276.votosurveytaker.data.ApplicationManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
/**
 * 
 * Question fragment button listener, hold a reference to the button bound to it and the fragment the button belongs to
 *
 */
public class QuestionFragmentButtonListener implements OnClickListener{

	protected ApplicationManager app = null;
	protected Fragment fragment = null;
	protected int questionId;

	public QuestionFragmentButtonListener(ApplicationManager app, Fragment fragment) {
		this.app = app;
		this.fragment = fragment;
	}
	
	@Override
	public void onClick(View arg0) {

		
	}
	
	public void bindButton(Button button, int questionId) {
		button.setOnClickListener(this);
		this.questionId = questionId;
	}
	
	protected void displayShortToast(Fragment fragment, int stringID){
		Toast.makeText(
				fragment.getActivity(),
				fragment.getActivity().getResources()
						.getString(stringID),
				Toast.LENGTH_SHORT).show();
	}

}
