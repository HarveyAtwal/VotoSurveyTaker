package org.votomobile.proxy.taker;

/**
 * The response to a survey question.
 */
public class QuestionResponse {
	private String submitted_date;   // when the response was submitted (in GMT)
	private String id;               // unique identifier for this response
	private String option_chosen;    // (1-based index in options) or null
	private String open_text;        // response text, or null if not an open-ended text response
	private String numeric_response; // number, or null if not a numeric response
	private String open_audio_url;   // where to find the spoken open-ended response, or false
	
	public int getId() {
		return Util.toInt(id);
	}
	public String getSubmittedDate() {
		return submitted_date;
	}
	
	// 0-indexed.
	public int getOptionChosenIndex() {
		return Util.toInt(option_chosen) - 1;
	}
	public String getOpenText() {
		return open_text;
	}
	public int getNumericResponse() {
		return Util.toInt(numeric_response);
	}
	public String getOpenAudioUrl() {
		return open_audio_url;
	}
		
	/**
	 * 
	 * @param optionChosen 0-indexed option 
	 */
	public void setOptionChosen(int optionChosen) {
		this.option_chosen = "" + (optionChosen + 1);
	}
	public void setOpenText(String openText) {
		this.open_text = openText;
	}
	public void setNumericResponse(int numericResponse) {
		this.numeric_response = "" + numericResponse;
	}
}
