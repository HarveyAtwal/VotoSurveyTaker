package org.votomobile.proxy.taker;

/**
 * Bean to store information about a language including its name, abbreviation and ID (assigned by server).
 */
public class Language {
	private String id;
	private String name;
	private String abbreviation;
	
	public Language() {
	}
	public Language(String name, String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public String getName() {
		return name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public int getId() {
		return Integer.parseInt(id);
	}	
}
