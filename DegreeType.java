import java.util.List;
import java.util.ArrayList;

/**
 * EZ-Plan
 * Contains the requirements of a single degree program from the UBCO Academic Calendar.
 * (Serves as a set of constraints for degree requirements.
 * Not to be confused with the courses a student has taken in their degree program.)
 * @author Eliana Wardle (38509121)
 *
 */
public class DegreeType implements Tuple {

	/**
	 * Represents the components of a degree type.
	 * For example: "Bachelor of Science, major in Computer Science" would
	 * have degreeLevel = B, subject = Science, major = Computer Science.
	 * Note, EZ-Plan is designed primarily for undergraduate students
	 * (which also have easier-to-distinguish requirements than higher levels).
	 */
	public static enum DegreeLevel {
			B("Bachelor"), M("Master"), PhD("Doctor");
			
			DegreeLevel(String text) {
				title = text;
			}
			String title;
		};
	private DegreeLevel level;
	private String faculty;
	private String major;
	private Integer minCredits;
	
	/**
	 * Set of courses needed to complete this degree program (ordered as on its program requirements page).
	 */
	private List<CourseRequirement> courseReqs;
	
	/**
	 * Basic constructor; when a degree program type is specified, specific requirements can be added later.
	 */
	public DegreeType(DegreeLevel lev, String subj, String maj, Integer mincred) {
		level = lev;
		faculty = subj;
		major = maj;
		minCredits = mincred;
		courseReqs = new ArrayList<CourseRequirement>();
	}
	// If desired, may create a constructor given an Iterator over a list of courses, but
	// in general it's possible to add courses after construction one at a time.
	
	/**
	 * Dummy type for degree.
	 */
	public DegreeType() {
	}

	/**
	 * Add a requirement defined in this degree.
	 * @param creq
	 */
	public void add(CourseRequirement creq) {
		courseReqs.add(creq);
	}
	
	/**
	 * Add a set (or subset) of requirements defined in this degree.
	 * @param creqs
	 */
	public void add(List<CourseRequirement> creqs) {
		courseReqs.addAll(creqs);
	}

	/**
	 * Return the requirements associated with this degree.
	 * @return
	 */
	public List<CourseRequirement> getAll() {
		return courseReqs;
	}
	
	@Override
	public String[] getValues() {
		// Note, CourseRequirement tuples are in their own table and
		// will be referenced by this combination of attributes,
		// so the DDL is fairly simple.
		return new String[] {
				this.toString(),
				minCredits.toString()
				};
	}

	@Override
	public String[] getColumns() {
		return new String[] {
				"degree", 
				"mincredits"
				};
	}

	@Override
	public String[] getKeyHeadings() {
		return new String[] {getColumns()[0]};
	}

	@Override
	public String getTableDDL() {
		String ddl =
				"CREATE TABLE IF NOT EXISTS DegreeType ( "
				+ "degree VARCHAR(100), "
				+ "mincredits INT, "
				+ "PRIMARY KEY (degree)"
				+ " )";
		return ddl;
	}

	@Override
	public String getTableTitle() {
		return "DegreeType";
	}
	
	@Override
	public String toString() {
		return level.title+" of "+faculty+", Major in "+major;
	}
}
