import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class Schedule {
	ArrayList<Major> majorsList;
	ArrayList<Requirement> reqsList;
	ArrayList<Semester> semesters;
	
	CourseList masterList;
	
	boolean reqListValid;
	
	public static Schedule testSchedule(){

		CourseList l = CourseList.testList();
		Schedule result = new Schedule(l);

		SemesterDate one= new SemesterDate(2018,SemesterDate.FALL);
		SemesterDate two = new SemesterDate(2019, SemesterDate.SPRING);
		Semester a = new Semester(one, result);
		Semester b = new Semester(two, result);
		result.semesters.add(a);
		
		//result.semesters.add(b);
		
		return result;
	}
	
	public Schedule(CourseList masterList){
		this.majorsList= new ArrayList<Major>();
		this.semesters = new ArrayList<Semester>();
		this.reqsList = new ArrayList<Requirement>();
		reqListValid = false;
		this.masterList = masterList;
		
	}
	
	
	public Semester addNewSemester(){
		SemesterDate last = semesters.get(semesters.size() - 1).getDate();
		Semester next = new Semester(last.next(), this);
		this.semesters.add(next);
		return next;
	}
	
	
	public void checkErrorsWhenAdding(ScheduleElement e, Semester s){
		checkPrerequsites(e, s.semesterDate);
		s.checkOverlap(e);
		checkDuplicates(e);
	}
	public void added(ScheduleElement e, Semester s){
		if (e instanceof Course){
			updateRequirementsSatisfied((Course)e);
		}
		for (Requirement r : e.getRequirementsFulfilled()){
			r.numFinished ++;
		}
		
	}
	public void checkErrorsWhenRemoving(ScheduleElement e, Semester s){
		
	}
	public void removed(ScheduleElement e, Semester s){
		for (Requirement r : e.getRequirementsFulfilled()){
			r.numFinished -= 1;
		}
	}
	
	public ArrayList<ScheduleElement> allElements(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
			result.addAll(s.getElements());
		}
		return result;
	}
	
	
	/**
	 * Check every element to see if it has all the prereqs it needs.
	 */
	public void checkAllPrerequsites(){
		for(Semester s : semesters){
			for (ScheduleElement e : s.getElements()){
				checkPrerequsites(e, s.semesterDate);
			}
		}
	}
	public void checkPrerequsites(ScheduleElement e, SemesterDate sD){
		Prefix p = e.getPrefix();
		if(p == null){
			return;
		}
		else{
			HashSet<Prefix> needed = masterList.getPrerequsites(p);
			for(Semester s : semesters){
				if(s.semesterDate.compareTo(sD) < 1){
					for (ScheduleElement earlier : s.getElements()){
						Prefix earlierPrefix = earlier.getPrefix();
						if(earlierPrefix != null){
							if(needed.contains(earlierPrefix)){
								needed.remove(earlierPrefix);
							}
						}
					}
				}
			}
			if(! needed.isEmpty()){
				throw new PrerequsiteException(needed, e);
			}
			
		}
	}
	
	
	
	/**
	 * Check all semesters for duplicates.
	 * If one is found, throw an exception.
	 */
	public void checkDuplicates(){
		ArrayList<ScheduleElement> elements = allElements();
		for (int i = 0; i < elements.size() ; i ++){
			checkDuplicates(elements.get(i));
		}
	}
	public void checkDuplicates(ScheduleElement e){
		for(ScheduleElement e1 : allElements()){
			if(e1 == e){
				continue;
			}
			if(e1.isDuplicate(e) || e.isDuplicate(e1)){
				throw new DuplicateException(e1, e);
			}
		}
	}
	
	
	

	
	/**
	 * Check all semesters to see if any elements in them
	 * have overlapping times (are taken at the same time)
	 */
	public void checkOverlap(){
		for(Semester s : semesters){
			s.checkOverlap(null);
		}
	}
	
	
	
	/**
	 * Forces a full update of all of the requirements
	 *  based on the majors currently included 
	 *  and the courses currently taken 
	 *  
	 *  Assumes that all courses have up-to-date requirementsSatisfied lists.
	 */
	public void updateReqList(){
		for(Requirement r : reqsList){
			r.numFinished = 0;
		}
		for(Semester s : semesters){
			for(ScheduleElement e : s.getElements()){
				//update all requirements satisfied by this schedule element.
				for (Requirement r : getRequirementsSatisfied(e)){
					r.numFinished ++;
				}
			}
		}
	}
	
	public ArrayList<Requirement> getRequirementsSatisfied(ScheduleElement e){
		return e.getRequirementsFulfilled();
	}
	
	/**
	 * Tells this course the list of requirements that it satisfied.
	 * 		this list is a subset of the requirements implied by the set
	 * 		of majors.
	 * @param c
	 */
	public void updateRequirementsSatisfied(Course c){
		c.clearReqsSatisfied();
		ArrayList<Requirement> allSatisfied = new ArrayList<Requirement>();
		for(Requirement r : reqsList){
			if(r.isSatisfiedBy(c)){
				allSatisfied.add(r);
			}
		}
		//We don't want to add any requirement with a double dip issue, those
		// should be handled by the user.
		HashSet<Integer> doubleDipIssues = new HashSet<Integer>();
		HashSet<Integer> doubleDipNumbers = new HashSet<Integer>();
		for(Requirement r : allSatisfied){
			if(! doubleDipNumbers.add(r.doubleDipNumber)){
				doubleDipIssues.add(r.doubleDipNumber);
			}
		}
		//allow any doubleDip numbers that use the default DDN
		// because you're allowed to double dip from requirements with the 
		// default number.
		for(Integer i : doubleDipIssues){
			if(i%Major.MajorDDNRange == Requirement.defaultDDN){
				doubleDipIssues.remove(i);
			}
		}
		//Any time there's a question of which requirement a course should satisfy,
		// ignore it and leave it up to the user (and the GUI) to specify.
		for (Requirement r : allSatisfied){
			if(! doubleDipIssues.contains(r.doubleDipNumber)){
				c.satisfies(r);
			}
		}
	}
	
	
	/**
	 * Go through the list of courses and tell each one
	 * which current requirements it can satisfy.
	 */
	public void updateAllCourseRequirementsSatisfied(){
		for(Semester s : semesters){
			for(ScheduleElement e : s.getElements()){
				if(e instanceof Course){
					e = (Course)e;
					updateRequirementsSatisfied((Course)e);
				}
			}
		}
	}
	
	
	public ArrayList<Requirement> getRequirementsList(){
		if(! reqListValid){
			updateReqList();
		}
		return reqsList;
	}
	
	public void addMajor(Major newMajor){
		//Use sets to prevent duplicate requirements.
		HashSet<Requirement> set = new HashSet<Requirement>();
		majorsList.add(newMajor);
		for (Major m : majorsList){
			for (Requirement r : m.reqList){
				set.add(r);
			}
		}
		reqsList = new ArrayList<Requirement>(set);
		Collections.sort(reqsList);
		//Tell the courses which requirements they satisfy
		updateAllCourseRequirementsSatisfied();
		//Tell the new requirements if some taken course satisfies them.
		updateReqList();
	}
	
	public int getCreditHoursComplete(){
		int result = 0;
		for (ScheduleElement e : allElements()){
			if(e instanceof Course){
				result += ((Course)e).creditHours;
			}
		}
		return result;
	}
}