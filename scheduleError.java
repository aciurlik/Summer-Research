import java.util.HashSet;

public class scheduleError {
	String error = null; 
	ScheduleElement[] duplicateCourses = null;
	Prefix course = null;
	Prefix missingCourse = null; 
	ScheduleElement offendingCourse = null;
	HashSet<Prefix> neededCourses = null;
	int overloadLimit = 0;
	String instructions;

	//Duplicate Error and overlaps 
	public scheduleError(String s, ScheduleElement[] offendingCourses){
		this.error=s;
		this.duplicateCourses = offendingCourses;
		
		if(this.error.equals(MenuOptions.duplicateError)){
			this.instructions=duplicateCourses[0].getDisplayString() + " duplicates " +  duplicateCourses[1];		
		}
		if(this.error.equals(MenuOptions.overlapError)){
			this.instructions=duplicateCourses[0].getDisplayString() + " overlaps " + duplicateCourses[1];	
		}

	}

	//Prereq Error
	public scheduleError(String prereqerror, Prefix p, Prefix newP) {
		this.error = prereqerror;
		this.course=p;
		this.missingCourse=newP;
		this.instructions = this.course.toString() + " has prerequisite " + this.missingCourse.toString();



	}
	//Prereq Error
	public scheduleError(String prereqerror, ScheduleElement newE, HashSet<Prefix> missing) {
		this.error = prereqerror;
		this.offendingCourse = newE;
		this.neededCourses = missing;
		this.instructions = newE.getDisplayString() + " needs " + missing.toString() + " as a prerequisise";


	}


	//Overload Error
	public scheduleError(String overloaderror, ScheduleElement addition, int overloadLimit) {
		this.error = overloaderror;
		this.offendingCourse = addition;
		this.overloadLimit=overloadLimit;
		this.instructions = addition.getDisplayString() + " would exceeded the overload limit of " + overloadLimit;		
	}
}
