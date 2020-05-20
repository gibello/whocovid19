package com.gibello.whocovid19;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Extract data from WHO Covid19 reports
 * Example: https://www.who.int/docs/default-source/coronaviruse/situation-reports/20200317-sitrep-57-covid-19.pdf
 * Should not work for reports before March 2, 2020
 */
public class WHOReportParser {

	static final short INDATA_THRESHOLD = 3;
	static short dataScore = 0;

	public static String extractFromPDF(File pdfReport, char sep) throws IOException {
		PDDocument document = PDDocument.load(pdfReport);
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setStartPage(2);
		stripper.setEndPage(stripper.getEndPage()-3);

		StringBuilder chn = new StringBuilder();
		String raw = preProcess(stripper.getText(document), sep, chn);
		document.close();
		
		StringBuilder data = new StringBuilder();
		Scanner scanner = new Scanner(raw);
		scanner.useDelimiter("[\r\n]");
		while (scanner.hasNext()) {
			// Warning for datascore calculation: old reports have been pre-processed (all China province data removed).
			if(scanner.hasNext(".*Days since.*")) dataScore++;
			if(scanner.hasNext(".*Data as of.*")) dataScore++;
			// Starting May 2... 1st region "Africa" (and no more "region" in name, like in previous "African Region")
		    if (scanner.hasNext(".*egion.*") || scanner.hasNext("Africa")) {
		    	String line = scanner.next();
		    	if(! inData()) {
		    		if(line.length() < 40) {
		    			if(dataScore >= INDATA_THRESHOLD-1) dataScore++;
		    		}
		    	}
		    } else if(scanner.hasNext("Grand total.*")) {
		    	String gt = scanner.next().trim();
		    	if(gt.equals("Grand total")) { // Grand total on 2 lines...
		    		data.append(gt + sep + scanner.next().trim() + sep + "n/a" + sep + "0");
		    	}
		    	else data.append(gt + sep + "n/a" + sep + "0"); // Grand total on single line (always before may 3)
		        dataScore = 0; // Out of data
		    } else {
		    	String line = scanner.next().trim();
		    	boolean endOfLine = line.matches(".*\\d+$");
		    	if(inData() && !line.startsWith("Territories") && !line.startsWith("Subtotal")
		    			&& !line.startsWith("Reporting") && !line.startsWith("Territory") && !line.startsWith("Total")
		    			// region names to be filtered (since May 2)
		    			&& !line.startsWith("Africa") && !line.startsWith("Americas") && !line.startsWith("Eastern Mediterranean") && !line.startsWith("Europe")
		    			&& !line.startsWith("South-East Asia") && !line.startsWith("Western Pacific")
		    			//TODO New issues since may 2 new format (filter below) - to be checked ?
		    			&& !line.contains("classification") && !line.contains("Days") && !line.contains("reported case")
		    			&& !line.equals("cases") && !line.equals("new cases")) {
		    		//System.out.println("########" + line);
		    		data.append(line + (endOfLine ? "\n" : " "));
		    	}
		    }
		}
		scanner.close();
		data.append("\n" + chn); // China data if any (from reports prior to march 15, 2020)

		return postProcess(data.toString(), sep, extractDate(pdfReport.getName()));
	}

	protected static String extractDate(String reportName) {
		String date = reportName.substring(0, 8);
		LocalDate currentDate;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			currentDate = LocalDate.parse(date, formatter);
		} catch(DateTimeParseException e) {
			currentDate = LocalDate.now().minusDays(1); // Report supposedly of yesterday
		}
		return currentDate.minusDays(1).toString();
	}

	/**
	 * Likely to be inside relevant data ?
	 * @return true if data score high enough, false otherwise
	 */
	private static boolean inData() { return WHOReportParser.dataScore >= WHOReportParser.INDATA_THRESHOLD; }
	
	/**
	 * Reports between march 2 and march 15 2020 expose China by province...
	 * Cleanup text, then pre-process that.
	 * @param rawData
	 * @return
	 */
	private static String preProcess(String rawData, char sep, StringBuilder chn) {
		StringBuilder raw = new StringBuilder(rawData);
		for(int i=0; i<raw.length(); i++) {
			char c = raw.charAt(i);
			if(! (Character.isLetterOrDigit(c) || Character.isWhitespace(c) || c == '(' || c == ')' || c == '\''
					|| c == '[' || c == ']' || c == '-')) raw.setCharAt(i, ' ');
			if(c == 'ã') raw.setCharAt(i, 'a');
			if(c == 'é') raw.setCharAt(i, 'e');
			if(c == 'í') raw.setCharAt(i, 'i');
			if(c == 'ô') raw.setCharAt(i, 'o');
		}

		if(! rawData.contains("Hubei")) { // China among other countries
			// Issue with "Community Transmission" (on 3 lines: country name + data / "Community" / "Transmission" + days since last case)
			StringBuilder data = new StringBuilder();
			Scanner scanner = new Scanner(raw.toString());
			scanner.useDelimiter("[\r\n]");
			String prevLine = "";
			while (scanner.hasNext()) {
				String line = scanner.next().trim();
				if(line.equals("Community")) continue;

				// "Community" may be alone on one line, or "transmission"
				// See for example reports #85 and #86 (data as of O4/14 and 04/15).
				if(line.startsWith("ransmission", 1) && ! prevLine.endsWith("Community")) {
					data.append(prevLine + ",Community " + line + "\n");
				}
				else data.append(prevLine + "\n");

				prevLine = line;
			}
			scanner.close();
			return data.toString();

		} else { // China report by province (before march 16, 2020)
			StringBuilder data = new StringBuilder();
			Scanner scanner = new Scanner(raw.toString());
			scanner.useDelimiter("[\r\n]");
			boolean inData = false;
			while (scanner.hasNext()) {
				if (scanner.hasNext("Total\\s+\\d.*") && chn != null) { // Total china
					// Parse line for China... otherwise China won't appear.
					// eg. Total 142823 146 102 30 80711 3045
					// Format: population/new cases/new suspected cases/new deaths/confirmed cases/deaths
					// Expected: "China"/confirmed cases/new cases/deaths/new deaths/"Under investigation"/"0"
					inData = true;
					String line = scanner.next();
					String[] parts = line.split(" ");
					chn.append("China" + sep + parts[5] + sep + parts[2] + sep + parts[6] + sep + parts[4] + sep + "Under investigation" + sep + "0\n");
				} else if(scanner.hasNext("Grand total.*")) {
					data.append(scanner.next() + "\n");
					inData = false;
				} else {
					String line = scanner.next();
					if(inData) data.append(line + "\n");
				}
			}
			scanner.close();
			return data.toString();
		}
	}

	/**
	 * Post-process and cleanup data.
	 * @param rawData
	 * @param sep The separator to use in CSV
	 * @return
	 */
	private static String postProcess(String rawData, char sep, String date) {
		
		StringBuilder data = new StringBuilder("Date" + sep + "Country" + sep + "ISO-3166 code" + sep + "Confirmed cases" + sep + "New cases" + sep + "Deaths" + sep + "New deaths" + sep + "Transmission type" + sep + "Days since last case\n");
		Scanner scanner = new Scanner(rawData);
		scanner.useDelimiter("[\r\n]");
		while (scanner.hasNext()) {
			boolean isData = ! scanner.hasNext("\\d+.*");
			String line = scanner.next().trim();
			if(isData) {
				// 1st replace removes footnote refs like in "Kosovo[1]"
				// next replace statements put commas btw figures
				line = line.replaceFirst("\\[\\d+\\]", "").replaceAll("\\s+(\\d+)", sep + "$1").replaceFirst("(\\d) ", "$1" + sep);

				// Specific issue with "Grand total" and "Diamond princess" lines (as "Diamond princess" title is sometimes multi-line)
				// Patch that !
				if(line.startsWith("International")) {
					int pos = line.indexOf(sep);
					if(pos <= 0) isData = false;
					else line = "International conveyance (Diamond Princess)" + line.substring(pos);
				}
				// Starting May 2, "International conveyance (Diamond Princess)" is now called "Other"...
				if(line.startsWith("Other")) {
					line = line.replace("Other", "International conveyance (Diamond Princess)").replace("-", "n/a");
				}
				if(line.contains("Grand total")) {
					int pos = line.lastIndexOf("0");
					line = line.substring(line.indexOf("Grand total"), pos+1);
					// Grand total gross value may contain a space (already replaced by a "sep")
					String gtdata = line.substring(12);
					if((pos = gtdata.indexOf(sep)) < 4) {
						line = "Grand total" + sep + gtdata.substring(0, pos) + gtdata.substring(pos+1);
					}
				}
				if(isData && line.length() > 20) {
					// Filter out too weird lines (sometimes pdf syntax not clean)
					if(Character.isAlphabetic(line.charAt(0)) && line.indexOf(sep) > 0) {
						// Fix country name if required
						int pos = line.indexOf(sep);
						String country = line.substring(0, pos);
						String fix = fixCountryName(country);
						line = date + sep + fix + sep + Iso3166.getCountryCode(fix) + line.substring(pos);
						//data.append(line.trim() + "\n");
						data.append(fixLineFigures(line, sep));
					}
				}
			}
		}
		scanner.close();
		return data.toString();
	}

	/**
	 * Some figures may contain spaces in source PDF (eg. 10 000 for 10000). Try to fix that...
	 * @param line The line with possibly too many tokens (spaces have been replaced by commas)
	 * @return The line with exactly 9 tokens
	 */
	private static String fixLineFigures(String line, char sep) {
		String ret = line.trim() + "\n";
		String values[] = new String[9];
		String strings[] = new String[5];
		String elements[] = new String[9];
		int nbval = 0;
		int nbstrings = 0;
		StringTokenizer st = new StringTokenizer(line, Character.toString(sep));
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			try {
				Integer.parseInt(token);
				values[nbval++] = token; // Do not parse int (may start with 0)
			} catch(Exception e) {
				strings[nbstrings++] = token;
			}
		}
		elements[0] = strings[0];
		elements[1] = strings[1];
		elements[2] = strings[2];
		elements[7] = strings[3];
		
		// There should be 5 numbers in line...
		if(nbval == 5) {
			return ret;  // NO CHECK WAS REQUIRED !
		} else if(nbval == 6) { // Space in "confirmed cases"
			elements[3] = values[0] + values[1];
			elements[4] = values[2];
			elements[5] = values[3];
			elements[6] = values[4];
			elements[8] = values[5];
		} else if(nbval == 7) { // Space in "confirmed cases" + "Deaths"
			elements[3] = values[0] + values[1]; 	// confirmed cases
			elements[4] = values[2]; 				// new cases
			elements[5] = values[3] + values[4];	// deaths
			elements[6] = values[5];				// new deaths
			if(Integer.parseInt(elements[5]) >= Integer.parseInt(elements[3])) {
				elements[4] = values[2] + values[3];
				elements[5] = values[4];
			}
			elements[8] = values[6];
		} else if(nbval >= 8) { // Space in "confirmed cases" + "New cases" + "Deaths"
			elements[3] = values[0] + values[1] + (nbval > 8 ? values[2] : "");
			int shift = (nbval > 8 ? 1 : 0);
			elements[4] = values[2+shift] + values[3+shift];
			elements[5] = values[4+shift] + values[5+shift];
			elements[6] = values[6+shift];
			elements[8] = values[7+shift];
		}
		StringBuilder result = new StringBuilder();
		for (int i=0; i<9; i++) {
			result.append((i == 0 ? "" : sep) + elements[i]);
		}
		
		boolean toCheck = (nbstrings > 4 || nbval > 8); // Very suspicious syntax...
		if(! toCheck) {
			try {
				toCheck = (Integer.parseInt(elements[3]) > 99999 // More than 100.000 confirmed
					|| Integer.parseInt(elements[4]) > 9999); // Many new cases
				if(! toCheck) toCheck = (Integer.parseInt(elements[8]) > 0); // At least & day since last
			} catch(Exception e) { toCheck = true; }
		}
		if(toCheck) System.err.println("Warning: check required for " + result.toString().substring(11));
		return result.toString() + "\n";
	}

	/**
	 * Country names are a bit fuzzy in WHO reports...
	 * Try to fix them when possible
	 * @param name The country name as of WHO report
	 * @return The fixed country name
	 */
	private static String fixCountryName(String name) {
		HashMap<String, String> names = new HashMap<String, String>();
		if(name.contains("Kosovo")) name = "Kosovo"; // Sometimes weird name appears for Kosovo...
		names.put("occupied Palestinian territory", "Occupied Palestinian Territory");
		names.put("Occupied Palestinian territory", "Occupied Palestinian Territory");
		names.put("occupied Palestinian Territory", "Occupied Palestinian Territory");
		names.put("the United Kingdom", "The United Kingdom");
		names.put("the United States", "United States of America");
		names.put("Cote d Ivoire", "Cote d'Ivoire"); // The quote in who report is not a real quote for this country...
		names.put("Curacao", "Curaçao");
		names.put("Lao People s Democratic Republic", "Lao People's Democratic Republic");
		names.put("Bonaire  Sint Eustatius and Saba", "Bonaire Sint Eustatius and Saba"); // Should be a comma after "Bonaire"... but CSV issue !
		String ret = names.get(name);
		return (ret == null ? name : ret);
	}

	/**
	 * Parse multiple reports in a directory
	 * @param srcDir Source directory, with PDF reports
	 * @param destDir Destination directory, for CSV data files
	 * @throws IOException
	 */
	public static void parseDirectory(File srcDir, File destDir) throws IOException {
		if(! destDir.exists()) destDir.mkdirs();
		if(! destDir.isDirectory()) throw new IOException(destDir + " should be a directory");
		File files[] = srcDir.listFiles();
		for (int i=0; i<files.length; i++) {
			String fname = files[i].getName();
			if(fname.endsWith(".pdf")) {
				System.err.print("Parsing " + fname + " ... ");
				String baseName = fname.substring(0, fname.lastIndexOf('.'));
				File csv = new File(destDir, baseName + ".csv");
				PrintWriter out = null;
				try {
					out = new PrintWriter(csv);
					out.print(WHOReportParser.extractFromPDF(files[i], ','));
				} catch(IOException ioe) {
				} finally {
					out.close();
				}
				System.err.println("Done !");
			}
		}
	}

    public static void main(String[] args) throws Exception {
    	String defaultReport = System.getProperty("java.io.tmpdir");
    	if(! defaultReport.endsWith(File.separator)) defaultReport += File.separator;
    	defaultReport += "report.pdf";
  
    	String pdfReport = defaultReport;
    	if(args.length > 0) {
    		pdfReport = args[0];
    		File src = new File(pdfReport);
    		if(src.isDirectory()) {
    			if(args.length < 2) throw new IOException("Usage: WHOReportParser <sourceDir> <targetDir>");
    			File destDir = new File(args[1]);
    			WHOReportParser.parseDirectory(src, destDir);
    		} else {
    			System.out.println(WHOReportParser.extractFromPDF(new File(pdfReport), ','));
    		}
    	} else {
    		System.err.println("No source file specified, trying " + defaultReport);
    		System.out.println(WHOReportParser.extractFromPDF(new File(defaultReport), ','));
    	}
        
    }
}
