import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * EZ-Plan
 * Instantiates the necessary 
 * @author Eliana
 *
 */
public class CrawlerExecute {

	public static void main(String[] args) throws IOException {

		// Add courses to database
		//populateCourses(); // former main method defined here
		
		// Add course requirements to database
		populateRequirements();

	}
	
	/**
	 * Finds, and populates the database with, all the course fields on the UBCO website.
	 * (Part of the former main method of Crawler.)
	 * @throws IOException
	 */
	private static void populateCourses() throws IOException {
		System.out.println("Retrieving course data...");
		Crawler crawler = new Crawler();
		ArrayList<Tuple> courseList = crawler.fullCrawl();
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
		String path = "jdbc:mysql://cosc304.ok.ubc.ca/db_ioyedele",
				user = "ioyedele", pass = "36547123";
		Tuple first = courseList.get(0); // ugly syntax...
		String tableDDL = first.getTableDDL();
		String tableName = first.getTableTitle();
		
		MySQLTableInput table = new MySQLTableInput(path, user, pass);
		boolean created = table.createTable(tableDDL);
		int count = table.populateTable(tableName, courseList.iterator());
		if (created && count >= courseList.size()) {
			// Confirm after completion of method.
			System.out.println("Done populating table with course data. (count="+count+")");
		} 
		else {
			System.out.println("Could not populate entire table. (count="+count+"/"+courseList.size()+")");
		}
	}

	/**
	 * Finds, and populates the database with, certain degree types and their requirements.
	 * (Currently, only Computer Science, as we ended up having to do it manually.)
	 * @throws IOException
	 */
	private static void populateRequirements() throws IOException {
		System.out.println("Setting up degree and requirement data...");
		
		DegreeType bscCosc = new DegreeType(
				DegreeType.DegreeLevel.B, "Science", "Computer Science", 120);
		ArrayList<Tuple> bscWrapper = new ArrayList<Tuple>();
		bscWrapper.add(bscCosc);
		
		Course co = new Course();
		CourseRequirement c = new CourseRequirement(bscCosc); // used to help build other ones since we're doing it manually anyways
		String key = co.getKeyHeadings()[0];
		
		String artsElRegex = key+" RLIKE \'";
		int artsLength = Faculties.ARTS_SUBJECTS.length;
		for (int i=0; i < artsLength-1; i++) {
			artsElRegex += "^"+Faculties.ARTS_SUBJECTS[i].code + " |";
		}
		artsElRegex += Faculties.ARTS_SUBJECTS[artsLength-1]+"\'";
		
		List<Tuple> 
		y1reqs = Arrays.asList(new CourseRequirement[] {
				c.define(key+" RLIKE \'COSC (111|123)\'", "COSC 111 or 123", 1, 3),
				c.define(key+" = \'COSC 121\'", "COSC 121", 1, 3),
				c.define(key+" RLIKE \'ENGL (112|114)\'", "Two of ENGL 112 or 114, 113, 150, 151, 153", 1, 3),
				c.define(key+" RLIKE \'ENGL (113|150|151|153)\'", "Two of ENGL 112 or 114, 113, 150, 151, 153", 1, 3),
				c.define(key+" = \'MATH 100\'", "MATH 100, 101", 1, 3),
				c.define(key+" = \'MATH 101\'", "MATH 100, 101", 1, 3),
				c.define(key+" RLIKE \'PHYS (111|112)\'", "PHYS 111 or 112; and PHYS 102 or 122", 1, 3),
				c.define(key+" RLIKE \'PHYS (102|122)\'", "PHYS 111 or 112; and PHYS 102 or 122", 1, 3),
				// mixing "general electives" into upper-year specified ones
		}),
		y2reqs = Arrays.asList(new CourseRequirement[] {
				c.define(key+" = \'COSC 211\'", "COSC 211", 1, 3),
				c.define(key+" = \'COSC 221\'", "COSC 221", 1, 3),
				c.define(key+" = \'COSC 222\'", "COSC 222", 1, 3),
				c.define(key+" = \'MATH 220\'", "MATH 220", 1, 3),
				c.define(key+" = \'MATH 221\'", "MATH 221", 1, 3),
				c.define(key+" = \'STAT 230\'", "STAT 230", 1, 3),
				c.define(key+" RLIKE \'CHEM (111|121)\'", "CHEM 111 or 112; and CHEM 102 or 122", 1, 3),
				c.define(key+" RLIKE \'CHEM (113|123)\'", "CHEM 111 or 112; and CHEM 102 or 122", 1, 3),
				c.define(artsElRegex, "Arts electives", null, 6)
		}),
		y34reqs = Arrays.asList(new CourseRequirement[] {
				c.define(key+" = \'COSC 304\'", "COSC 304", 1, 3),
				c.define(key+" = \'COSC 320\'", "COSC 320", 1, 3),
				c.define(key+" = \'COSC 310\'", "COSC 310", 1, 3),
				c.define(key+" = \'COSC 341\'", "COSC 341", 1, 3),
				c.define(key+" = \'COSC 499\'", "COSC 499", 1, 6),
				c.define(key+" = \'PHIL 331\'", "PHIL 331", 1, 3),
				c.define(key+" RLIKE \'^COSC (3|4)\'", "Upper-level Computer Science electives", null, 15),
				c.define(key+" RLIKE \'^[a-zA-Z]+ (3|4)\'", "Upper-level electives", null, 6),
				c.define(key+" RLIKE \'[\\s\\S]*\'", "Electives", null, 24),
		}),
		reqs = new ArrayList<Tuple>();
		reqs.addAll(y1reqs); 
		reqs.addAll(y2reqs); 
		reqs.addAll(y34reqs);
		
		System.out.println("Requirements data specified. Populating table...");
		
		String path = "jdbc:mysql://cosc304.ok.ubc.ca/db_ioyedele",
				user = "ioyedele", pass = "36547123";
		
		// Create if necessary, and populate degrees table with this degree
		String tableDDL = bscCosc.getTableDDL();
		String tableName = bscCosc.getTableTitle();
		MySQLTableInput table = new MySQLTableInput(path, user, pass);
		boolean created = table.createTable(tableDDL);
		int count = table.populateTable(tableName, bscWrapper.iterator());
		if (created && count >= 1) {
			// Confirm after completion of method.
			System.out.println("Done populating table with degree type data. (count="+count+")");
		} 
		else {
			System.out.println("Could not populate entire table of degree type data. (count="+count+"/"+reqs.size()+")");
		}
		
		// Create if necessary, and populate requirements table for this degree
		Tuple first = reqs.get(0); // ugly syntax...
		tableDDL = first.getTableDDL();
		tableName = first.getTableTitle();
		created = table.createTable(tableDDL);
		count = table.populateTable(tableName, reqs.iterator());
		if (created && count >= reqs.size()) {
			// Confirm after completion of method.
			System.out.println("Done populating table with requirement data. (count="+count+")");
		} 
		else {
			System.out.println("Could not populate entire table of requirement data. (count="+count+"/"+reqs.size()+")");
		}
		// TODO: Since creating and adding to database is pretty consistent,
		// put a method for it in MySQLTableInput instead, when time is available.
		
	}
}
