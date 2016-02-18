import java.math.BigDecimal;

/**
 * EZ-Plan
 * Contains the information of a single course from the UBCO course website.
 * (Intended to be easy to isolate values and add to database.)
 * @author Eliana Wardle (38509121)
 *
 */
public class Course {
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
	 * Getter methods
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
	 * Tuple representation of the course.
	 * @return entire course as tab-separated "tuple"
	 */
	String toTuple() {
		String ret =
				getCourseCode()+"\t"
				+ title+"\t"
				+ description+"\t"
				+ prereqs+"\t"+coreqs;
		return ret;
	}
	
	/**
	 * String representation of the course.
	 * @return Course info similar to that read into the crawler
	 */
	// TODO
}
