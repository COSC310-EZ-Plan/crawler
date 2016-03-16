
/**
 * Represents an object that can be added to the database (i.e. fields are
 * labeled and given a value corresponding to the actual database structure).
 * @author Eliana
 *
 */
public interface Tuple {
	
	/**
	 * Values for the columns in this tuple.
	 * @return
	 */
	public String[] getValues();
	
	/**
	 * All field names.
	 * @return
	 */
	public String[] getColumns();
	/**
	 * Field name(s) of the primary key.
	 * @return
	 */
	public String[] getKeyHeadings();
	/**
	 * Full DDL to create the table.
	 * @return
	 */
	public String getTableDDL();
	/**
	 * Name of table in the database.
	 * @return
	 */
	public String getTableTitle();
}
