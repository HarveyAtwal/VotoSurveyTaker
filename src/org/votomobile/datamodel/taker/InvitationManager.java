package org.votomobile.datamodel.taker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.votomobile.proxy.taker.Invitation;
import org.votomobile.proxy.taker.VotoProxyBase;
import org.votomobile.proxy.taker.VotoSurveyTakerProxy;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Data model for managing surveys, and loading them via the proxy from the remote server.
 * UI can be notified of any change by subscribing for updates; code in base class.
 */
public class InvitationManager extends AbstractManager{
	
	private VotoSurveyTakerProxy proxy;
	private List<Invitation> invitations = new ArrayList<Invitation>();

	/**
	 * Create the Invitation Manager
	 * @param apiKey API Key to use with server
	 * @param context The application's context; used for setting networking queue.
	 */
	public InvitationManager(String apiKey, Context context) {
		proxy = new VotoSurveyTakerProxy(
			apiKey, 
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

	/**
	 * Reload all surveys from server.
	 */
	public void fetchAllInvitations() {
		// First wipe out the current list (if any).
		invitations.clear();
		
		// Update from server.
		proxy.getOutgoingInvitations(
				new Response.Listener<Invitation[]>() {
					// When data received, process it.
					@Override
					public void onResponse(Invitation[] response) {
						processInvitationDataArrive(response);
					}
				});
	}
	
	private int findInvitationIndexById(int invitationId) { 
		int index = 0;
		for (Invitation invitation : invitations) {
			if (invitation.getId() == invitationId) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Get access to a survey based on its Indexd
	 * @param index Index of survey (0-indexed)
	 * @return
	 */
	public Invitation getInvitationAt(int index) {
		return invitations.get(index);
	}
	
	/**
	 * Allow iteration of the surveys.
	 * @return Iterable object to support for-each loop.
	 */
	public Iterable<Invitation> invitations() {
		return new Iterable<Invitation>() {
			@Override
			public Iterator<Invitation> iterator() {
				return Collections.unmodifiableCollection(invitations).iterator();
			}
		};
	}
	
	/*
	 * Methods for updating a data 
	 */
	private void processInvitationDataArrive(Invitation[] incomingInvitations) {
		for (Invitation invitation : incomingInvitations) {
			int index = findInvitationIndexById(invitation.getId());

			// Handle invitations without any items (messages or surveys) and remove them:
			if (invitation.getMessageId() == 0 && invitation.getSurveyId() == 0) {
				// Invalid: remove if already in list.
				if (index >= 0) {
					invitations.remove(index);
				}
				continue;
			} else {
				// It's a good invitation

				// If the element exists, swap with new one; else just add.
				// This keeps the list in order.
				if (index >= 0) {
					invitations.set(index, invitation);
				} else {
					invitations.add(invitation);
				}
			}
		}
		notifyDataChangeListeners();
	}	
}
