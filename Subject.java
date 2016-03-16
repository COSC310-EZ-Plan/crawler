
/**
 * Helper/wrapper class that pairs a subject code with its full title.
 * (This pairing is necessary to deal with the inconsistent phrasing of 
 * program requirements on the course website.)
 */
public class Subject {
	public String code;
	public String title;
	
	public Subject(String code, String title) {
		this.code = code;
		this.title = title;
	}

}