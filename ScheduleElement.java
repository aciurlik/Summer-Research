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
	 * Should include all the information about this element
	 * in a user-friendly way. Can use as many characters
	 * as necessary. Used when the user wants to examine an 
	 * element in full. 
	 */
	public String getDisplayString();
	
	
	/**
	 * Should contain only the vital information to identify this element, 
	 * while attempting to use less than preferredLength characters.
	 * However, this method should not truncate 
	 * if it goes over perferredLength characters.
	 * Acts as a default name if no name is provided.
	 * @return String that has less information than display string
	 */
	public String shortString(int preferredLength);
	
	
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
