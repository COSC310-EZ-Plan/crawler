
/**
 * Represents an object that can be added to the database (i.e. fields are
 * labeled and given a value corresponding to the actual database structure).
 * @author Eliana
 *
 */
public interface Tuple {
	
	public String[] getValues();
	public String[] getColumns();
	public String getTableDDL();
	public String getTableTitle();
}
