import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Version 0.2: Handles reading and parsing of the course webpage to
 * retrieve the latest course info, as well as reading degree
 * and program requirements.
 * @author Eliana Wardle (38509121)
 * 
 * Note, this uses the jsoup library;
 * if you are using this from a new project location,
 * be sure to add the jsoup jar files (in src/lib) to the
 * build path or these functions may not work!
 * API docs available at http://jsoup.org/apidocs/.
 * 
 * (Incidentally, found this library with a cursory
 * Google search for "how to parse HTML in Java".)
 */

public class Crawler {

	/**
	 * Constant for the root pages (which link to each course sublist or each degree specification) to start parsing from
	 */
	private static final String ROOT_URL = "http://www.calendar.ubc.ca/okanagan/courses.cfm?go=code";
	private static final String DEGREES_URL = "http://www.calendar.ubc.ca/okanagan/index.cfm?tree=18,0,0,0";
	
	/**
	 * 
	 */
	private ArrayList<Tuple> courseList;

	/**
	 * Constructor: create a new instance of a crawler/parser
	 */
	public Crawler() {
		// Initialize global variables with default values
		courseList = new ArrayList<Tuple>();
	}
	
	/**
	 * Main method: from the course root page, access and parse courses out from all subject-area subpages
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		Crawler crawler = new Crawler();
		
		// Load the subject area list page (linking to each sublist of courses according to subject area)
		Document nav = Jsoup.connect(ROOT_URL).get();
		
		// This class, in the course page, is used exclusively
		// for table rows containing a course list link for a faculty
		Elements pages = nav.body().select("tr.row-highlight"); 
		
		System.out.println("Retrieving course data...");
		
		// Parse out all courses and add them to the course list
		/* */
		for (Element page : pages) {
			// Retrieve the subject code and get its contained courses:
			// in current version of course root, will be the text of
			// the row-highlight's child "td a" grandchild
			String subjectCode = page.child(0).child(0).ownText();
			crawler.parseCoursesFrom(subjectCode);
		}
		/* */
		
		// For demonstration purposes, faster to just parse one subject worth (COSC in this case):
		/*
		crawler.parseCoursesFrom(pages.get(8).child(0).child(0).ownText());
		/* */
		
		System.out.println("Course data retrieved. Populating table...");
		/* */
		
		/*
		// Initially, wrote to a text file line-by-line with courses listed as tuples 
		PrintWriter writer = new PrintWriter("sample-output.txt");
		for (Course c: crawler.courseList) {
			writer.println(c.toString());
		}
		writer.close();
		/* */
		
		// For demonstration purposes, faster to just parse one subject worth (COSC in this case):
		/* 
		crawler.parseAllFrom(pages.get(8).child(0).child(0).ownText());
		// Format courses in list as tuples and print line-by-line
		for (Course c : crawler.courseList) {
			System.out.println(c.toTuple());
		}
		/* */
		
		// Rather than printing out, add each course to database.
		String path = "jdbc:mysql://cosc304.ok.ubc.ca/db_ewardle",
				user = "ewardle", pass = "38509121";
		Tuple first = crawler.courseList.get(0); // ugly syntax...
		String tableDDL = first.getTableDDL();
		String tableName = first.getTableTitle();
		
		MySQLTableInput table = new MySQLTableInput(path, user, pass);
		boolean created = table.createTable(tableDDL);
		int count = table.populateTable(tableName, crawler.courseList.iterator());
		if (created && count == crawler.courseList.size()) {
			// Confirm after completion of method.
			System.out.println("Done populating table with course data.");
		}
	}
	
	/**
	 * Add all the courses in the specified page to the course list.
	 * @param subjectCode The specific subject area to retrieve course data from.
	 */
	private void parseCoursesFrom(String subjectCode) throws IOException {
		Document page = Jsoup.connect(ROOT_URL+"&code="+subjectCode).get();
		Elements courseTitles = page.body().select("dt"); // tag type used specifically for course titles

		for (Element titleElement : courseTitles) {
			// Create the course objects
			Course cNew = parseCourseDetails(titleElement);
			courseList.add(cNew);
		}
	}
	/**
	 * Reference a section of course sublist page HTML to parse out course values.
	 * @param titleElement The <dt> element of a particular course.
	 * @return A course object parsed out from that course title.
	 */
	private Course parseCourseDetails(Element titleElement) {
		// Each course has a predictable, well-defined structure:
		/*
		 	<dt><a name="000"></a>[SUBJ] [num] ([credits])  <b>[title]</b></dt>
			<dd>[description <br>] [<i>Prerequisite:</i> [prereqs]<br>] [<i>Corequisite:</i> [coreqs]<br>]
			</dd>
		 */
		// Rather than getting too deep into regex stuff, will just use string and jsoup methods
		// to parse out expected data into the Course object elements.
		
		// Split full dt element text into its elements:
		// course code (already known), number, credits in parentheses,
		// and each word of the course title (easier to get the bold element contents instead)
		String fullTitle = titleElement.text();
		String[] titlePieces = fullTitle.split(" ");
		
		// Get the useful components as their own variables
		String subject = titlePieces[0];
		int number = Integer.parseInt(titlePieces[1]);
		String[] creditList = titlePieces[2].replaceAll("[()]","").split("[-/]"); // remove parentheses around credits
		
		// Must parse further in case course can be taken multiple times (format: "([credits][-/][max])")
		BigDecimal credits = new BigDecimal(creditList[0]);
		BigDecimal maxCredits = credits; // Default, same as regular credits
		if (creditList.length > 1) { // Override with specific value if available
			maxCredits = new BigDecimal(creditList[1]);
		}
		
		String title = titleElement.select("b").text();
		
		// Similar for dd element, except not all fields need be present:
		Element descElement = titleElement.nextElementSibling();
		String fullDesc = descElement.html();
		String[] descPieces = fullDesc.split("<br>");
		String description = null, prereqs = null, coreqs = null;
		
		// Find the appropriate value to assign based on contents of each newline-separated field.
		for (int checkIndex = 0; checkIndex < descPieces.length; checkIndex++) {
			String piece = descPieces[checkIndex];
			if (piece.contains("Prerequisite")){
				prereqs = piece.replace("<i>Prerequisite:</i>", "").trim();
			} else if (piece.contains("Corequisite")) {
				coreqs = piece.replace("<i>Corequisite:</i>", "").trim();
			} else {
				description = piece.trim();
				if (description.equals("")) {
					description = null; // use null instead of empty string if not present
				}
			}
		}
		
		// Create the course object
		return new Course(subject, number, credits, maxCredits, title, description, prereqs, coreqs);
	}

	
	private void parseRequirementsFrom(String degree) throws IOException {
		// TODO If able to do it programmatically, do so here;
		// otherwise, will need to hard-code things in.
	}
	


}
