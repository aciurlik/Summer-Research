

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;


public class Semester implements Comparable<Semester>{
	public SemesterDate semesterDate;
	public ArrayList<ScheduleElement> elements;
	public Schedule schedule;
	private int OverloadLimit;
	public boolean hasNotes = false;
	public String notes = "";
	boolean isAP = false;
	public boolean studyAway = false;
	protected boolean extraSemester = false;
	protected boolean lastSemester = false;






	

	public Semester(SemesterDate sD, Schedule s){
		elements = new ArrayList<ScheduleElement>();
		this.semesterDate = sD;
		this.schedule = s;
		if(this.semesterDate.sNumber == SemesterDate.FALL || this.semesterDate.sNumber == SemesterDate.SPRING){
			OverloadLimit = 20;
		}
		if(this.semesterDate.sNumber == SemesterDate.SUMMERONE|| this.semesterDate.sNumber == SemesterDate.SUMMERTWO){
			OverloadLimit = 8;
		}
		if(this.semesterDate.sNumber == SemesterDate.MAYX ){
			OverloadLimit = 2;
		}
	}

	public ArrayList<ScheduleElement> getElements(){
		return elements;
	}

	public SemesterDate getDate(){
		return semesterDate;
	}

	/**
	 * Check for any overlap in this semester with the new element.
	 * 
	 * true if overlap is found.
	 * 
	 * @param addition
	 */

	public boolean checkOverlap(ScheduleElement addition){
		//only courses can have overlap.

		if(addition == null || ( ! (addition instanceof ScheduleCourse))){
			return false;
		}
		ArrayList<ScheduleCourse> courses = allCourses();
		for(int i= 0; i<courses.size(); i++){
			if(courses.get(i).overlaps(addition)){
				ScheduleError overlaped = makeOverlapError(courses.get(i), (ScheduleCourse) addition);
				if(!this.schedule.userOverride(overlaped)){
					return true; //we found an overlap, and the user didn't override
				}
			}
		}
		return false;
	}

	/**
	 * Check all courses in this semester for overlap issues.
	 * 
	 * Return a list of all errors found, size 0 if none found.
	 * @return
	 */
	public ArrayList<ScheduleError> checkAllOverlap(){
		ArrayList<ScheduleError> overlaps = new ArrayList<ScheduleError>();
		ArrayList<ScheduleCourse> courses = allCourses();
		for (int i = 0; i < courses.size() ; i ++){
			for(int j = i+1; j < courses.size() ; j ++){
				if(courses.get(i).overlaps(courses.get(j))){
					ScheduleError overlapped = makeOverlapError(courses.get(i), courses.get(j));
					overlaps.add(overlapped);
				}
			}
		}
		return overlaps;
	}

	private ArrayList<ScheduleCourse> allCourses(){
		ArrayList<ScheduleCourse> courses = new ArrayList<ScheduleCourse>();
		for (ScheduleElement e : this.elements){
			if(e instanceof ScheduleCourse){
				courses.add((ScheduleCourse)e);
			}
		}
		return courses;
	}

	/**
	 * Given courses where c1.overlaps(c2) is true.
	 * @param c1
	 * @param c2
	 * @return
	 */
	private ScheduleError makeOverlapError(ScheduleCourse c1, ScheduleCourse c2){
		ScheduleError result = new ScheduleError(ScheduleError.overlapError);
		ScheduleCourse[] overlap = new ScheduleCourse[]{c1, c2};
		result.setElementList(overlap);

		//Figure out whether it's lab, meeting, or exam that overlaps.
		if(c1.examTime().overlaps(c2.examTime())){
			result.examOverlap = true;
		}
		if(c1.labTime() != null && c2.labTime() != null){
			if(c1.labTime().overlaps(c2.labTime())){
				result.labOverlap = true;
			}
		}
		if(c1.meetingTimes().overlaps(c2.meetingTimes())){
			result.meetingOverlap = true;
		}
		return result;
	}



	/**
	 * Check if adding addition would make an overload
	 * This is able to take a null input. 
	 * @param forAll true if checking all errors, false otherwise
	 * @param addition the schedule Element you are adding to your semester
	 * @return true if there is an error in the case of for all, false if no error, and user dependented true/false otherwise
	 */
	public boolean checkOverload(boolean forAll,ScheduleElement addition){
		System.out.println("checking overload");
		int totalHours = 0;

		if(addition instanceof ScheduleCourse){
			ScheduleCourse toAdd = (ScheduleCourse) addition;
			totalHours= totalHours + toAdd.getCreditHours();
		}
		if(addition instanceof Requirement){
			Requirement toAdd = (Requirement) addition;
			totalHours = totalHours + toAdd.getCreditHours();
		}
		totalHours = totalHours + getCreditHours();
		if(totalHours > OverloadLimit){
			ScheduleError overload = new ScheduleError(ScheduleError.overloadError);
			overload.setOverloadLimit(this.OverloadLimit);
			overload.setOffendingCourse(addition);
			//	overload.setInstructions("Adding " + addition.getDisplayString() + " exceeds this semester's overload limit of " + this.OverloadLimit );
			if(forAll == false){
				return (!this.schedule.userOverride(overload));
			}
			if(forAll == true){
				return true;
			}
		}
		return false;
	}


	public int getCreditHours(){
		int totalHours = 0;
		for(ScheduleElement e : this.elements){
			
			if(e instanceof ScheduleCourse){
				totalHours = totalHours + ((ScheduleCourse) e).getCreditHours();
			
				
			}
			
			if(e instanceof Requirement){
				totalHours = totalHours + ((Requirement) e).getCreditHours();
				
			}
		}
		return totalHours;
		
	}



	public int compareTo(Semester other){
		return this.semesterDate.compareTo(other.semesterDate);
	}


	/**
	 * Add a schedule element to this semester.
	 * This method should only be called by Schedule.
	 * @param e
	 * @return
	 */
	public boolean add(ScheduleElement e){
		if(!this.checkOverload(false, e)){
			this.elements.add(e);
			return true;
		}
		else{
			return false;
		}
	}






	public boolean remove(ScheduleElement e){
		//this.schedule.checkErrorsWhenRemoving(e, this);
		this.elements.remove(e);
		return true;
	}




	public boolean replace(ScheduleElement oldElement, ScheduleElement newElement){
		if(!this.checkOverload(false, newElement)){
			//	System.out.println(oldElement);

			int i = this.elements.indexOf(oldElement);
			//	System.out.println(i);
			//	System.out.println(this.elements.size());
			this.elements.set(i, newElement);

			return true;
		}
		else{
			return false;
		}

		//this.schedule.checkErrorsWhenAdding(newElement, this);
		//this.schedule.checkErrorsWhenRemoving(oldElement, this);

	}


	public ArrayList<ScheduleCourse> getCoursesSatisfying(Requirement r){
		ArrayList<Course> semesterCourses = this.schedule.masterList.getCoursesIn(this);
		ArrayList<Course> finalCourse = this.schedule.masterList.onlyThoseSatisfying(semesterCourses, r);
		ArrayList<ScheduleCourse> coursesSatisfying = new ArrayList<ScheduleCourse>();
		for(Course c : finalCourse){
			ScheduleCourse s = new ScheduleCourse(c, this.schedule);
			coursesSatisfying.add(s);
		}
		return coursesSatisfying;
	}


	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isHasNotes() {
		return hasNotes;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}

	public boolean isStudyAway() {
		return studyAway;
	}

	public void setStudyAway(boolean studyAway) {
		this.studyAway = studyAway;
	}

	


	public int getOverloadLimit() {
		return OverloadLimit;
	}

	public void setOverloadLimit(int overloadLimit) {
		OverloadLimit = overloadLimit;
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof Semester)){
			return false;
		}
		Semester o = (Semester) other;
		if(this.compareTo(o)==0){
			return true;
		}
		else{
			return false;
		}
	}

	public void setExtraSemester(boolean b) {
		this.extraSemester =b;
		
	}
	

	public void setLastSemester(boolean lastSemester) {
		this.lastSemester = lastSemester;
	}


}