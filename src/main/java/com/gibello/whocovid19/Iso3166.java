package com.gibello.whocovid19;

import java.util.List;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
		String ret = names.get(name);
		return (ret == null ? name : ret);
	}
	
	public static String getCountryCode(String country) {
		List<CountryCode> codes = CountryCode.findByName(isoCountryName(country));
		if(! codes.isEmpty()) return codes.get(0).getAlpha3();
		return "n/a";
	}

	/* THIS CODE WAS USED TO CONVERT OLD csv FILES (without country code)
	 * KEPT for testing purposes if required
	public static void main(String args[]) throws Exception {
		parseDirectory(new File("/tmp/csv"), new File("/tmp/new-reports"));
		System.exit(0);

		File input = new File("/tmp/countries.txt");
		BufferedReader in = new BufferedReader(new FileReader(input));
		String country;
		while((country = in.readLine()) != null) {
			System.out.println(country + "=" + getCountryCode(country));
		}
		in.close();
	}
	
	public static void parseDirectory(File srcDir, File destDir) throws IOException {
		if(! destDir.exists()) destDir.mkdirs();
		if(! destDir.isDirectory()) throw new IOException(destDir + " should be a directory");
		File files[] = srcDir.listFiles();
		for (int i=0; i<files.length; i++) {
			String fname = files[i].getName();
			if(fname.endsWith(".csv")) {
				System.err.print("Parsing " + fname + " ... ");
				File csv = new File(destDir, fname);
				PrintWriter out = null;
				BufferedReader in = new BufferedReader(new FileReader(files[i]));
				try {
					out = new PrintWriter(csv);
					String line;
					while((line = in.readLine()) != null) {
						int pos = line.indexOf(',');
						if(pos <= 0) continue;
						String country = line.substring(0, pos);
						String code;
						if(country.startsWith("International") || country.startsWith("Grand total")) {
							code = "n/a";
						}
						else if(country.contentEquals("Country")) {
							code = "ISO-3166 code";
						}
						else {
							code = getCountryCode(country);
						}
						out.println(country + "," + code + line.substring(pos));
					}
				} catch(IOException ioe) {
				} finally {
					out.close();
				}
				System.err.println("Done !");
			}
		}
	} */
}
