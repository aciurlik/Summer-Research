import java.util.ArrayList;
import java.util.HashSet;

public class ScheduleCourse implements ScheduleElement, java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Course c;
	//public boolean taken;
	HashSet<Requirement> userSpecifiedReqs; //when enemy list is not empty, this list specifies which reqs the user chose to satisfy
	private HashSet<Requirement> oldEnemyList;//this is a list of enemy requirements that this schedule course satisfied last time the schedule told this course to update.
	// used to tell if we need to ask the user for userSpecifiedReqs again.
	Schedule s;

	public ScheduleCourse(Course c, Schedule s){
		this.c=c;
		this.s=s;
		this.userSpecifiedReqs = new HashSet<Requirement>();
		this.oldEnemyList = new HashSet<Requirement>();
	}

	@Override
	public Prefix getPrefix() {
		return c.coursePrefix;
	}

	@Override
	/**
	 * Only compare based on prefix
	 */
	public boolean isDuplicate(ScheduleElement other) {
		if(! ( other instanceof ScheduleCourse )){
			return false;
		}
		return this.getPrefix().compareTo(other.getPrefix()) == 0;
	}


	@Override
	public String getDisplayString() {
		return this.c.toString();
	}

	/**
	 * uses priority order
	 * Number/subject
	 * Time
	 * Title
	 * Professor
	 * 
	 */
	@Override
	public String shortString(int preferredLength){

		ArrayList<String> data = new ArrayList<String>();
		int size = 0;


		String prefixSectionString = this.c.coursePrefix.toString(); 
		if(this.c.sectionNumber !=(null)){
			prefixSectionString = prefixSectionString + "-" + this.c.sectionNumber+ " ";
		}
		else{
			prefixSectionString += " ";
		}
		data.add(prefixSectionString);
		size += prefixSectionString.length() + 1;


		if(c.meetingTime != null && c.meetingTime.length != 0){
			String timeString =  c.meetingTime[0].clockTime();
			size += timeString.length() + 1;
			if(size < preferredLength){
				data.add(0, timeString);
			}
		}

		String title = c.name;
		if(title != null){
			size += title.length() + 1;
			if(size < preferredLength){
				data.add(title);
			}
		}

		String professor = c.professor;
		if(professor!=null){
			size += professor.length() + 1;
			if(size < preferredLength){
				data.add(professor);
			}
		}
		//Fall 2017, ACC-101-01 Accounting with ... Dr. Hansworth
		
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < data.size() - 1 ; i ++){
			result.append(data.get(i) + " ");
		}
		if(!data.isEmpty()){
			result.append(data.get(data.size() - 1));
		}
		
		return result.toString();
	}

	public String supriseString(){
		String result = "";
		result = result + c.getPrefix().getSubject() + "-,";
		result = result + c.getPrefix().getNumber() + "-,";
		result = result + c.sectionNumber + ",";
		if(c.name!=null){
			result = result + c.name+ ",";
		}
		if(c.meetingDaysCode()!=null){
			result = result + c.meetingDaysCode() + ",";
		}
		if(c.meetingTime != null){
			result= result +( c.meetingTime[0].clockTime() + ",");
		}
		if(c.professor!=null){
			result = result + c.professor + " ";
		}
		return result;

	}
	
	/**
	 * Return an unsorted list of the requirements satisfied by this course.
	 */
	@Override
	public ArrayList<Requirement> filterEnemyRequirements(ArrayList<Requirement> loaded){
		//get the reqs fulfilled while having the user resolve conflicts.
		return getRequirementsFulfilled(loaded, true);
	}

	/**
	 * Return an unsorted list of the requirements satisfied by this course.
	 * If haveUserResolveConflicts is false, all requirement-enemies will be included in the list.
	 * @return
	 */
	public ArrayList<Requirement> getRequirementsFulfilled(ArrayList<Requirement> loaded, boolean haveUserResolveConflicts) {
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Requirement r : loaded){
			if(r.isSatisfiedBy(this.getPrefix())){
				result.add(r);
			}
		}
		
		HashSet<Requirement> enemies = RequirementGraph.enemiesIn(new HashSet<Requirement>(result));
		
		
		
	    //  There are different sets of enemies being called for REL-226
	    if (((ScheduleCourse) this).c.name.contains("Roman")){
		
		}
	    
		result.removeAll(enemies);
		
		
		if(! /*sets equal*/ ((enemies.containsAll(this.oldEnemyList)) && this.oldEnemyList.containsAll(enemies)) ){
			
			if(enemies.isEmpty()){
				this.userSpecifiedReqs = new HashSet<Requirement>();
				this.oldEnemyList = enemies;
			}
			else{
				
				//the enemies changed.  
				//ask the user which requirements should now be satisfied by this course. 
				
				HashSet<Requirement> kept = enemies;
				if(haveUserResolveConflicts){
					kept = this.s.resolveConflictingRequirements(enemies, this.c); 
				}
				this.userSpecifiedReqs = kept; 
				if(haveUserResolveConflicts){
					this.oldEnemyList = enemies; 
				}
			}
			
		}


		
		result.addAll(this.userSpecifiedReqs);
		return result;


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



	/**
	 * If the user declares that this course will satisfy this requirement, 
	 * then this will save it forever (even if the requirement is no longer on
	 * the requirements list visible to the user).
	 * @param req
	 */
	public void userSpecifiedSatisfies(Requirement req){
		userSpecifiedReqs.add(req);
	}

	public int getCreditHours() {
		return c.getCreditHours();
	}

	public SemesterDate getSemester() {
		return c.getSemester();
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof ScheduleCourse){
			ScheduleCourse compare =(ScheduleCourse)other;
			if(compare.c.equals(this.c)){
				return true;
			}

		}
		return false;	
	}



}
