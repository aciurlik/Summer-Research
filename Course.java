import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Course implements ScheduleElement{


	protected int creditHours;
	protected Prefix coursePrefix;
	protected String sectionNumber; //was originally an int, but for courses like
	// ACC-221-BLK is was converted to a string.
	protected SemesterDate semester;
	protected int[] meetingDays; //specified by the constants in the Time class, 
	//  as in Time.SUNDAY.
	protected Time[] meetingTime; //two times where the day, month, and year are unused.
	protected String name;
	Time[] labTime; //assumed to repeat weakly until examTime. Month and year are unused.
	int labDay;
	Time[] examTime; // month, day, year, and so on are all used.

	String professor;

	HashSet<Requirement> reqsSatisfied;
	HashSet<Requirement> userSpecifiedReqs;



	/**
	 * Creates a course object
	 * @param c
	 * @param p
	 */

	public Course(Prefix prefix, SemesterDate semester, String professor, int[] meetingDays, 
			int creditHours, String sectionNumber ){
		this.creditHours=creditHours;
		this.coursePrefix=prefix;
		this.semester = semester;
		this.professor = professor;
		this.meetingDays = meetingDays;
		this.sectionNumber = sectionNumber;
		userSpecifiedReqs = new HashSet<Requirement>();
		reqsSatisfied = new HashSet<Requirement>();
	}

	/**
	 * A constructor that follows the order of a saved string.
	 * @param p
	 * @param sectionNumber
	 */
	public Course(Prefix prefix, String sectionNumber, String professor, int[] meetingDays, int creditHours, SemesterDate semester ){
		this.creditHours=creditHours;
		this.coursePrefix=prefix;
		this.semester = semester;
		this.professor = professor;
		this.meetingDays = meetingDays;
		this.sectionNumber = sectionNumber;
		userSpecifiedReqs = new HashSet<Requirement>();
		reqsSatisfied = new HashSet<Requirement>();
	}
	
	public void setName(String name){
		this.name = name;
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
		if (meetingDays==null){
			return result;
		}
		for (int day : meetingDays){
			result += Time.dayCodes[day];
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
		this.meetingTime = new Time[] {startTime, startTime.addMinutes(durationMinutes)};
	}
	public void setMeetingTime(Time[] t){
		this.meetingTime = t;
	}
	public void setExamTime(Time examStartTime, int durationInMinutes){
		this.examTime = new Time[]{
				examStartTime, 
				examStartTime.addMinutes(durationInMinutes)};
	}
	public void setExamTime(Time[] t){
		this.examTime = t;
	}
	public void setLabTime(int dayOfWeek, int hour, boolean AM, int minutes, int durationMinutes){
		Time startTime = new Time( hour, AM, minutes, 0);
		this.labTime = new Time[]{startTime, startTime.addMinutes(durationMinutes)};
		this.setLabDay(dayOfWeek);
	}
	public void setLabTime(Time[] t){
		this.labTime = t;
	}
	/**
	 * Should only be called after setting labTime.
	 * @param labDay
	 */
	public void setLabDay(int labDay){
		this.labDay = labDay;
	}

	//////////////
	/// Overlaps method and helper
	//////////////
	public Intervals<Time> allTakenTimes(){
		Intervals<Time> result = new Intervals<Time>();
		if(labTime != null){
			labTime[0].day = labDay;
			labTime[1].day = labDay;
			labTime[0].month = Time.UNUSED;
			labTime[0].year = Time.UNUSED;
			labTime[1].month = Time.UNUSED;
			labTime[1].year = Time.UNUSED;
			result.addInterval(new Interval<Time>(labTime[0], labTime[1]));
		}
		if(examTime != null){
			result.addInterval(new Interval<Time>(examTime[0], examTime[1]));
		}

		//note - some courses may have meeting times with all values unused.
		// For example, music courses often don't specify times.
		if(meetingDays != null && meetingTime != null){
			for (int day : meetingDays){
				Time s = new Time(meetingTime[0]);
				Time f = new Time(meetingTime[1]);
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
		result.append(String.format("-" + this.sectionNumber + " "));
		if(name != null){
			result.append(name + " ");
		}
		result.append(this.meetingDaysCode() + " ");
		if(this.meetingTime != null){
			result.append( this.meetingTime[0].clockTime() + " ");
		}
		result.append(this.professor);
		return result.toString();
	}

	@Override
	public String getDisplayString() {
		return this.toString();
	}

	public String saveString(){
		/*  TODO add name
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
		MTH-220-01;Fray;[3,5,7];11:30:A,50;5/30/2017 14:30:00;4;2017-2;2,2:30:P,150
		MTH-220-01;Fray;[3,5,7];11:30:P,50;5/30/2017 12:30:00;4;2017-2;
		 */

		StringBuilder result = new StringBuilder();
		result.append(this.coursePrefix.toString() + "-" + sectionNumber + ";");
		result.append(this.professor + ";");
		result.append(Arrays.toString(this.meetingDays) + ";");
		if(this.meetingTime != null){
			result.append(this.meetingTime[0].clockTime() + ",");
			result.append(meetingTime[0].minutesUntil(meetingTime[1]) );
		}
		result.append(";");
		if(this.examTime!=null){
			result.append(this.examTime[0].toString());
		}
		result.append(";");
		result.append(this.creditHours + ";");
		result.append(this.semester.saveString());
		if(this.labTime != null){
			int labDay = labTime[0].day;
			String timeString = labTime[0].clockTime();
			int labDuration = labTime[0].minutesUntil(labTime[1]);
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
		String sectionNumber = courseCode[2];

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
		boolean foundMeetingTime = false;
		String[] time = null;
		if(!chunks[chunkNumber].equals("")){
			foundMeetingTime = true;
			time = chunks[chunkNumber].split(":");
		}

		chunkNumber++;
		boolean foundExamTime = false;
		String examTimeString = null;
		if(!chunks[chunkNumber].equals("")){
			foundExamTime = true;
			examTimeString = chunks[chunkNumber];
		}


		chunkNumber++;
		int creditHours = Integer.parseInt(chunks[chunkNumber]);

		chunkNumber++;
		SemesterDate semester = SemesterDate.readFrom(chunks[chunkNumber]);

		Course result = new Course(prefix, sectionNumber, professor, meetingDays, creditHours, semester);
		if(foundExamTime){
			Time examTime = Time.readFrom(examTimeString);
			result.setExamTime(examTime, 150);
		}
		if(foundMeetingTime){
			int meetingHours = Integer.parseInt(time[0]);
			int meetingMinutes = Integer.parseInt(time[1]);
			time = time[2].split(",");
			boolean meetingAM = (time[0].contains("A"));
			int meetingDuration = Integer.parseInt(time[1]);
			result.setMeetingTime(meetingHours, meetingAM, meetingMinutes, meetingDuration);
		}


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
	public static Course readFromFurmanData(ArrayList<String> furmanData){
		String semesterString = furmanData.get(0); // EX "Fall 2017 - Day"
		String section = furmanData.get(1); // EX "ACC-111-01"
		String title = furmanData.get(3);  // EX "Financial Accounting Prncpls"
		int creditHours = Integer.parseInt(furmanData.get(4));
		String instructionalMethod = furmanData.get(5); 
		// Known examples 
		// EXAM, LEC, LAB, IDI, STU, SKL, SEM, ADD, ACT, blank 
		String location = furmanData.get(6);
		Time[] times = readTimesFrom(furmanData);
		String meetingDays = furmanData.get(9);
		String professor = furmanData.get(12);
		String GERs = furmanData.get(14);
		String prerequsites = furmanData.get(16);

		String[] sectionValues = section.split("-");
		Prefix p = new Prefix(sectionValues[0], sectionValues[1]);
		String sectionNumber = sectionValues[2];

		SemesterDate semester = SemesterDate.fromFurman(semesterString);

		Time totalStartTime = Time.combine(times[0], times[1]);
		Time totalEndTime = Time.combine(times[0], times[3]);
		Time[] meetingTime = new Time[]{totalStartTime, totalEndTime};


		Course result =  new Course(p, sectionNumber, professor, Time.meetingDaysFrom(meetingDays), creditHours, semester);
		if(meetingTime[0].hours != Time.UNUSED){
			result.setMeetingTime(meetingTime);
		}
		if(!title.equals("")){
			result.setName(title);
		}
		return result;




	}

	/**
	 * Return an array of times:
	 * StartDate
	 * StartTime
	 * EndDate
	 * EndTime
	 * @param furmanData
	 * @return
	 */
	public static Time[] readTimesFrom(ArrayList<String> furmanData){
		String startDateString = furmanData.get(7);
		String endDateString = furmanData.get(8);
		String meetingDaysString = furmanData.get(9);
		String startTimeString = furmanData.get(10);
		String endTimeString = furmanData.get(11);
		Time startTime = Time.tryRead(startTimeString);
		Time startDate = Time.tryRead(startDateString);
		Time endTime = Time.tryRead(endTimeString);
		Time endDate = Time.tryRead(endDateString);

		return new Time[]{startDate, startTime, endDate, endTime};

	}



	public static void main(String[] args){
		int[] meetingDays = {Time.MONDAY, Time.WEDNESDAY, Time.FRIDAY};
		Course c = new Course(new Prefix("MTH", 220), new SemesterDate(2017, SemesterDate.FALL), "Fray", meetingDays, 4, "02");
		c.setMeetingTime(11, true, 30, 50);
		c.setExamTime(new Time(2017, 6, 20, 13, 30, 0), 150);


		System.out.println(c.saveString());
		Course d = Course.readFrom(c.saveString());
		System.out.println(d.saveString());
		//TODO check out when you read in again it's military time?


	}


}