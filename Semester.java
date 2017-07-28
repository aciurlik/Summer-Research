

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Blurb written 7/26/2017
 * Last updated 7/26/2017
 * 
 * A single semester which holds ScheduleElements.
 * 
 * 
 */
public class Semester implements Comparable<Semester>, java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SemesterDate semesterDate; //the year and season for this semester
	public ArrayList<ScheduleElement> elements;
	public Schedule schedule;
	
	private int overloadLimit; // the number of credits allowed in this semester
	
	String notes; // if this is not null, then the notes panel will show up when 
	// a semester panel updates.
	
	boolean isPriorSemester = false; //true if this semester is the semester that holds
	//courses taken before starting Furman, like AP classes or placement courses.
	public boolean studyAway = false;
	 







	public Semester(SemesterDate sD, Schedule s){
		elements = new ArrayList<ScheduleElement>();
		this.semesterDate = sD;
		this.schedule = s;
		if(this.semesterDate.sNumber == SemesterDate.FALL || this.semesterDate.sNumber == SemesterDate.SPRING){
			if(this.semesterDate.equals(this.schedule.firstSemester) || this.semesterDate.equals(this.schedule.firstSemester.nextSemester())){
				
			}
			overloadLimit = 20;
		}
		if(this.semesterDate.sNumber == SemesterDate.SUMMERONE|| this.semesterDate.sNumber == SemesterDate.SUMMERTWO){
			overloadLimit = 8;
		}
		if(this.semesterDate.sNumber == SemesterDate.MAYX ){
			overloadLimit = 2;
		}
	}
	
	/////////////////////////
	/////////////////////////
	////Getters and setters
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GettersAndSetters_________;


	public ArrayList<ScheduleElement> getElements(){
		return elements;
	}

	public SemesterDate getDate(){
		return semesterDate;
	}
	
	/**
	 * Return the total number of credit hours scheduled in this semester
	 * @return
	 */
	public int getCreditHours(){
		int totalHours = 0;
		for(ScheduleElement e : this.elements){
			totalHours += e.getCreditHours();
		}
		return totalHours;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	

	public boolean hasNotes() {
		return this.notes != null;
	}

	public boolean isStudyAway() {
		return studyAway;
	}

	public void setStudyAway(boolean studyAway) {
		this.studyAway = studyAway;
	}
	

	public int getOverloadLimit() {
		return overloadLimit;
	}

	public void setOverloadLimit(int overloadLimit) {
		this.overloadLimit = overloadLimit;
	}
	

	/**
	 * Check whether this is a 'taken' semester, meaning
	 * it cannot be edited.
	 * @return
	 */
	public boolean isTaken() {
		if(this.semesterDate.compareTo(this.schedule.currentSemester)<0){
			return true;
		}
		else{
			return false;
		}
	}
	
	/////////////////////////
	/////////////////////////
	////Overlap
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Overlap_________;


	/**
	 * Check if addition overlaps with any elements in this semester
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
	 * Find the courses (not other schedule elements) in this.elements
	 * @return
	 */
	public ArrayList<ScheduleCourse> allCourses(){
		ArrayList<ScheduleCourse> courses = new ArrayList<ScheduleCourse>();
		for (ScheduleElement e : this.elements){
			if(e instanceof ScheduleCourse){
				courses.add((ScheduleCourse)e);
			}
		}
		return courses;
	}

	

	/////////////////////////
	/////////////////////////
	////Overload
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Overload_________;

	/**
	 * Check if adding addition would make an overload error.
	 *  If addition is null, check if the semester already surpasses its overload limit.
	 *  If addition is not null, check if addition itself causes the 
	 *      semester to surpass its overload limit.
	 * @param skipUserOverride  don't make any popups.
	 * @param addition  the ScheduleElement you are adding to your semester. May be null.
	 * @return null for no error, or else the error.
	 */
	public ScheduleError checkOverload(ScheduleElement addition){
		int totalHours = getCreditHours();
		if(addition == null){
			if(totalHours > overloadLimit){
				return makeOverloadError(addition);
			}
			else{
				return null;
			}
		}
		else{
			//if addition caused the overload limit to be exceeded
			if(totalHours <= overloadLimit 
					&& totalHours + addition.getCreditHours() > overloadLimit){
				return makeOverloadError(addition);
			}
			else{
				return null;
			}
		}
	}

	/**
	 * Check if this replacement causes an overload error.
	 * Does not accept null arguments.
	 * If the replacement causes no overload issues, this method returns null.
	 * @param newElement
	 * @param oldElement
	 * @return
	 */
	private ScheduleError checkReplaceOverload(
			ScheduleElement newElement, 
			ScheduleElement oldElement) {
		int totalHours = getCreditHours() - oldElement.getCreditHours();
		if(totalHours > overloadLimit){
			return null;
		}
		totalHours= totalHours + newElement.getCreditHours();
		if(totalHours > overloadLimit){
			return  makeOverloadError(newElement);
		}
		return null;
	}
	
	private ScheduleError makeOverloadError(ScheduleElement e){
		ScheduleError overload = new ScheduleError(ScheduleError.overloadError);
		overload.setOverloadLimit(this.overloadLimit);
		if(e != null){
			overload.setOffendingCourse(e);
		}
		overload.setOffendingSemester(this);
		return overload;
	}
	
	






	public int compareTo(Semester other){
		return this.semesterDate.compareTo(other.semesterDate);
	}


	
	/////////////////////////
	/////////////////////////
	//// Stateful Modifiers
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___AddRemoveReplace_________;
	
	/**
	 * Add a schedule element to this semester.
	 * This method should only be called by Schedule.
	 * @param e
	 * @return
	 */
	public boolean add(ScheduleElement e){
		ScheduleError overloadError = this.checkOverload(e);
		if(overloadError == null || this.schedule.userOverride(overloadError)){
			this.elements.add(e);
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Add this element without any updates or
	 * error checking
	 * @param e
	 * @return
	 */
	protected boolean directAdd(ScheduleElement e){
		return this.elements.add(e);
	}






	public boolean remove(ScheduleElement e){
		//this.schedule.checkErrorsWhenRemoving(e, this);
		this.elements.remove(e);
		return true;
	}




	public boolean replace(ScheduleElement oldElement, ScheduleElement newElement){
		ScheduleError overload = this.checkReplaceOverload(newElement, oldElement);
		if(overload != null && (!this.schedule.userOverride(overload))){
			return false;
		}
		else{
			int i = this.elements.indexOf(oldElement);
			this.elements.set(i, newElement);
			return true;
		}
	}




	/**
	 * Find the list of all courses with this semesterDate that also 
	 * satisfy r
	 * @param r
	 * @return
	 */
	/*
	public ArrayList<ScheduleCourse> getCoursesSatisfying(Requirement r){
		ArrayList<Course> semesterCourses =CourseList.getCoursesIn(this);
		ArrayList<Course> finalCourse = CourseList.onlyThoseSatisfying(semesterCourses, r);
		ArrayList<ScheduleCourse> coursesSatisfying = new ArrayList<ScheduleCourse>();
		for(Course c : finalCourse){
			ScheduleCourse s = new ScheduleCourse(c, this.schedule);
			coursesSatisfying.add(s);
		}
		return coursesSatisfying;
	}
*/

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






}