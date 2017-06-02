
import java.util.ArrayList;


public class Semester implements Comparable<Semester>{
	public SemesterDate semesterDate;
	public ArrayList<ScheduleElement> elements;
	public Schedule schedule;
	private int OverloadLimit;
	
	
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
					//throw new OverlapException(elements.get(i), elements.get(j));
				}
			}
		}
	}
	
	public void checkOverload(ScheduleElement addition){
		if(this.semesterDate.sNumber == SemesterDate.FALL || this.semesterDate.sNumber == SemesterDate.SPRING){
			OverloadLimit = 20;
		}
		if(this.semesterDate.sNumber == SemesterDate.SUMMERONE|| this.semesterDate.sNumber == SemesterDate.SUMMERTWO){
			OverloadLimit = 8;
		}
		if(this.semesterDate.sNumber == SemesterDate.MAYX ){
			OverloadLimit = 4;
		}
		int totalHours = 0;
		
		if(addition instanceof Course){
			Course toAdd = (Course) addition;
			totalHours= totalHours + toAdd.getCreditHours();
		}
		
		for(ScheduleElement e : this.elements){
			if(e instanceof Course){
				totalHours = totalHours + ((Course) e).getCreditHours();
			}
		}
		if (totalHours > OverloadLimit){
			throw new OverloadException();
		}
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
		this.checkOverload(e);
		//this.schedule.checkErrorsWhenAdding(e,this);
		this.elements.add(e);
		//this.schedule.added(e, this);
		return true;

	}
	public boolean remove(ScheduleElement e){
		//this.schedule.checkErrorsWhenRemoving(e, this);
		this.elements.remove(e);
		return true;
	}
	
	
	
	
	public boolean replace(ScheduleElement oldElement, ScheduleElement newElement){
		this.checkOverload(newElement);
		//this.schedule.checkErrorsWhenAdding(newElement, this);
		//this.schedule.checkErrorsWhenRemoving(oldElement, this);
		int i = this.elements.indexOf(oldElement);
		this.elements.set(i, newElement);
		return true;
	}
	

	public ArrayList<Course> getCoursesSatisfying(Requirement r){
		ArrayList<Course> semesterCourses = this.schedule.masterList.getCoursesIn(this);
		return this.schedule.masterList.onlyThoseSatisfying(semesterCourses, r);
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


}
