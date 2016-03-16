import java.util.Arrays;
import java.util.ArrayList;
import java.math.BigDecimal;


/**
 * EZ-Plan
 * Wrapper class for course requirements. 
 * It is necessary, since degrees often specify things like 
 * "upper-level Arts course" in their requirements, to have
 * a way to list "dummies" for actual courses.
 * 
 * These generally specify one or more year levels (which 
 * range from "first-year" to "upper-year") and one or more 
 * subject areas (which range from "[Faculty] elective[s]" 
 * to "[N] of [list of courses]". (Listed, specific courses 
 * provide the same information in a more concise format.)
 * 
 * 
 * Notes on how REGEXP in MySQL is used in the "query" section:
 * http://www.tutorialspoint.com/mysql/mysql-regexps.htm
 * 
 * @author Eliana Wardle
 *
 */
public class CourseRequirement implements Tuple {
	
	/**
	 * General components of a requirement.
	 * We can consider a few cases:
	 * 	- certain number of credits in a certain general faculty or area
	 * 	- certain number of credits in a particular course code or corresponding subject title
	 * 	- certain number or combinations in a list of specific named courses
	 * Most of these will have to be manually parsed, but the "cond" section allows the full list of
	 * specified options (within a certain requirement) to be found by SQL query.
	 */

	private DegreeType degreeType;
	private String cond, description;
	private Integer count;
	private Integer credits; //presumably, requirements ask for whole numbers of credits
	
	/**
	 * Constructor (must give all values in order to define one).
	 */
	public CourseRequirement(DegreeType degreeType, String cond, String description, Integer count, Integer credits) {
		this.degreeType = degreeType;
		this.cond = cond;
		this.description = description;
		this.count = count;
		this.credits = credits;
	}
	
	/**
	 * Constructor for a "dummy requirement".
	 * @param degreeType
	 */
	public CourseRequirement(DegreeType degreeType) {
		this(degreeType, null, null, null, null);
	}
	
	/**
	 * Helper method to make it easier to define other requirements in the degree.
	 * @param cond
	 * @param description
	 * @return A requirement constructed from the other fields, in this degree.
	 */
	public CourseRequirement define(String cond, String description, Integer count, Integer credits) {
		return new CourseRequirement(this.degreeType, cond, description, count, credits);
	}
	
	// Interface implemented methods
	
	@Override
	public String[] getValues() {
		return new String[] {
				degreeType.toString(), 
				cond, 
				description,
				((count == null)? null : count.toString()),
				((credits == null)? null : credits.toString())
				};
	}

	@Override
	public String[] getColumns() {
		return new String[] {
				"degree",
				"cond",
				"description",
				"count",
				"credits"
		};
	}

	@Override
	public String[] getKeyHeadings() {
		String[] headings = getColumns();
		return new String[] {headings[0], headings[1]};
	}

	@Override
	public String getTableDDL() {
		DegreeType d = new DegreeType();
		String ddl = "CREATE TABLE IF NOT EXISTS "+ getTableTitle() +" ( "
				+ "degree VARCHAR(100), "
				+ "cond VARCHAR(255), "
				+ "description VARCHAR(500), "
				+ "count INT, "
				+ "credits DECIMAL(3,1), "
				+ "PRIMARY KEY(degree, cond), "
				+ "FOREIGN KEY (degree) REFERENCES "
				+ d.getTableTitle()+"("+d.getKeyHeadings()[0]
				+ ") ON DELETE CASCADE ON UPDATE CASCADE ) ";
		return ddl;
	}

	@Override
	public String getTableTitle() {
		return "CourseRequirement";
	}
}
