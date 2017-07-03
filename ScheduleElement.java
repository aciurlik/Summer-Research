import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author drivers, aciurlik
 * An interface that describes the operations of any element of a schedule
 * This includes Requirement, course, prefix, 
 *
 */


public interface ScheduleElement {

	/**
	 * @return The prefix of the given ScheduleElment, 
	 * and null if ScheduleElement has no prefix. 
	 */
	public Prefix getPrefix();

	/**
	 * 
	 * @param other, the ScheduleElement you want to compare
	 * @return true if the two SchduleElements are duplicates (the "same" as
	 * each other, false if the elements are different 
	 */
	public boolean isDuplicate(ScheduleElement other);

	/**
	 * 
	 * @return String of the ScheduleElement 
	 * that should be displayed in GUI
	 */
	public String getDisplayString();
	
	
	/**
	 * If course return Semester Date, Prefix and Section number, requirement returns and empty String
	 * @return String that has less information than display string
	 */
	public String shortString();
	
	
	/**
	 * 
	 * @return An ArrayList of the requirements this ScheduleElement fulfills,
	 * if element has no requirements it fulfills, an empty ArrayList is returned.
	 * 
	 * If this element doesn't satisfy some requirement due to an
	 * issue with requirement enemies, then issue requirements should not be 
	 * in the returned list (if enemy issues are overridden, then the requirements
	 * that were allowed to use this element may be in the list).
	 */

	public ArrayList<Requirement> getRequirementsFulfilled(ArrayList<Requirement> loaded);

	
}
