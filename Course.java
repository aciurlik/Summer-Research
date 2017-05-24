import java.util.ArrayList;
import java.util.Arrays;
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
			 int creditHours, int sectionNumber ){
		this.creditHours=creditHours;
		this.coursePrefix=prefix;
		this.semester = semester;
		this.professor = professor;
		this.meetingDays = meetingDays;
		this.sectionNumber = sectionNumber;
	}
	
	/**
	 * A constructor that follows the order of a saved string.
	 * @param p
	 * @param sectionNumber
	 */
	public Course(Prefix prefix, int sectionNumber, String professor, int[] meetingDays, int creditHours, SemesterDate semester ){
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
	

	@Override
	public boolean isDuplicate(ScheduleElement other) {
		if(! ( other instanceof Course )){
			return false;
		}
		return this.coursePrefix.compareTo(((Course)other).coursePrefix) == 0;
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
	
	@Override
	public String getDisplayString() {
		return this.toString();
	}
	
	public String saveString(){
		/*
		 creditHours;
		 coursePrefix;
		 sectionNumber;
		 semester; (semesterDate)
		 meetingDays; (int[])
		 meetingTime;
		 Professor;
		 examTime;
		 labTime (Interval<Time>);
		
		Prefix-sectionNumber;Professor; meetingDays; meetingTime,meetingDuration; examTime;creditHours;Semester[;labDay,labTime,labDuration]
		
		Examples:
		MTH-220-01;Fray;[3,5,7];11:30:A,50;30/5/2017 14:30:00;4;2017-2;2,2:30:P,150
		MTH-220-01;Fray;[3,5,7];11:30:P,50;30/5/2017 12:30:00;4;2017-2;
		*/
		
		StringBuilder result = new StringBuilder();
		result.append(this.coursePrefix.toString() + String.format("-%02d", sectionNumber) + ";");
		result.append(this.professor + ";");
		result.append(Arrays.toString(this.meetingDays) + ";");
		result.append(this.meetingTime.start.clockTime() );
		if(this.meetingTime.start.isAM()){
			result.append(":A,");
		}
		else{
			result.append(":P,");
		}
		result.append(meetingTime.start.minutesUntil(meetingTime.end) + ";");
		result.append(this.examTime.start.toString() + ";");
		result.append(this.creditHours + ";");
		result.append(this.semester.saveString());
		if(this.labTime != null){
			int labDay = labTime.start.day;
			String timeString = labTime.start.clockTime();
			if(labTime.start.isAM()){
				timeString = timeString + ":A";
			}
			else{
				timeString = timeString+ ":P";
			}
			int labDuration = labTime.start.minutesUntil(labTime.end);
			result.append(";" + labDay);
			result.append("," + timeString);
			result.append("," + labDuration);
		}
		
		return result.toString();
		
	}
	public static Course readFrom(String saveString){
		int chunkNumber = 0;
		String[] chunks = saveString.split(";");
		String[] courseCode = chunks[chunkNumber].split("-");
		Prefix prefix = new Prefix(courseCode[0], Integer.parseInt(courseCode[1]));
		int sectionNumber = Integer.parseInt(courseCode[2]);
		
		chunkNumber ++;
		String professor = chunks[chunkNumber];
		
		//make the meetingDays list
		chunkNumber ++;
		String[] daysStrings = chunks[chunkNumber].substring(1,chunks[chunkNumber].length() - 1).split(",");
		int[] meetingDays = new int[daysStrings.length];
		for(int i = 0; i < meetingDays.length ; i ++){
			meetingDays[i] = Integer.parseInt(daysStrings[i].replace(" ",""));
		}
		
		//Make the meetingTimes 
		chunkNumber ++;
		String[] time = chunks[chunkNumber].split(":");
		int meetingHours = Integer.parseInt(time[0]);
		int meetingMinutes = Integer.parseInt(time[1]);
		time = time[2].split(",");
		boolean meetingAM = (time[0].contains("A"));
		int meetingDuration = Integer.parseInt(time[1]);
		
		chunkNumber++;
		Time examTime = Time.readFrom(chunks[chunkNumber]);
		
		chunkNumber++;
		int creditHours = Integer.parseInt(chunks[chunkNumber]);
		
		chunkNumber++;
		SemesterDate semester = SemesterDate.readFrom(chunks[chunkNumber]);
		
		Course result = new Course(prefix, sectionNumber, professor, meetingDays, creditHours, semester);
		result.setExamTime(examTime, 150);
		result.setMeetingTime(meetingHours, meetingAM, meetingMinutes, meetingDuration);
		
		
		chunkNumber++;
		if(chunkNumber < chunks.length){
			String[] labData = chunks[chunkNumber].split(",");
			int labDay = Integer.parseInt(labData[0]);
			int labDuration = Integer.parseInt(labData[2]);
			String[] labTimeString = labData[1].split(":");
			int labHours = Integer.parseInt(labTimeString[0]);
			int labMinutes = Integer.parseInt(labTimeString[1]);
			boolean labAM =labTimeString[2].contains("A");
			
			result.setLabTime(labDay, labHours, labAM, labMinutes, labDuration);
		}
		
		return result;
		
		
		
	}
	
	
	public static void main(String[] args){
		int[] meetingDays = {Time.MONDAY, Time.WEDNESDAY, Time.FRIDAY};
		Course c = new Course(new Prefix("MTH", 220), new SemesterDate(2017, SemesterDate.FALL), "Fray", meetingDays, 4, 02);
		c.setMeetingTime(11, true, 30, 50);
		c.setExamTime(new Time(2017, 6, 20, 13, 30, 0), 150);
		System.out.println(c.saveString());
		Course d = Course.readFrom(c.saveString());
		System.out.println(d.saveString());
		//TODO check out when you read in again it's military time?
	}

	
	
	
	
}
