package com.gibello.whocovid19;

import java.util.List;
import java.util.HashMap;

import com.neovisionaries.i18n.CountryCode;

public class Iso3166 {
	
	private static String isoCountryName(String name) {
		HashMap<String, String> names = new HashMap<String, String>();
		names.put("Bolivia (Plurinational State of)", "Bolivia.*");
		names.put("British Virgin Islands", "Virgin Islands, British");
		names.put("Cabo Verde", "Cape Verde");
		names.put("Cote d'Ivoire", ".*Ivoire");
		names.put("Czechia", "Czech Republic");
		names.put("Democratic Republic of the Congo", "Congo, the Democratic.*");
		names.put("Guinea Bissau", "Guinea-Bissau");
		names.put("Holy See", "Holy See.*");
		names.put("Iran (Islamic Republic of)", "Iran, Islamic.*");
		names.put("Kosovo", "Kosovo.*");
		names.put("Northern Mariana Islands (Commonwealth of the)", "Northern Mariana Islands.*");
		names.put("Northern Mariana Islands (Common wealth of the)", "Northern Mariana Islands.*");
		names.put("Northern Mariana Islands (Common-wealth of the)", "Northern Mariana Islands.*");
		names.put("North Macedonia", "North Macedonia, Republic of");
		names.put("Occupied Palestinian Territory", "Palestine.*");
		names.put("Republic of Korea", "Korea, Republic of");
		names.put("Republic of Moldova", "Moldova.*");
		names.put("Reunion", "Réunion");
		names.put("Saint Barthelemy", "Saint Barth.*");
		names.put("Saint Martin", "Saint Martin.*");
		names.put("Sint Maarten", "Sint Maarten.*");
		names.put("The United Kingdom", "United Kingdom.*");
		names.put("Timor Leste", "Timor-Leste");
		names.put("United Republic of Tanzania", "Tanzania.*");
		names.put("United States of America", "United States");
		names.put("United States Virgin Islands", "Virgin Islands, U.S.");
		names.put("Venezuela (Bolivarian Republic of)", "Venezuela.*");
		names.put("Bonaire Sint Eustatius and Saba", "Bonaire.*");
		names.put("Falkland Islands (Malvinas)", "Falkland.*");
		String ret = names.get(name);
		return (ret == null ? name : ret);
	}
	
	public static String getCountryCode(String country) {
		List<CountryCode> codes = CountryCode.findByName(isoCountryName(country));
		if(! codes.isEmpty()) return codes.get(0).getAlpha3();
		else if(country.startsWith("International") || country.startsWith("Other")) return "---";
		else if(country.startsWith("Grand total")) return "WLD";
		return "n/a";
	}

}
