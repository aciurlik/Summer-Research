import java.util.HashSet;

public class ScheduleError {
	String error = null; 
	ScheduleElement[] duplicateCourses = null;
	Prefix offendingPCourse = null;
	Prefix missingCourse = null; 
	ScheduleElement offendingCourse = null;
	HashSet<Prefix> neededCourses = null;
	int overloadLimit = 0;
	String instructions;
	public static final String duplicateError = "Duplicate Error";
	public static final String preReqError = "Prerequisite Error";
	public static final String overlapError = "Overlap Error";
	public static final String overloadError = "Overload Error";
	public static final String preReqErrorPrefix = "Prerequisite Error";

	 
	public ScheduleError(String s){
		this.error = s;
	}
	
	


	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}


	public ScheduleElement[] getDuplicateCourses() {
		return duplicateCourses;
	}


	public void setDuplicateCourses(ScheduleElement[] duplicateCourses) {
		this.duplicateCourses = duplicateCourses;
	}


	public Prefix getCourse() {
		return offendingPCourse;
	}


	public void setCourse(Prefix course) {
		this.offendingPCourse = course;
	}


	public Prefix getMissingCourse() {
		return missingCourse;
	}


	public void setMissingCourse(Prefix missingCourse) {
		this.missingCourse = missingCourse;
	}


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


	