import java.math.BigDecimal;

/**
 * EZ-Plan
 * Contains the information of a single course from the UBCO course website.
 * (Intended to be easy to isolate values and add to database.)
 * @author Eliana Wardle (38509121)
 *
 */
public class Course implements Tuple {
	/**
	 * Relational Attributes
	 * (Private visibility with getter methods only, since a course
	 * object should be created all at once and not modified.)
	 */
	
	// Subject (aka faculty) and number make up the course code
	private String subject;
	private int number;
	
	// Credits offered each time (and max if able to take multiple times)
	// BigDecimal type since some courses offer partial (+0.5) credits and we want perfect precision for those!
	private BigDecimal credits, maxCredits;
	
	// Descriptive attributes of the course

	private String title, description;
	
	// Pre-reqs and co-reqs; plain text for database storage,
	// but to be parsed on server end during schedule building.
	// NOTE: does not include "credit will not be granted
	// for both..." restrictions (which are in description element).
	private String prereqs, coreqs; 
	
	/**
	 * Course constructor; all fields must be specified at the creation of the course
	 */
	public Course(String subj, int num, BigDecimal cred, BigDecimal maxCred, String title, String desc, String pre, String co) {
		this.subject = subj;
		this.number = num;
		this.credits = cred;
		this.maxCredits = maxCred;
		this.title = title;
		this.description = desc;
		this.prereqs = pre;
		this.coreqs = co;
	}
	/**
	 * Blank course constructor. Only should be used for dummy entries.
	 */
	public Course() {
		this(null, -1, null, null, null, null, null, null);
	}
	
	/**
	 * Getter methods (mostly unnecessary, actually)
	 */
	
	String getSubject() {
		return subject;
	}
	int getNumber() {
		return number;
	}
	String getCourseCode() { // mix of subject and number, eg. "COSC 310"
		return (subject+" "+number);
	}
	String getTitle() {
		return title;
	}
	String getDescription() {
		return description;
	}
	String getPrereqs() {
		return prereqs;
	}
	String getCoreqs() {
		return coreqs;
	}

	/**
	 * String representation of the course.
	 * @return entire course as tab-separated text
	 */
	@Override
	public String toString() {
		String ret =
				getCourseCode()+"\t"
				+ title+"\t"
				+ description+"\t"
				+ prereqs+"\t"+coreqs;
		return ret;
	}
	
	/**
	 * Gets the column names of the course tuple.
	 */
	@Override
	public String[] getColumns() {
		return new String[] {
				"cname", 
				"title", 
				"credits", 
				"maxcredits", 
				"description", // cannot use "desc" as it is a MySQL reserved word
				"prereq", 
				"coreq"
				};
	}
	
	/**
	 * Gets the values for the course tuple.
	 */
	@Override
	public String[] getValues() {
		// TODO Pre-reqs and co-reqs may need to be updated later when
		// we come up with logic to parse it properly. Faculty probably
		// not necessary as it wouldn't be referenced consistently anyways,
		// so a mapping (within crawler project or in PHP) would be more useful instead.
		return new String[] {
				getCourseCode(), 
				title, 
				credits.toString(),
				maxCredits.toString(),
				description, 
				prereqs, 
				coreqs
				};
	}

	/**
	 * Gets the DDL for creating this kind of table.
	 */
	@Override
	public String getTableDDL() {
		String ddl = "CREATE TABLE Course ( "
				+ "cname VARCHAR(10), "
				+ "title VARCHAR(100), "
				+ "credits DECIMAL(3,1), "
				+ "maxcredits DECIMAL(3,1), "
				+ "description VARCHAR(1000), "
				+ "prereq VARCHAR(500), "
				+ "coreq VARCHAR(500), "
				+ "PRIMARY KEY(cname) "
				+ "); ";
		return ddl;
	}

	/**
	 * Gets the table title, in case different than class name.
	 */
	@Override
	public String getTableTitle() {
		return "Course";
	}

	@Override
	public String[] getKeyHeadings() {
		// TODO Auto-generated method stub
		return new String[] {"cname"};
	}



}
