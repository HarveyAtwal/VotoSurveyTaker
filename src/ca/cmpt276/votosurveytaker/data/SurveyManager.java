package ca.cmpt276.votosurveytaker.data;


import java.util.List;

import org.votomobile.datamodel.taker.InvitationManager;
import org.votomobile.proxy.taker.Invitation;


import android.content.Context;

/**
 * 
 * Fetches invitations from the server
 *
 */
public class SurveyManager extends InvitationManager {
	
	public SurveyManager(String apiKey, Context context) {
		super(apiKey, context);
	}
	
	public int findInvitationIndexById(List<Invitation> invitationList, int invitationId) { 
		
		int index = 0;
		for(Invitation invitation : invitationList){
			if (invitation.getId() == invitationId) {
				return index;
			}
			index++;
		}
		return -1;
	}
}
