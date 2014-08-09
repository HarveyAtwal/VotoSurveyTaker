package org.votomobile.proxy.taker;

/**
 * Data bean to store information about a single invitation from the server.
 * Do basic conversions for types (String to int... as required).
 */
public class Invitation {
	private String id = "";
	private String date_invited = "";
	private String date_active = "";
	private String delivery_status = "";
	private String message_id = "";
	private String survey_id = "";
	private String survey_title = "";
	private String survey_questions_total = "";
	private String survey_questions_answered = "";
	
	public enum DeliveryStatus {
		NONE, 
		INVITED,
		IN_PROGRESS,
		COMPLETED
	}

	public Invitation() {
		
	}
	public int getId() {
		return Util.toInt(id);
	}
	public String getDateInvited() {
		return date_invited;
	}
	public String getDateActive() {
		return date_active;
	}
	public DeliveryStatus getDeliveryStatus() {
		if (delivery_status.equals("Invited")) {
			return DeliveryStatus.INVITED;
		}
		if (delivery_status.equals("In Progress")) {
			return DeliveryStatus.IN_PROGRESS;
		}
		if (delivery_status.equals("Complete")) {
			return DeliveryStatus.COMPLETED;
		}
		return DeliveryStatus.NONE;
	}
	public int getMessageId() {
		return Util.toInt(message_id);
	}
	public int getSurveyId() {
		return Util.toInt(survey_id);
	}
	public String getSurveyTitle() {
		if (survey_title != null) { 
			return survey_title;
		} else {
			return "INVALID: No survey for this invitation.";
		}
	}
	public int getNumberSurveyQuestions() {
		return Util.toInt(survey_questions_total);
	}
	public int getNumberSurveyQuestionsAnswered() {
		return Util.toInt(survey_questions_answered);
	}
}