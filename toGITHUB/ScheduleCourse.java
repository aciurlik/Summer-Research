import java.util.ArrayList;
import java.util.HashSet;

/**
 * Blurb written: 7/26/2017
 * Last updated: 7/26/2017
 * 
 * This class represents a course that has been scheduled in some semester. It 
 * if differentiated from course (a FILE class) in order to hold
 * a reference to a schedule so that it can perform other actions, like
 * asking the user to resolve conflicting requirements. Multiple 
 * ScheduleCourses may share the same course.
 * 
 * It is in the DATA group of classes.
 *
 */
public class ScheduleCourse implements ScheduleElement, java.io.Serializable{

	private static final long serialVersionUID = 1L;
	Course c;
	HashSet<Requirement> userSpecifiedReqs; // If there are some requirements where this 
	// course can only satisfy one of them, and the user chose which ones to satisfy,
	// this field stores the user's choices.
	private HashSet<Requirement> oldEnemyList;//this is a list of enemy requirements 
	//that this schedule course satisfied last time the schedule told this course to update.
	// it is used to tell if we need to ask the user for userSpecifiedReqs again.
	Schedule s;

	public ScheduleCourse(Course c, Schedule s){
		this.c=c;
		this.s=s;
		this.userSpecifiedReqs = new HashSet<Requirement>();
		this.oldEnemyList = new HashSet<Requirement>();
	}

	
	/////////////////////////
	/////////////////////////
	////Getters and Setters
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Getters_________;
	
	@Override
	public Prefix getPrefix() {
		return c.coursePrefix;
	}
	
	public SemesterDate getSemester() {
		return c.getSemesterDate();
	}
	
	public int getCreditHours() {
		return c.getCreditHours();
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
	 * 
	 * uses priority order
	 * Number/subject
	 * Time
	 * Title
	 * Professor
	 * 
	 * to decide what information to include. Only includes the next piece of information
	 * if it can fit without going over preferredLength characters.
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
	
	/**
	 * Return an unsorted list of the requirements satisfied by this course.
	 */
	@Override
	public ArrayList<Requirement> filterEnemyRequirements(ArrayList<Requirement> loaded){
		//get the reqs fulfilled while having the user resolve conflicts.
		return filterEnemyRequirements(loaded, true);
	}

	
	/**
	 * Return an unsorted list of the requirements satisfied by this course.
	 * If haveUserResolveConflicts is false, all requirement-enemies will be included in the list.
	 * @return
	 */
	public ArrayList<Requirement> filterEnemyRequirements(ArrayList<Requirement> loaded, boolean haveUserResolveConflicts) {
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Requirement r : loaded){
			if(r.isSatisfiedBy(this.getPrefix())){
				result.add(r);
			}
		}
		if(!haveUserResolveConflicts){
			return result;
		}
		else{
			HashSet<Requirement> enemies = RequirementGraph.enemiesIn(new HashSet<Requirement>(result));
			//  TODO There are different sets of enemies being called for REL-226 (Is this still happening after 7/26?)

			result.removeAll(enemies);
			if(! /*oldEnemyList == enemies*/ ((enemies.containsAll(this.oldEnemyList)) 
					&& this.oldEnemyList.containsAll(enemies)) ){
				//the enemies changed. Ask the user which requirements should now 
				// be satisfied by this course, and store the result. 
				if(enemies.isEmpty()){
					this.userSpecifiedReqs = new HashSet<Requirement>();
				}
				else{
					this.userSpecifiedReqs = this.s.resolveConflictingRequirements(enemies, this.c); 
				}
				this.oldEnemyList = enemies;
			}
			result.addAll(this.userSpecifiedReqs);
			return result;
		}
	}
	
	
	

	/**
	 * Get each of the pieces that surprise me slowly shows to the user
	 * @return
	 */
	public String[] supriseMePieces(){
		ArrayList<String> result = new ArrayList<String>();
		result.add(c.getPrefix().getSubject() + "-");
		result.add(c.getPrefix().getNumber() + "-");
		result.add(c.sectionNumber);
		if(c.name!=null){
			result.add( c.name);
		}
		if(c.meetingDaysCode()!=null){
			result.add(c.meetingDaysCode());
		}
		if(c.meetingTime != null){
			result.add(c.meetingTime[0].clockTime());
		}
		if(c.professor!=null){
			result.add(c.professor);
		}
		return result.toArray(new String[result.size()]);
	}
	
	/////////////////////////
	/////////////////////////
	////Time methods
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___TimeMethods_________;


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

	public static ArrayList<ScheduleCourse> toScheduleCourses(Iterable<Course> input, Schedule sch){
		ArrayList<ScheduleCourse> result = new ArrayList<ScheduleCourse>();
		for(Course c : input){
			result.add(new ScheduleCourse(c, sch));
		}
		return result;
	}
	
	//TODO Test case - what if we call filterEnemyRequiremements and there are 2 requirements that are enemies, but that don't
	// actually use this course? Will a popup happen for the user?


}
