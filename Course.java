import java.util.ArrayList;


/**
 * 
 * blurb written: 7/23/2017
 * last updated: 7/26/2017
 * 
 * This class represents one actual offered course in furman's system,
 * including the section number and semester.
 * 
 * It can compare with other courses to see if their times overlap, and 
 * is a ScheduleElement.
 * 
 * It can contain null values for:
 * 		Professor
 * 		name (title)
 * 		credit hours
 * 		any time-related field except 'semester'
 * 		section number
 * 		
 * but it must have:
 * 		coursePrefix (see prefix class)
 * 		Semester
 * 		
 * 		
 * 
 *  It is in the FILE group of classes.
 *  
 *
 *
 */
public class Course implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	protected int creditHours;
	protected Prefix coursePrefix;
	protected String sectionNumber; //was originally an int, but for courses like
	// ACC-221-BLK is was converted to a string.
	protected String name; //course title, like "Principles of Accounting"
	String professor;
	
	protected SemesterDate semesterDate;
	protected int[] meetingDays; //specified by the constants in the Time class, 
	//  as in Time.SUNDAY.
	protected Time[] meetingTime; //two times where the day, month, and year will be unused.
	// The course meets each day in meedingDays, between meetingTime[0] and meetingTime[1].
	Time[] labTime; //assumed to repeat weakly until examTime. Month and year will be unused.
	int labDay;
	Time[] examTime; // month, day, and year are USED, not unused, because 
	// the exam time is just one day and doesn't repeat. 
	
	
	//These two fields are used to estimate whether a course loaded from Furma's data is in SS1 or SS2.
	//See the readFromFurmanData method
	static Time SummerSessionOne = new Time(Time.UNUSED, 6, 16, Time.UNUSED, Time.UNUSED, Time.UNUSED);
	static Time SummerSessionTwo = new Time(Time.UNUSED, 7, 25, Time.UNUSED, Time.UNUSED, Time.UNUSED);





	/**
	 * Creates a course object
	 * @param c
	 * @param p
	 */

	public Course(Prefix prefix, SemesterDate semester, String professor, int[] meetingDays, 
			int creditHours, String sectionNumber ){
		this.creditHours=creditHours;
		this.coursePrefix=prefix;
		this.semesterDate = semester;
		this.professor = professor;
		this.meetingDays = meetingDays;
		this.sectionNumber = sectionNumber;

	}

	/**
	 * A constructor that follows the order of a saved string.
	 * @param p
	 * @param sectionNumber
	 */
	public Course(Prefix prefix, String sectionNumber, String professor, int[] meetingDays, int creditHours, SemesterDate semester ){
		this(prefix, semester, professor, meetingDays, creditHours, sectionNumber);
	}
	
	
	
	
	/**
	 * A constructor used for temporary courses that aren't actually offered
	 * (used in Requirements to test for completion).
	 * @param p
	 * @param creditHours
	 */
	public Course(Prefix p, int creditHours){
		this.coursePrefix = p;
		this.creditHours = creditHours;
	}
	
	
	
	/////////////////////////
	/////////////////////////
	////Getters and setters
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GettersAndSetters_________;
	

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}

	public Prefix getPrefix(){
		return coursePrefix;
	}

	public int getCreditHours(){
		return creditHours;
	}
	
	public Time getStartTime(){
		if(meetingTime == null){
			return null;
		}
		else if(meetingTime.length == 0){
			return null;
		}
		else if(meetingTime[0] == null){
			return null;
		}
		return meetingTime[0].dateless();
	}


	public SemesterDate getSemesterDate() {
		return this.semesterDate;
	}

	
	/**
	 * turn the meeting days array, of the form [1, 3, 5], into a string
	 * of the form "MWF" and return that string.
	 * @return
	 */
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



	/////////////////////////
	/////////////////////////
	////Time getters and setters
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ______TimeGettersAndSetters_________;
	
	/**
	 * set the meeting time to start at hours:min(AM)
	 * and last for durationMinutes minutes. So, if hours = 8, min = 30, 
	 * AM = false, and durationMinutes = 60 it will set the time to
	 * 8:30PM to 9:30PM.
	 * 
	 * @param hours
	 * @param AM
	 * @param minutes
	 * @param durationMinutes
	 */
	public void setMeetingTime(int hours, boolean AM, int minutes, int durationMinutes){
		Time startTime = new Time(hours, AM, minutes, 0);
		startTime.setYear(semesterDate.getYear());
		startTime.setMonth(this.semesterDate.getStartMonth());
		this.meetingTime = new Time[] {startTime, startTime.addMinutes(durationMinutes)};
	}
	public void setMeetingTime(Time[] t){
		this.meetingTime = t;
	}
	
	/**
	 * This method must be given a Time object that uses month, day, and year.
	 * @param examStartTime
	 * @param durationInMinutes
	 */
	public void setExamTime(Time examStartTime, int durationInMinutes){
		this.examTime = new Time[]{
				examStartTime, 
				examStartTime.addMinutes(durationInMinutes)};
	}
	public void setExamTime(Time[] t){
		this.examTime = t;
	}
	
	/**
	 * See setMeetingTime(hours, AM, minutes, durationMinutes)
	 *  for explanation of the arguments.
	 * @param dayOfWeek
	 * @param hour
	 * @param AM
	 * @param minutes
	 * @param durationMinutes
	 */
	public void setLabTime(int dayOfWeek, int hour, boolean AM, int minutes, int durationMinutes){
		Time startTime = new Time( hour, AM, minutes, 0);
		this.labTime = new Time[]{startTime, startTime.addMinutes(durationMinutes)};
		this.setLabDay(dayOfWeek);
	}
	public void setLabTime(Time[] t){
		this.labTime = t;
	}
	
	public void setLabDay(int labDay){
		this.labDay = labDay;
	}



	/**
	 * Return times with:
	 * 		day set to the lab day,
	 * 		hours and min set to the correct values, 
	 * 		sec may or may not be unused,
	 * 		and year/month unused.
	 * 
	 * May return null.
	 * 
	 * @return
	 */
	public Interval<Time> labTime(){
		if(labTime != null){
			labTime[0].day = labDay;
			labTime[1].day = labDay;
			labTime[0].month = Time.UNUSED;
			labTime[0].year = Time.UNUSED;
			labTime[1].month = Time.UNUSED;
			labTime[1].year = Time.UNUSED;
			return new Interval<Time>(labTime[0], labTime[1]);
		}
		return null;
	}
	
	/**
	 * Return times that use 
	 * year, month, day, hour, and min
	 * 		(sec may be unused)
	 * may return null
	 * @return
	 */
	public Interval<Time> examTime(){
		if(examTime == null){
			return null;
		}
		return new Interval<Time>(examTime[0], examTime[1]);
	}

	/**
	 * find a list of all the intervals
	 *  representing all the meeting times for
	 *  this course.
	 *  
	 * Each interval in the list represents one day, so it will have times
	 * of the form 
	 * 		year:UNUSED month:UNUSED day:one of 0-6 hour:given min:given sec:UNUSED or given.
	 * @return
	 */
	public Intervals<Time> meetingTimes(){
		if(meetingDays != null && meetingTime != null){
			Intervals<Time> result = new Intervals<Time>();
			for (int day : meetingDays){
				Time start = new Time(meetingTime[0]);
				Time finish = new Time(meetingTime[1]);
				start.advanceToNext(day);
				finish.advanceToNext(day);
				result.addInterval(new Interval<Time>(start,finish));
			}
			return result;
		}
		else{
			return null;
		}
	}


	/////////////////////////
	/////////////////////////
	////SavingAndReadingMethods
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___SavingAndReadingMethods_________;
	
	
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append(coursePrefix.toString());
		if(this.sectionNumber != null){
			result.append(String.format("-" + this.sectionNumber + " "));
		}
		if(name != null){
			result.append(name + " ");
		}
		if(this.meetingDaysCode()!=null);
		result.append(this.meetingDaysCode() + " ");
		if(this.meetingTime != null){
			result.append( this.meetingTime[0].clockTime() + " ");
		}
		if(professor != null){
			result.append(this.professor);
		}
		return result.toString();
	}







	/*
	public String saveString(){
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
*/
	
	/*
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
	*/
	
	
	/**
	 * last updated 7/23/2017
	 * Read from data of the form found in a course catalog downloaded from furman.
	 * To download a course catalog, go to myFurman, view course listings
	 *  (Registration --> course listings), choose a particular semester
	 *  and click "search" or "submit" without putting any other filters in.
	 * It should give you the option to download the full catalog, and that's the data.
	 * @param furmanData
	 * @return
	 */
	public static Course readFromFurmanData(ArrayList<String> furmanData){
		String semesterString = furmanData.get(0); // EX "Fall 2017 - Day"
		String section = furmanData.get(1); // EX "ACC-111-01"
		String title = furmanData.get(3);  // EX "Financial Accounting Prncpls";
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


		if(semester.sNumber==(SemesterDate.SUMMERONE) || semester.sNumber==(SemesterDate.SUMMERTWO)){
			//Furman doesn't specify S1 or S2 in the course catalog, so we have to try to figure out which
			// semester the course is offered in using the meeting times.
			// Our strategy is to make two times, 6/16 and 7/25, which will be the estimated
			// midpoints of the two summer sessions. Then, for each course, 
			// if the dates the course meets contain one of these two midpoints, 
			// categorize that course accordingly (so if the course meets between
			// 5/12 and 6/30, we'll call it a summer session one course).
			// Then shift the summerSessionOne midpoint in the direction of the midpoint
			// for the course's dates, so that our 6/16 and 7/25 update as we go.

			Interval<Time> sessionInterval = new  Interval<Time>(times[0], times[2]);
			SummerSessionOne.year = times[0].year;
			SummerSessionTwo.year = times[0].year;
			
			if(sessionInterval.contains(SummerSessionOne, true)){
				semester = new SemesterDate(semester.year, SemesterDate.SUMMERONE);
				Time courseMidpoint = Time.findMidPoint(times[0], times[2]);
				//Now, shift SummerSessionTwo to be closer to the midpoint of this course.
				SummerSessionOne = Time.findMidPoint(SummerSessionOne,courseMidpoint);
			}
			if(sessionInterval.contains(SummerSessionTwo, true)){
				semester = new SemesterDate(semester.year,SemesterDate.SUMMERTWO);
				Time courseMidpoint = Time.findMidPoint(times[0],times[2]);
				//Now, shift SummerSessionTwo to be closer to the midpoint of this course.
				SummerSessionTwo = Time.findMidPoint(SummerSessionTwo,courseMidpoint);
			}
		}
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



	}




}