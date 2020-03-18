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
		stripper.setStartPage(3);
		stripper.setEndPage(6);
		
		String raw = preProcess(stripper.getText(document));
		
		boolean inData = false;
		StringBuilder data = new StringBuilder();
		Scanner scanner = new Scanner(raw);
		scanner.useDelimiter("[\r\n]");
		while (scanner.hasNext()) {
		    if (scanner.hasNext(".*egion.*")) {
		    	if(! inData) inData = true;
		    	scanner.next();
		    } else if(scanner.hasNext("Grand total.*")) {
		    	data.append(scanner.next().trim() + sep + "n/a" + sep + "n/a");
		        inData = false;
		    } else {
		    	String line = scanner.next().trim();
		    	boolean endOfLine = line.matches(".*[a-zA-Z]\\s\\d+$") || line.matches("\\d+$");
		    	if(inData && !line.startsWith("Territories")) {
		    		data.append(line + (endOfLine ? "\n" : " "));
		    	}
		    }
		}
		scanner.close();
		
		return postProcess(data.toString(), sep);
	}
	
	/**
	 * Reports between march 2 and march 7 2020 expose China by province...
	 * Pre-process that.
	 * @param rawData
	 * @return
	 */
	private static String preProcess(String rawData) {
		if(! rawData.contains("Hubei")) return rawData; // China among other countries
		else { // China report by province (before march 7, 2020)
			StringBuilder data = new StringBuilder();
			Scanner scanner = new Scanner(rawData);
			scanner.useDelimiter("[\r\n]");
			boolean inData = false;
			while (scanner.hasNext()) {
				if (scanner.hasNext("Total\\s+\\d.*")) { // Total china
					inData = true;
					String line = scanner.next();
					//TODO parse line for China... otherwise China won't appear.
					// eg. Total 142823 146 102 30 80711 3045
					// Format: population/new cases/new suspected cases/new deaths/confirmed cases/deaths
					// Expected: "China"/confirmed cases/deaths/new deaths/"Under investigation"/"0"
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
		StringBuilder data = new StringBuilder("Country" + sep + "Confirmed cases" + sep + "Deaths" + sep + "New deaths" + sep + "Transmission type" + sep + "Days since last case\n");
		Scanner scanner = new Scanner(rawData);
		scanner.useDelimiter("[\r\n]");
		while (scanner.hasNext()) {
			String line = scanner.next();
			if(!line.startsWith("Subtotal")) {
				line = line.replaceAll(" (\\d+)", sep + "$1").replaceFirst("(\\d) ", "$1" + sep);
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
