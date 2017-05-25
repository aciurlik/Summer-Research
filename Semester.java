import java.util.ArrayList;


public class Semester implements Comparable<Semester>{
	public SemesterDate semesterDate;
	public ArrayList<ScheduleElement> elements;
	public Schedule schedule;

	public Semester(SemesterDate sD, Schedule s){
		elements = new ArrayList<ScheduleElement>();
		this.semesterDate = sD;
		this.schedule = s;
	}

	public ArrayList<ScheduleElement> getElements(){
		return elements;
	}

	public SemesterDate getDate(){
		return semesterDate;
	}

	/**
	 * Check for any overlap among the Courses in this semester.
	 * If addition is not equal to null, check if there would be
	 *   overlap were addition to be added.
	 * 
	 * @param addition
	 */
	public void checkOverlap(ScheduleElement addition){
		//only courses can have overlap.
		ArrayList<Course> courses = new ArrayList<Course>();
		for (ScheduleElement e : this.elements){
			if(e instanceof Course){
				courses.add((Course)e);
			}
		}
		if(addition != null && (addition instanceof Course)){
			courses.add((Course) addition);
		}
		for (int i = 0; i < courses.size() ; i ++){
			for(int j = i; j < courses.size() ; j ++){
				if(courses.get(i).overlaps(courses.get(j))){
					throw new OverlapException(elements.get(i), elements.get(j));
				}
			}
		}
	}

	public int compareTo(Semester other){
		return this.semesterDate.compareTo(other.semesterDate);
	}
	/**
	 * Add a schedule element to this semester.
	 * This semester will handle error checks and changes in the Schedule.
	 * @param e
	 * @return
	 */
	public boolean add(ScheduleElement e){
		this.schedule.checkErrorsWhenAdding(e,this);
		this.elements.add(e);
		this.schedule.added(e, this);
		return true;

	}

	public ArrayList<Course> getCoursesSatisfying(Requirement r){
		ArrayList<Course> semesterCourses = this.schedule.masterList.getCoursesIn(this);
		return this.schedule.masterList.onlyThoseSatisfying(semesterCourses, r);
	}

	public boolean remove(ScheduleElement e){
		this.schedule.checkErrorsWhenRemoving(e, this);
		this.elements.remove(e);
		this.schedule.removed(e,this);
		return true;
	}
}
