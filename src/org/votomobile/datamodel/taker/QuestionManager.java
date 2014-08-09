package org.votomobile.datamodel.taker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.votomobile.proxy.taker.Question;
import org.votomobile.proxy.taker.QuestionResponse;
import org.votomobile.proxy.taker.VotoProxyBase;
import org.votomobile.proxy.taker.VotoSurveyTakerProxy;

import android.content.Context;


import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Data model for managing questions, and loading them via the proxy from the remote server.
 * UI can be notified of any change by subscribing for updates; code in base class.
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
	
	// Give base class access to our proxy.
	@Override
	protected VotoProxyBase getProxy() {
		return proxy;
	}

	public void setCurrentSurveyId(int surveyId) {
		this.currentInvitationId = surveyId;
		questions.clear();
		notifyDataChangeListeners();
	}
	public int getCurrentSurveyId() {
		return currentInvitationId;
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

	private int findIndexById(int id) {
		int index = 0;
		for (Question question : questions) {
			if (question.getId() == id) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Access a single question, introduction, or conclusion by its index in the list.
	 * Introduction (if present) will be first; conclusion (if present) will be last.
	 * @param index
	 * @return
	 */
	public Question getAt(int index) {
		return questions.get(index);
	}
	
	/**
	 * Return the number of questions (+1 if there's an intro, +1 if there's a conclusion).
	 * @return
	 */
	public int getNumQuestions() {
		return questions.size();
	}

	/**
	 * Access an iterator for all of the questions (plus the introduction and conclusion). 
	 * @return
	 */
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
}
