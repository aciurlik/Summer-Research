import java.util.ArrayList;

/**
 * Blurb written: 7/26/2017
 * Last updated: 7/26/2017
 * 
 * This class represents a prerequisite for one course.
 * 
 * The actual prerequisite is stored as a requirement, and the course
 * that needs it is referenced by the prefix p.
 * 
 * The Schedule class should handle deciding which ScheduleElements count as being
 * taken 'before' p. Once this list is collected, it can call the method 
 * updateOn(takenElements) to check whether the prereq has been satisfied
 * (to check if the prereq is satisfied, use getRequirement().storedIsComplete(). )
 * 
 * Prereq is part of the DATA group of classes.
 *
 */
public class Prereq implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private Requirement r;
	private Prefix p;
	
	public Prereq(Requirement r, Prefix p){
		this.r = r;
		this.p = p;
	}
	
	public Requirement getRequirement(){
		return this.r;
	}
	
	public Prefix getPrefix(){
		return this.p;
	}
	
	public void updateOn(ArrayList<ScheduleElement> taken){
		//remove any elements that have prefix p from taken.
		// this keeps you from being satisfied by yourself, like
		// a prereq for MTH>200 being satisfied by the MTH-340 course that
		// it's a prereq for.
		ArrayList<ScheduleElement> toRemove = new ArrayList<ScheduleElement>();
		for(ScheduleElement e : taken){
			if(e.getPrefix() != null && e.getPrefix().equals(p)){
				toRemove.add(e);
			}
		}
		taken.removeAll(toRemove);
		
		//Update on the new array list.
		this.r.updateAllStoredValues(taken);
	}
	
}
