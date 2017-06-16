import java.util.ArrayList;
import java.util.HashSet;

public class ScheduleCourse implements ScheduleElement{
	Course c;
	public boolean taken;
	ArrayList<Requirement> userSpecifiedReqs = null;
	Schedule s;

	public ScheduleCourse(Course c, Schedule s){
		this.c=c;
		this.s=s;
	}

	@Override
	public Prefix getPrefix() {
			return c.coursePrefix;
		}

	@Override
	public boolean isDuplicate(ScheduleElement other) {
		if(! ( other instanceof Course )){
			return false;
		}
		return this.c.coursePrefix.compareTo(((Course)other).coursePrefix) == 0;
	}


	@Override
	public String getDisplayString() {
		return this.c.toString();
	}

	@Override
	public String shortString(){
		String result = new String();
		result = result + this.c.semester.getSeason(this.c.semester.sNumber)+ " "+ this.c.semester.year + " ";
		result = result + this.c.coursePrefix.toString() + " "; 
		result = result + this.c.sectionNumber+ " ";

		return result;

	}




	/**
	 * Return an unsorted list of the requirements satisfied by this course.
	 * @return
	 */
	@Override
	public ArrayList<Requirement> getRequirementsFulfilled(HashSet<Requirement> loaded) {
		HashSet<Requirement> result = new HashSet<Requirement>();
		for(Requirement r : loaded){
			if(r.isSatisfiedBy(this.c.coursePrefix)){
				result.add(r);
			}
		}
		HashSet<Requirement> enemies = RequirementGraph.enemiesIn(new HashSet<Requirement>(result));
		if(! /*sets equal*/ ((enemies.containsAll(this.c.oldEnemyList)) && this.c.oldEnemyList.containsAll(enemies)) ){
			//the enemies changed. TODO
			//ask the user which requirements should now be satisfied by this course.
			System.out.println("The course " + this.getDisplayString() + " satisfies clashing requirements,\n"
					+ enemies.toString() + "\n"
					+ " Which one should get the credit hours?");
			HashSet<Requirement> kept = this.s.resolveConflictingRequirements(enemies, this.c);
			this.c.userSpecifiedReqs = kept;
			this.c.oldEnemyList = enemies;
		}
		result.removeAll(this.c.oldEnemyList);
		for(Requirement specified : this.c.userSpecifiedReqs){
			result.add(specified);
		}
		return new ArrayList<Requirement>(result);
	}

	public Interval<Time> examTime() {
		return c.examTime();
	}

	public Interval<Time> labTime() {
		return c.labTime();
	}

	public Intervals<Time> meetingTimes() {
		return c.meetingTimes();
	}
	
	
	//////////////
	/// Overlaps method and helper
	//////////////
	public boolean overlaps(ScheduleElement other){
		if(other instanceof ScheduleCourse){
			return this.allTakenTimes().overlaps(((ScheduleCourse)other).allTakenTimes());
		}
		else{
			return false;
		}
	}

	public Intervals<Time> allTakenTimes(){
		Intervals<Time> result = new Intervals<Time>();
		Interval<Time> labTime = labTime();
		if(labTime != null){
			result.addInterval(labTime);
		}
		Interval<Time> examTime = examTime();
		if(examTime != null){
			result.addInterval(examTime);
		}
		//note - some courses may have meeting times with all values unused.
		// For example, music courses often don't specify times.
		Intervals<Time> meetingDays = meetingTimes();
		if(meetingDays != null){
			for(Interval<Time> i : meetingDays.intervals){
				result.addInterval(i);
			}
		}
		return result;
	}
	
	
	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	

	/**
	 * If the user declares that this course will satisfy this requirement, 
	 * then this will save it forever (even if the requirement is no longer on
	 * the requirements list visible to the user).
	 * @param req
	 */
	public void userSpecifiedSatisfies(Requirement req){
		userSpecifiedReqs.add(req);
	}

}
