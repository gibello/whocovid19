package com.gibello.whocovid19;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

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
		stripper.setEndPage(8);

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
		    if (scanner.hasNext(".*egion.*")) {
		    	String line = scanner.next();
		    	if(! inData()) {
		    		if(line.length() < 40) {
		    			if(dataScore >= INDATA_THRESHOLD-1) dataScore++;
		    		}
		    	}
		    } else if(scanner.hasNext("Grand total.*")) {
		    	data.append(scanner.next().trim() + sep + "n/a" + sep + "n/a");
		        dataScore = 0; // Out of data
		    } else {
		    	String line = scanner.next().trim();
		    	boolean endOfLine = line.matches(".*\\d+$");
		    	if(inData() && !line.startsWith("Territories") && !line.startsWith("Subtotal")) {
		    		data.append(line + (endOfLine ? "\n" : " "));
		    	}
		    }
		}
		scanner.close();
		data.append("\n" + chn); // China data if any (from reports prior to march 15, 2020)
		
		return postProcess(data.toString(), sep);
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
					|| c == '[' || c == ']')) raw.setCharAt(i, ' ');
			if(c == 'é') raw.setCharAt(i, 'e');
			if(c == 'ô') raw.setCharAt(i, 'o');
		}
		if(! rawData.contains("Hubei")) return raw.toString(); // China among other countries
		else { // China report by province (before march 16, 2020)
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
	private static String postProcess(String rawData, char sep) {
		StringBuilder data = new StringBuilder("Country" + sep + "Confirmed cases" + sep + "New cases" + sep + "Deaths" + sep + "New deaths" + sep + "Transmission type" + sep + "Days since last case\n");
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
				if(line.contains("Grand total")) {
					int pos = line.lastIndexOf("n/a");
					line = line.substring(line.indexOf("Grand total"), pos+3);
					// Grand total gross value may contain a space (already replaced by a "sep")
					String gtdata = line.substring(12);
					if((pos = gtdata.indexOf(sep)) < 4) {
						line = "Grand total" + sep + gtdata.substring(0, pos) + gtdata.substring(pos+1);
					}
				}
				if(isData) {
					// Fix country name if required
					int pos = line.indexOf(sep);
					String country = line.substring(0, pos);
					String fix = fixCountryName(country);
					if(!country.equals(fix)) {
						line = fix + line.substring(pos);
					}
					data.append(line.trim() + "\n");
				}
			}
		}
		scanner.close();
		return data.toString();
	}
	
	/**
	 * Country names are a bit fuzzy in WHO reports...
	 * Try to fix them when possible
	 * @param name The country name as of WHO report
	 * @return The fixed country name
	 */
	private static String fixCountryName(String name) {
		HashMap<String, String> names = new HashMap<String, String>();
		names.put("occupied Palestinian territory", "Occupied Palestinian Territory");
		names.put("Occupied Palestinian territory", "Occupied Palestinian Territory");
		names.put("occupied Palestinian Territory", "Occupied Palestinian Territory");
		names.put("the United Kingdom", "The United Kingdom");
		names.put("the United States", "United States of America");
		names.put("Cote d Ivoire", "Cote d'Ivoire"); // The quote in who report is not a real quote for this country...
		names.put("Curacao", "Curaçao");
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
