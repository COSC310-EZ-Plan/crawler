import java.util.ArrayList;
import java.util.Arrays;

/**
 * Constants (hard-coded), used to make SQL query conditions easier to create.
 * Composed as a mapping from general to specific subject area.
 * (usable to calculate a sublist of courses that fall 
 * under the general requirement description)
 */
public class Faculties {

	public static final Subject[] 

	MODERN_LANGUAGES = {
		new Subject("FREN", "French"),
		new Subject("GERM", "German"),
		new Subject("JPST", "Japanese Studies"),
		new Subject("SPAN", "Spanish"),
	},
	
	LANGUAGES = concat(new Subject[] {
		new Subject("ENGL", "English"), 
		new Subject("HEBR", "Hebrew"),
		new Subject("GREK", "Greek"),
		new Subject("LATN", "Latin"),
	}, MODERN_LANGUAGES),
	
	HUMANITIES = concat(new Subject[] {
		new Subject("ARTH", "Art History and Visual Culture"),
		new Subject("CCS", "Creative and Critical Studies"),
		new Subject("CRWR", "Creative Writing"),
		new Subject("CULT", "Cultural Studies"),
		new Subject("FILM", "Film"),
		new Subject("HIST", "History"),
		new Subject("MUSC", "Music"),
		new Subject("PHIL", "Philosophy"),
		new Subject("THTR", "Theatre"),
		new Subject("VISA", "Visual Arts"),
	}, LANGUAGES),
	
	SOCIAL_SCIENCES = {
		new Subject("ANTH", "Anthropology"),	
		new Subject("ECON", "Economics"),	
		new Subject("GWST", "Gender and Women's Studies"),	
		new Subject("GEOG", "Geography"),	
		new Subject("INDG", "Indigenous Studies"),	
		new Subject("POLI", "Political Science"),	
		new Subject("PSYO", "Psychology"),	
		new Subject("SOCI", "Sociology")	
	}, 
	
	ARTS_SUBJECTS =
		concat(SOCIAL_SCIENCES, HUMANITIES),
		
	SCI_SUBJECTS = {
		new Subject("ASTR", "Astronomy"),
		new Subject("BIOC", "Biochemistry"),
		new Subject("BIOL", "Biology"),
		new Subject("CHEM", "Chemistry"),
		new Subject("COSC", "Computer Science"),
		new Subject("DATA", "Data Science"),
		new Subject("EESC", "Earth and Environmental Sciences"),
		new Subject("GEOG", "Geography"),
		new Subject("MATH", "Mathematics"),
		new Subject("PHYS", "Physics"),
		new Subject("STAT", "Statistics")
	};
	
	/**
	 * Helper method to concatenate two Subject arrays (without having to import more third-party tools).
	 * Done in a single statement (so as to be usable for initialization, i.e. of constants).
	 * @param a, b The arrays to concatenate (a, then b).
	 * @return A new array concatenating those arrays.
	 */
	private static Subject[] concat(Subject[] a, Subject[] b) {
		ArrayList<Subject> listOut = new ArrayList<Subject>(Arrays.asList(a));
		listOut.addAll(Arrays.asList(b));
		return listOut.toArray(new Subject[listOut.size()]);
	}
	

}
