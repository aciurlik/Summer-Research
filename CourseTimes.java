import java.util.ArrayList;

/**
 * The class used by a course to keep track of when it meets.
 * 
 * This class tracks the time of week that a course meets, the semester, 
 * lab times, exam times, and other necessary times that a course
 * may need to know.
 * 
 * It includes methods to check if it overlaps 
 * with another CourseTimeIntervals.
 * 
 * 
 * @author dannyrivers
 *
 */

public class CourseTimes {
	
	

	
	public static final int SPRING = 2;
	public static final int FALL = 1;
	public static final int SUMMER = 3;
	public static final int MAYX = 4;
	
	
	ArrayList<Integer> daysOfWeek; //0 thru 6, 0 = sunday, 6 = saturday
	Interval<Time> examTime;
	Interval<Time> labTime;
	Interval<Time> dailyMeetingTime;
	//Lab and Daily meeting times happen every week.
	// Rather than including the entire semester's times, 
	// we can simply store one week's worth of time 
	// to see if classes overlap.
	
	int semester;
	int year;
	Time weekStart;
	
	
	public CourseTimes(int semester, int year){
		this.semester = semester;
		this.year = year;
		int month = 0;
		switch(semester){
		case SPRING:
			month = 1;
			break;
		case FALL:
			month = 8;
			break;
		case MAYX:
			month = 5;
			break;
		case SUMMER:
			month = 6;
			break;
		}
		this.weekStart = new Time(year,month, 0, 0, 0, 0);
		while(weekStart.dayOfWeek() != Time.SUNDAY){
			weekStart = weekStart.addDays(1);
		}
	}
	
	
	
	public void setMeetingDays(ArrayList<Integer> daysOfWeek){
		this.daysOfWeek = daysOfWeek;
	}
	
	
	private int toMilitary(int hours, boolean AM){
		if(hours == 12 ){
			if(AM) return 0;
			return 12;
		}
		if(AM){
			return hours;
		}
		return hours + 12;
	}
	
	

	public void setExamTime(Time examStartTime, int durationInMinutes){
		this.examTime = new Interval<Time>(
				examStartTime, 
				examStartTime.addMinutes(durationInMinutes)
				);
	}
	/**
	 * Assumes that exams last 150 minutes.
	 * @param examStartTime
	 */
	public void setExamTime(Time examStartTime){
		this.examTime = new Interval<Time>(examStartTime, examStartTime.addMinutes(150));
	}
	

	public void setLabTime(int dayOfWeek, int hour, boolean AM, int minutes ){
		int acutalHour = toMilitary(hour, AM);
		
	}
	public void setLabTim(int day, int hour, int minutes, int durationMinutes){
		
	}
	
	
	
	public Intervals<Time> allTakenTimes(){
		Intervals<Time> result = new Intervals<Time>();
		if(labTime != null){
			result.addInterval(labTime);
		}
		if(examTime != null){
			result.addInterval(examTime);
		}
		if(dailyMeetingTime != null && daysOfWeek != null){
			for (int day : daysOfWeek){
				
			}
		}
		return result;
		
		
	}
	public boolean overlaps(CourseTimes other){
		return this.allTakenTimes().overlaps(other.allTakenTimes());
	}
	
}
