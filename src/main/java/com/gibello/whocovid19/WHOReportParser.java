package com.gibello.whocovid19;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Extract data from WHO Covid19 reports
 * Example: https://www.who.int/docs/default-source/coronaviruse/situation-reports/20200317-sitrep-57-covid-19.pdf
 * Should not work for reports before March 2, 2020
 */
public class WHOReportParser {

	public static String extractFromPDF(File pdfReport, char sep) throws IOException {
		PDDocument document = PDDocument.load(pdfReport);
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setStartPage(2);
		stripper.setEndPage(7);
		
		StringBuilder chn = new StringBuilder();
		String raw = preProcess(stripper.getText(document), sep, chn);

		boolean inData = false;
		StringBuilder data = new StringBuilder();
		Scanner scanner = new Scanner(raw);
		scanner.useDelimiter("[\r\n]");
		while (scanner.hasNext()) {
		    if (scanner.hasNext(".*egion.*")) {
		    	String line = scanner.next();
		    	if(! inData) {
		    		if(line.length() < 40) inData = true;
		    	}
		    } else if(scanner.hasNext("Grand total.*")) {
		    	data.append(scanner.next().trim() + sep + "n/a" + sep + "n/a");
		        inData = false;
		    } else {
		    	String line = scanner.next().trim();
		    	boolean endOfLine = line.matches(".*\\d+$");
		    	if(inData && !line.startsWith("Territories") && !line.startsWith("Subtotal")) {
		    		data.append(line + (endOfLine ? "\n" : " "));
		    	}
		    }
		}
		scanner.close();
		data.append("\n" + chn); // China data if any (from reports prior to march 15, 2020)
		
		return postProcess(data.toString(), sep);
	}
	
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
			if(! (Character.isLetterOrDigit(c) || Character.isWhitespace(c) || c == '(' || c == ')')) raw.setCharAt(i, ' ');
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
	 * @param sep
	 * @return
	 */
	private static String postProcess(String rawData, char sep) {
		StringBuilder data = new StringBuilder("Country" + sep + "Confirmed cases" + sep + "New cases" + sep + "Deaths" + sep + "New deaths" + sep + "Transmission type" + sep + "Days since last case\n");
		Scanner scanner = new Scanner(rawData);
		scanner.useDelimiter("[\r\n]");
		while (scanner.hasNext()) {
			boolean isData = ! scanner.hasNext("\\d+.*");
			String line = scanner.next();
			if(isData) {
				line = line.replaceAll("\\s+(\\d+)", sep + "$1").replaceFirst("(\\d) ", "$1" + sep);
				data.append(line.trim() + "\n");
			}
		}
		scanner.close();
		return data.toString();
	}

    public static void main(String[] args) throws Exception {
    	String pdfReport = "/tmp/report.pdf";
    	if(args.length > 0) pdfReport = args[0];
        System.out.println(WHOReportParser.extractFromPDF(new File(pdfReport), ','));
    }
}
