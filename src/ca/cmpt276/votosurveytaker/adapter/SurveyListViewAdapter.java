package ca.cmpt276.votosurveytaker.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.votomobile.proxy.taker.Invitation;

import ca.cmpt276.votosurveytaker.R;
import ca.cmpt276.votosurveytaker.data.ApplicationManager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Displays each invitation list item with correct format
 *
 */

public class SurveyListViewAdapter extends ArrayAdapter{
	
	private Context context;
	private List<Invitation> menuItems;
	
	@SuppressWarnings("unchecked")
	public SurveyListViewAdapter(ApplicationManager app, Context context, int resource, List<Invitation> list) {
		super(context, resource, list);
		this.context = context;
		this.menuItems = list;
		
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Inflate the layout, sliding drawer layout.xml, in each row.
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.invitation_list_item, parent, false);

        // Declare and define the TextView, "item." This is where
        // the name of each item will appear.
        TextView title = (TextView)row.findViewById(R.id.newSurveyTextView);
        TextView date = (TextView)row.findViewById(R.id.txtDate);
		ImageView surveyIcon = (ImageView) row.findViewById(R.id.imgSurveyIcon);
        
		try {
	        Invitation invitation = menuItems.get(position);
	        
	    	title.setText(invitation.getSurveyTitle());
	    	// convert date text
	    	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
		    Date dateObject;
		
			dateObject = format.parse(menuItems.get(position).getDateInvited());
    	    date.setText(formatter.format(dateObject));
		
			Resources res = parent.getResources();
			int answeredQuestions = invitation.getNumberSurveyQuestionsAnswered();
			int totalQuestions = invitation.getNumberSurveyQuestions();
			String completedQuestionsText = String.format(res.getString(R.string.completed_questions), answeredQuestions, totalQuestions);
			
			TextView minesLabel = (TextView) row.findViewById(R.id.txtCompletedQuestions);
			minesLabel.setText(completedQuestionsText);
			
	        // Survey Icons
			boolean partialCompleted = answeredQuestions > 0 && answeredQuestions < totalQuestions;
			boolean allCompleted = answeredQuestions == totalQuestions;

			if(partialCompleted) {
				surveyIcon.setImageResource(R.drawable.survey_partial);
			} else if(allCompleted){
				surveyIcon.setImageResource(R.drawable.survey_complete);
			}
			
			// Visibily set background color of row to partially/all completed surveys
			if(allCompleted || partialCompleted) {
				row.setBackgroundResource(R.color.survey_visit_highlight);
				
				int darkGrey = parent.getResources().getColor(R.color.darkgrey);
				title.setTextColor(darkGrey);
				title.setTypeface(null, Typeface.NORMAL);
			}
		
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
        return row;
    }
}