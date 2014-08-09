package org.votomobile.datamodel.taker;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.votomobile.proxy.taker.Language;
import org.votomobile.proxy.taker.VotoProxyBase;
import org.votomobile.proxy.taker.VotoSurveyTakerProxy;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Data model for managing the preferred language.
 * UI can be notified of any change by subscribing for updates; code in base class.
 */
public class LanguageManager extends AbstractManager {
	private VotoSurveyTakerProxy proxy;
	private List<Language> languages = new ArrayList<Language>();
	private Language preferredLanguage = null;
	
	public LanguageManager(String apiKey, Context context) {
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
	 * Reload all languages from server.
	 */
	public void fetchAllLanguages() {
		// First wipe out the current list (if any).
		languages.clear();
		preferredLanguage = null;
		
		// Update from server.
		proxy.fetchLanguages(
				new Response.Listener<Language[]>() {
					// When data received, process it.
					@Override
					public void onResponse(Language[] response) {
						processAvailableLanguagesDataArrive(response);
					}
				});
		fetchPreferredLanguage();
	}
	private void fetchPreferredLanguage() {
		proxy.fetchPreferredLanguage(
				new Response.Listener<Language>() {
					// When data received, process it.
					@Override
					public void onResponse(Language response) {
						processPreferredLanguageDataArrive(response);
					}
				});
	}

	
	/**
	 * Find a language by its ID. Throws InvalidParameterException if unknown ID.
	 * @param languageId Language ID 
	 * @return The language object for the requested ID.
	 */
	public Language findLanguageById(int languageId) {
		int langIndex = findLanguageIndexById(languageId);
		if (langIndex < 0) {
			throw new InvalidParameterException("Unknown language ID.");
		}
		return getLanguageAt(langIndex);
		
	}

	private int findLanguageIndexById(int languageId) {
		int index = 0;
		for (Language language : languages) {
			if (language.getId() == languageId) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Get access to a language based on its Index
	 * @param index Index of language (0-indexed)
	 * @return
	 */
	public Language getLanguageAt(int index) {
		return languages.get(index);
	}
	
	/**
	 * Allow iteration of the languages.
	 * @return Iterable object to support for-each loop.
	 */
	public Iterable<Language> languages() {
		return new Iterable<Language>() {
			@Override
			public Iterator<Language> iterator() {
				return Collections.unmodifiableCollection(languages).iterator();
			}
		};
	}

	
	/*
	 * Manage Preferred Language
	 */
	public Language getPreferredLanguage() {
		return preferredLanguage;
	}
	
	public void setPreferredLanguage(int languageId) {
		proxy.setPreferredLanguage(
				languageId, 
				new Response.Listener<Language>() {
					// When data received, process it.
					@Override
					public void onResponse(Language response) {
						processPreferredLanguageDataArrive(response);
					}

				});
	}
	
	
	/*
	 * Method for handling return from server.
	 */
	private void processAvailableLanguagesDataArrive(Language[] incomingLanguages) {
		for (Language language : incomingLanguages) {
			// If the element exists, swap with new one; else just add.
			// This keeps the list in order.
			int index = findLanguageIndexById(language.getId());
			if (index >= 0) {
				languages.set(index, language);
			} else {
				languages.add(language);
			}
		}
		notifyDataChangeListeners();
	}	
	private void processPreferredLanguageDataArrive(Language response) {
		preferredLanguage = response;
		notifyDataChangeListeners();
	}
}
