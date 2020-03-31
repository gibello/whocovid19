package com.gibello.whocovid19;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Unit test for WHO Covid19 report parser
 */
public class WHOReportParserTest extends TestCase {

    /**
     * Test extractFromPDF
     */
    public void testExtractFromPDF() {
    	String data = null;
    	try {
    		// WHOReport.pdf is the WHO Covid19 report as of march 17, 2020
    		// Originally available on the web as
    		// https://www.who.int/docs/default-source/coronaviruse/situation-reports/20200317-sitrep-57-covid-19.pdf
			data = WHOReportParser.extractFromPDF(
					new File(getClass().getClassLoader()
							.getResource("WHOReport.pdf").getFile()), '|');
		} catch (IOException e) {
			e.printStackTrace(System.err);
			fail(e.getMessage());
		}
        assertTrue(data.contains("|Mexico|MEX|53|0|0|0|Imported cases only|1"));
    }

}
