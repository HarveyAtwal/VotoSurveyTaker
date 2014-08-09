package org.votomobile.proxy.taker;


/**
 * Bean to store the question information from the server. Do
 * basic conversions for data types (String to int, ....).
 */
public class Question {
	/*
	 * Internal constants used to setup the introduction and conclusion as questions.
	 */
	static final int INTERNAL_ID_INTRODUCTION = 1;
	static final int INTERNAL_ID_CONCLUSION = Integer.MAX_VALUE;
	
	private String id;
	private String question_num;

	private String sms_prompt;
	private String audio_prompt_url;
	private String response_type;	
	private String[] options;
	
	private QuestionResponse response;
	
	public enum ResponseType {
		NONE,
		MULTIPLE_CHOICE,
		NUMERIC,
		OPEN,
		INTRODUCTION_OR_CONCLUSION
	}
	
	
	public Question() {
	}

	/**
	 * Internally used method to force a newly created question to become the
	 * introduction or conclusion, and to put it into the question order.
	 */
	void makeIntroductionOrConclusion(int fakeIdNumber) {
		response_type = "introduction_or_conclusion";
		id = "" + fakeIdNumber;
	}
	
	public int getId() {
		return Util.toInt(id);
	}

	/**
	 * Return the question order, as set by the server. Returns 0 for introduction or conclusion.
	 * @return
	 */
	public int getQuestionNumber() {
		return Util.toInt(question_num);
	}
	
	public ResponseType getResponseType() {
		if (response_type != null) {
			if (response_type.equals("multichoice")) {
				return ResponseType.MULTIPLE_CHOICE;
			}
			if (response_type.equals("numeric")) {
				return ResponseType.NUMERIC;
			}
			if (response_type.equals("open")) {
				return ResponseType.OPEN;
			}
			
			// Not actually a server supported type; just simplfy coding to client code.
			if (response_type.equals("introduction_or_conclusion")) {
				return ResponseType.INTRODUCTION_OR_CONCLUSION;
			}
		}
		
		// Unknown type (or null); may be for a message (i.e., not a question).
		return ResponseType.NONE;
	}
	
	// Prompts
	public String getSMSPrompt() {
		return sms_prompt;
	}
	public String getAudioPromptUrl() {
		return audio_prompt_url;
	}
	
	// MC Options
	public int getNumChoices() {
		if (options == null) {
			return 0;
		}
		return options.length;
	}
	public String[] getOptions() {
		return options; 
	}
	
	public QuestionResponse getResponse() {
		return response;
	}
}
