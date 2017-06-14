import java.util.HashSet;

public class ScheduleError {
	String error; 
	ScheduleElement[] elementList;
	Prefix offendingPrefix;
	ScheduleElement offendingCourse;
	HashSet<Prefix> neededCourses;
	int overloadLimit = 0;
	String instructions;
	public static final String duplicateError = "Duplicate Error";
	public static final String preReqError = "Prerequisite Error";
	public static final String overlapError = "Overlap Error";
	public static final String overloadError = "Overload Error";
	public static final String preReqErrorPrefix = "Prerequisite Error";
	
	boolean meetingOverlap = false;
	boolean examOverlap = false;
	boolean labOverlap = false;

	 
	public ScheduleError(String s){
		this.error = s;
	}

	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}


	public ScheduleElement[] getElementList() {
		return elementList;
	}


	public void setElementList(ScheduleElement[] newList) {
		this.elementList = newList;
	}


	public Prefix getPrefix() {
		return offendingPrefix;
	}


	public void setPrefix(Prefix p) {
		this.offendingPrefix = p;
	}


//	public Prefix getMissingCourse() {
//		return missingCourse;
//	}


//	public void setMissingCourse(Prefix missingCourse) {
//		this.missingCourse = missingCourse;
//	}


	public ScheduleElement getOffendingCourse() {
		return offendingCourse;
	}


	public void setOffendingCourse(ScheduleElement offendingCourse) {
		this.offendingCourse = offendingCourse;
	}


	public HashSet<Prefix> getNeededCourses() {
		return neededCourses;
	}


	public void setNeededCourses(HashSet<Prefix> neededCourses) {
		this.neededCourses = neededCourses;
	}


	public int getOverloadLimit() {
		return overloadLimit;
	}


	public void setOverloadLimit(int overloadLimit) {
		this.overloadLimit = overloadLimit;
	}



	
}


	