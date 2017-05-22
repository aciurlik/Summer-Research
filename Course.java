import java.util.ArrayList;
import java.util.HashSet;

public class Course implements ScheduleElement{

	
	protected int creditHours;
	protected Prefix coursePrefix;
	protected int sectionNumber;
	protected SemesterDate semester;
	protected int[] meetingDays; //specified by the constants in the Time class, 
	  //  as in Time.SUNDAY.
	protected Interval<Time> meetingTime;
	Interval<Time> labTime; //assumed to repeat weakly until examTime
	Interval<Time> examTime; 
	String professor;
	
	HashSet<Requirement> reqsSatisfied;
	HashSet<Requirement> userSpecifiedReqs;
	
	public static final String[] dayCodes = {"U", "M", "T", "W", "H", "F", "S"};
	

	/**
	 * Creates a course object
	 * @param c
	 * @param p
	 */

	public Course(Prefix prefix, SemesterDate semester, String professor, int[] meetingDays, 
			 int creditHours, int sectionNumber){
		this.creditHours=creditHours;
		this.coursePrefix=prefix;
		this.semester = semester;
		this.professor = professor;
		this.meetingDays = meetingDays;
		this.sectionNumber = sectionNumber;
	}

	
	
	public Prefix getPrefix(){
		return coursePrefix;
	}

	public int getCreditHours(){
		return creditHours;
	}
	
	public SemesterDate getSemester() {
		return this.semester;
	}
	
	
	public String meetingDaysCode(){
		String result = "";
		for (int day : meetingDays){
			result += dayCodes[day];
		}
		return result;
	}
	
	///////////////////
	///////////////////
	////Methods used to for setting Time objects
	///////////////////
	///////////////////
	public void setMeetingTime(int hours, boolean AM, int minutes, int durationMinutes){
		Time startTime = new Time(hours, AM, minutes, 0);
		startTime.setYear(semester.getYear());
		startTime.setMonth(this.semester.getStartMonth());
		this.meetingTime = new Interval<Time> (startTime, startTime.addMinutes(durationMinutes));
	}
	public void setExamTime(Time examStartTime, int durationInMinutes){
		this.examTime = new Interval<Time>(
				examStartTime, 
				examStartTime.addMinutes(durationInMinutes));
	}
	public void setLabTime(int dayOfWeek, int hour, boolean AM, int minutes, int durationMinutes){
		Time startTime = new Time( hour, AM, minutes, 0);
		startTime.setYear(this.semester.getYear());
		startTime.setMonth(this.semester.getStartMonth());
		startTime.advanceToNext(dayOfWeek);
		this.labTime = new Interval<Time>(startTime, startTime.addMinutes(durationMinutes));
	}
	
	//////////////
	/// Overlaps method and helper
	//////////////
	public Intervals<Time> allTakenTimes(){
		Intervals<Time> result = new Intervals<Time>();
		if(labTime != null){
			result.addInterval(labTime);
		}
		if(examTime != null){
			result.addInterval(examTime);
		}
		if(meetingDays != null && meetingTime != null){
			for (int day : meetingDays){
				Time s = new Time(meetingTime.start);
				Time f = new Time(meetingTime.end);
				s.advanceToNext(day);
				f.advanceToNext(day);
				result.addInterval(new Interval<Time>(s,f));
			}
		}
		return result;
		
		
	}
	public boolean overlaps(ScheduleElement other){
		if(other instanceof Course){
			return this.allTakenTimes().overlaps(((Course)other).allTakenTimes());
		}
		else{
			return false;
		}
	}
	/**
	 * Clear the list of requirements that this course satisfies.
	 * Will not touch user specified requirements.
	 */
	public void clearReqsSatisfied(){
		this.reqsSatisfied = new HashSet<Requirement>();
	}
	/**
	 * Adds this requirement to the list of requirements satisfied by this course.
	 * Automatically ignores duplicates.
	 * @param req
	 */
	public void satisfies(Requirement req){
		this.reqsSatisfied.add(req);
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
	
	/**
	 * Return an unsorted list of the requirements satisfied by this course.
	 * @return
	 */
	@Override
	public ArrayList<Requirement> getRequirementsFulfilled(){
		ArrayList<Requirement> result = new ArrayList<Requirement>(reqsSatisfied);
		for(Requirement specified : userSpecifiedReqs){
			result.add(specified);
		}
		return result;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append(coursePrefix.toString());
		result.append(String.format("-%02d ", this.sectionNumber));
		result.append(this.meetingDaysCode() + " ");
		if(this.meetingTime != null){
			result.append( this.meetingTime.start.clockTime() + " ");
		}
		result.append(this.professor);
		
		return result.toString();
	}
	
	
	public static void main(String[] args){
		int[] meetingDays = {Time.MONDAY, Time.WEDNESDAY, Time.FRIDAY};
		Course c = new Course(new Prefix("MTH", 220), new SemesterDate(2017, SemesterDate.FALL), "Fray", meetingDays, 4, 02);
		c.setMeetingTime(11, true, 30, 50);
		System.out.println(c.meetingTime);
		System.out.println(c);
	}



	@Override
	public boolean isDuplicate(ScheduleElement other) {
		if(! ( other instanceof Course )){
			return false;
		}
		return this.coursePrefix.compareTo(((Course)other).coursePrefix) == 0;
	}



	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
}
