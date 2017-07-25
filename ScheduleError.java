import java.util.HashSet;

public class ScheduleError implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	String error; //This is used by all error types
	ScheduleElement[] elementList; //Used by overlap, and duplicate error. 
	ScheduleElement offendingCourse;//Used by overload, prereq, duplicate
	Requirement req;//Used by preReq
	int overloadLimit = 0;//Used by overload error
	Semester offendingSemester;//Used by overload error. 
	/**
	 * 
	 * Blurb written 7/24/2017
	 * Last updated 7/24/2017
	 * Each time the schedule, or semester catches
	 * an error it creates a ScheduleError object. The fields 
	 * in the schedule error can be used by multiple error types, and
	 * each error type can use more than one field. The ScheduleErrors are created
	 * in the ScheduleGUI. 
	 * 
	 * The error types are:
	 * 
	 * Overlap (two courses at the same time),
	 * 
	 * Overload (Class that puts a semester over its credit hours limit)
	 * 
	 * Prerequisite (A class requires another course that is currently not placed before
	 * the original class in the schedule), 
	 * 
	 * Duplicate (A prefix occurs twice in the schedule) 
	 * 
	 * Optimism (Warns the user that the schedule is making
	 * an assumption on the next category of a class within the overarching requirement. For example
	 * if a user drags in a NWL/NW and chooses a NW class then when they drag in the second
	 * NWL/NW the ScheduleError will inform them that the schedule assumes this is a NWL. If the user changes
	 * it to another NW, another error will not be thrown, that particular requirement will not be filled. 
	 * This is the only error that does not pop up in the check all errors. 
	 * 
	 */


	public static final String duplicateError = "Duplicate Error";
	public static final String preReqError = "Prerequisite Error";
	public static final String preReqErrorPrefix = "Prerequisite Error";
	public static final String overlapError = "Overlap Error";
	public static final String overloadError = "Overload Error";
	public static final String optimisticSchedulerError = "Optimisim error";
	
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

	public ScheduleElement getOffendingCourse() {
		return offendingCourse;
	}
	public void setOffendingCourse(ScheduleElement offendingCourse) {
		this.offendingCourse = offendingCourse;
	}


	public Requirement getNeededCourses() {
		return req;
	}
	public void setNeededCourses(Requirement neededCourses) {
		this.req = neededCourses;
	}
	
	
	public Requirement getOptimisticRequirement(){
		return req;
	}
	public void setOptimisticRequirement(Requirement r){
		this.req = r;
	}


	public int getOverloadLimit() {
		return overloadLimit;
	}
	public void setOverloadLimit(int overloadLimit) {
		this.overloadLimit = overloadLimit;
	}

	public Semester getOffendingSemester() {
		return offendingSemester;
	}
	public void setOffendingSemester(Semester offendingSemester) {
		this.offendingSemester = offendingSemester;
	}

	
	/**
	 * To test errors make sure each error appears in three places. It should
	 * a.) Appear in a dialog box when the error is made
	 * b.) Be listed one checks all errors without fixing the given error. (If a user fixes that
	 * error. For example removes a course that was causing a semester to be considered overloaded, that 
	 * error should not appear in the dialog box that appears when the user clicks "check all errors)
	 * c.) Appear in the print out of the student's schedule in the Schedule Layout version. 
	 * [Note: Again the optimism error will only show in version a.)]
	 */
}


	