
import java.util.ArrayList;

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
	 * 
	 * @return An ArrayList of the requirements this ScheduleElement fulfills,
	 * if element has no requirements it fulfills (if course), an empty ArrayList is returned
	 */
	public ArrayList<Requirement> getRequirementsFulfilled();


}
