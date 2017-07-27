import java.util.ArrayList;
import java.util.HashSet;

/**
 * Blurb written: 7/26/2017
 * Last updated: 7/26/2017
 * 
 * An interface that describes the operations of any anything that can be put
 * into a semester as a planned course.
 * 
 * This currently includes Requirement, ScheduleCourse, and Prefix 
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
	 * @return the number of credit hours gained by scheduling this
	 * element
	 */
	public int getCreditHours();

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
	 * 
	 * Acts as a default name if no name is provided.
	 * 
	 * @return String that has less information than display string
	 */
	public String shortString(int preferredLength);
	
	
	/**
	 * 
	 * 
 	 * @return If there is any requirement in loaded that isn't allowed to use this element,
 	 * because of requirement enemies, remove it.
 	 * 
 	 * You can also remove any requirements that are guaranteed to not be satisfied by this
 	 * element, but that is only if doing so takes very little computation time.
	 */

	public ArrayList<Requirement> filterEnemyRequirements(ArrayList<Requirement> loaded);

	
}
