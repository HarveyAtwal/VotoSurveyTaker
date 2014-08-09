package ca.cmpt276.votosurveytaker.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.votomobile.datamodel.taker.AbstractManager;
import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.Question.ResponseType;
import org.votomobile.proxy.taker.QuestionResponse;
import org.votomobile.proxy.taker.VotoProxyBase;
import org.votomobile.proxy.taker.VotoSurveyTakerProxy;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;


import ca.cmpt276.votosurveytaker.MainActivity;
import ca.cmpt276.votosurveytaker.QuestionActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Data model for managing questions, and loading them via the proxy from the remote server.
 * UI can be notified of any change by subscribing for updates; code in base class.
 * 
 * Copied some Question manager code from the proxy library and extended it
 */
public class QuestionManager extends AbstractManager {
	private VotoSurveyTakerProxy proxy;
	private List<Question> questions = new ArrayList<Question>();
	private int currentInvitationId;
	
	public QuestionManager(String regId, Context context) {
		proxy = new VotoSurveyTakerProxy(
			regId, 
			context, 
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					notifyErrorListeners(error);
				}
		});
	}
	
	public void setCurrentSurveyId(int surveyId) {
		this.currentInvitationId = surveyId;
		questions.clear();
		notifyDataChangeListeners();
	}
	public int getCurrentSurveyId() {
		return currentInvitationId;
	}
	
	public List<Question> getQuestions(){
		return questions;
	}
	
	public int getNumQuestions(){
		return questions.size();
	}
	
	public void fetchQuestions() {
		// First wipe out the current list (if any).
		questions.clear();
		
		// Update from server.
		proxy.getQuestions(
				currentInvitationId,
				new Response.Listener<Question[]>() {
					// When data received, process it.
					@Override
					public void onResponse(Question[] response) {
						processDataArrive(response);
					}
				});
	}

	private Question findById(int id) {
		return getAt(findIndexById(id));
	}

	public int findIndexById(int id) {
		int index = 0;
		for (Question question : questions) {
			if (question.getId() == id) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public void moveToNextQuestion(Fragment fragment, int id){
		QuestionActivity questionActivity = (QuestionActivity) fragment.getActivity();
		int totalNumFrags = questionActivity.getNumFragments();
		int currentPosition = findIndexById(id);
		
		if(currentPosition < totalNumFrags -1)
		{
			questionActivity.getPager().setCurrentItem(currentPosition + 1);
			Log.i("moveToNextQuestion", "moving to next question");
		}
		else
		{
			Intent homeIntent = new Intent(questionActivity, MainActivity.class);
  	      	questionActivity.startActivity(homeIntent);
  	      	questionActivity.finish();
		}
		
	}
	
	public int findFirstUnanswered(){
		int numAnswered = 0;
		int firstUnansweredIndex = -1;
		for(Question question : questions){
			if(question.getResponse() != null){
				numAnswered++;
			}else if(question.getResponse() == null && question.getResponseType() != ResponseType.INTRODUCTION_OR_CONCLUSION){
				if(firstUnansweredIndex == -1){
					firstUnansweredIndex = findIndexById(question.getId());
				}
			}
		}
		
		if(firstUnansweredIndex == -1){
			firstUnansweredIndex = 0;
		}
		
		if(numAnswered >0){
			return firstUnansweredIndex;
		}
		return 0;
	}

	public Question getAt(int index) {
		return questions.get(index);
	}
	public Iterable<Question> questions() {
		return new Iterable<Question>() {
			@Override
			public Iterator<Question> iterator() {
				return Collections.unmodifiableCollection(questions).iterator();
			}
		};
	}


	/*
	 * Manage Responses
	 */
	public void commitAnswer(final int questionId, QuestionResponse response) {
		proxy.respondToQuestion(
				currentInvitationId,
				questionId,
				response, 
				new Response.Listener<Integer>() {
					@Override
					public void onResponse(Integer nothing) {
						downloadOneQuestion(questionId);
					}
				});
	}
	public void commitAnswerChange(final int questionId) {
		Question question = findById(questionId);
		proxy.reRespondToQuestion(
				question.getResponse(), 
				new Response.Listener<Integer>() {
					@Override
					public void onResponse(Integer nothing) {
						downloadOneQuestion(questionId);
					}
				});
	}
	
	
	/*
	 * Methods for updating a question 
	 */
	
	private void downloadOneQuestion(int questionId) {
		proxy.getQuestionDetails(
				currentInvitationId,
				questionId,
				new Response.Listener<Question[]>() {
					@Override
					public void onResponse(Question[] response) {
						processDataArrive(response);
					}
				});
	}
	

	private void processDataArrive(Question[] incomingData) {
		for (Question question: incomingData) {
			// If the element exists, swap with new one; else just add.
			// This keeps the list in order.
			int index = findIndexById(question.getId());
			if (index >= 0) {
				questions.set(index, question);
			} else {
				questions.add(question);
			}
		}
		notifyDataChangeListeners();
	}

	@Override
	protected VotoProxyBase getProxy() {
		
		return null;
	}	
}
